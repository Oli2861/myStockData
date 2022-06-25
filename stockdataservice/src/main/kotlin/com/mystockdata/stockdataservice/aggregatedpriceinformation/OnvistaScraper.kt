package com.mystockdata.stockdataservice.aggregatedpriceinformation

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.math.BigDecimal
import java.util.*

/**
 * Retrieved info after scraping the onvista page of the desired stock for the first time.
 * @param redirectUrl The URL onvista redirects a user if he or she changes the selected trading place. Used to construct an url that is appendable by query parameters.
 * @param exchanges A list of the available trading places. Mapping their name to the onvista notation id.
 */

data class ScrapingInfo(val redirectUrl: String, val exchanges: Map<OnvistaExchange, String>)

private fun reduceToNumbers(string: String): String = string.replace(Regex("[^0-9.,]"), "")
private fun onVistaMonetaryStringToBigDecimal(string: String): BigDecimal = string.replace(",", ".").toBigDecimal()
private fun onVistaVolumeStringToInt(string: String): Int = reduceToNumbers(string).replace(".", "").toInt()

@Component
class OnvistaScraper(
    @Qualifier("onvistaWebClient") @Autowired val onVistaWebClient: WebClient
) {
    companion object {
        private val logger = LoggerFactory.getLogger(OnvistaScraper::class.java)
    }

    /**
     *  @param isinList List containing International Securities Identification Numbers (ISIN) of the securities.
     *  @param exchange Exchange to retrieve the data from.
     *  @return List containing the successfully retrieved stock data.
     */
    suspend fun retrieveStockDataForMultipleStocks(
        isinList: List<String>,
        exchange: OnvistaExchange
    ): List<OnvistaStockDataOHLCV> =
        isinList.mapNotNull { isin -> retrieveStockData(isin, exchange) }

    /**
     *  @param isinList List containing International Securities Identification Number (ISIN).
     *  @return List containing the successfully retrieved stock data.
     */
    suspend fun retrieveStockDataForMultipleStocks(isinList: List<String>): List<OnvistaStockDataOHLCV> =
        isinList.mapNotNull { retrieveStockData(it) }

    /**
     *  @param isin International Securities Identification Number of the security.
     *  @param onVistaExchange Exchange to retrieve the data from.
     *  @return Information about the historical price development of the stock (identified by the provided isin) on the corresponding exchange.
     */
    private suspend fun retrieveStockData(isin: String, onVistaExchange: OnvistaExchange? = null): OnvistaStockDataOHLCV? {
        val scrapingInfo = getRedirectUrlAndTradingPlace(isin) ?: return null
        // Map containing names of available exchanges
        val exchanges = scrapingInfo.exchanges
        // Url that is appendable using query parameters
        val url: String = scrapingInfo.redirectUrl
        // Remove baseUrl
        val onvistaSecurityIdentifier = url.removePrefix("https://www.onvista.de/aktien")

        logger.trace("Retrieved identifier for Isin $isin: $onvistaSecurityIdentifier")

        // Retrieve HTML using Spring WebClient.
        val response = onVistaWebClient.get()
            .uri { uriBuilder ->
                if (onVistaExchange == null) {
                    uriBuilder.path("/$onvistaSecurityIdentifier")
                        .build()
                } else {
                    uriBuilder.path("/$onvistaSecurityIdentifier")
                        .queryParam("notation", onVistaExchange.exchangeName)
                        .build()
                }
            }
            .retrieve()
            .awaitBody<String>()

        // Retrieve desired stock data.
        val document = Jsoup.parse(response)

        val stockDataOHLCV = getStockDataOHLCV(document, isin)
        logger.trace("Retrieved StockDataOHLCV: $stockDataOHLCV")

        return stockDataOHLCV
    }

    /**
     * Queries a document for OHLCV stock information.
     * @param document The Document to query.
     * @param isin The isin of the stock.
     * @return StockDataOHLCV information retrieved from the provided document.
     */
    private fun getStockDataOHLCV(document: Document, isin: String): OnvistaStockDataOHLCV? {
        val elementsWithHeaderAttribute = document.getElementsByAttribute("headers")

        var open: BigDecimal? = null
        var closePreviousDay: BigDecimal? = null
        var volume: Int? = null
        var high: BigDecimal? = null
        var low: BigDecimal? = null

        // The desired table rows all have an attribute named "headers".
        elementsWithHeaderAttribute.forEach { element ->
            when (element.attributes().get("headers")) {
                "dataOpen" -> open = onVistaMonetaryStringToBigDecimal(element.html())
                "dataClose" -> closePreviousDay = onVistaMonetaryStringToBigDecimal(element.html())
                "dataVolume" -> volume =
                    onVistaVolumeStringToInt(element.child(0).html())
                "dataHighLowDay" -> {
                    val list: List<String> = element.html().split("/").map { reduceToNumbers(it) }
                    high = onVistaMonetaryStringToBigDecimal(list[0])
                    low = onVistaMonetaryStringToBigDecimal(list[1])
                }
            }
        }

        return toStockDataOHLCV(isin, open, high, low, closePreviousDay, volume)
    }

    /**
     * Scrapes the onvista page of the provided isin for a list of available exchanges and the URL onvista redirects a user if he or she changes the selected trading place. Used to construct an url that is appendable by query parameters.
     * @param isin of the desired security.
     * @return URL path and a list of available exchanges. Null if retrieval of the url was unsuccessfully.
     */
    private suspend fun getRedirectUrlAndTradingPlace(isin: String): ScrapingInfo? {
        // Retrieve HTML.
        val response = onVistaWebClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/$isin")
                    .build()
            }
            .retrieve()
            .awaitBody<String>()

        val document = Jsoup.parse(response)

        // Query HTML for the desired URL.
        val url = getURL(document)

        // Query HTML for available exchanges.
        val exchanges = getExchanges(document)

        val scrapingInfo: ScrapingInfo? = url?.let { ScrapingInfo(it, exchanges) }
        logger.trace("Retrieved ScrapingInfo for $isin: ${scrapingInfo.toString()}")
        if (scrapingInfo == null) logger.debug("No ScrapingInfo found for $isin")
        return scrapingInfo
    }

    /**
     * Search the document for the element containing the URL onvista redirects a user if he or she changes the selected trading place. Used to construct an url that is appendable by query parameters.
     * @param document The document to query.
     * @return desired URL.
     */
    private fun getURL(document: Document): String? {
        return document.selectXpath("//link[@rel=\"canonical\"]")
            .first()
            ?.attributes()
            ?.get("href")
    }

    /**
     * Searches the document for exchanges and the ticker of the stock on that exchange.
     * @param document The document to query.
     * @return Map mapping the exchange (e.g. LS) to the ticker (e.g. SAP on LS).
     */
    private fun getExchanges(document: Document): MutableMap<OnvistaExchange, String> {
        val exchanges: MutableMap<OnvistaExchange, String> = EnumMap(OnvistaExchange::class.java)
        // List containing information about exchanges.
        val retrievedExchangeList = document.selectXpath("//*[@id=\"exchangesLayer\"]/ul")
        retrievedExchangeList.first()?.children()?.forEach { li ->
            // The desired information is located within an "a" HTML-Tag.
            val aElement: Element? = li.getElementsByTag("a").firstOrNull()
            // The notationId can be retrieved from the href attribute of the a-Tag.
            val notationId: String? = aElement?.attr("href")?.removePrefix("?notation=")
            // The name of the exchange can be found as a text node (child of aElement).
            val retrievedName: String? = aElement?.textNodes()?.firstOrNull()?.text()?.trim()
            // Identify exchange.
            val exchange: OnvistaExchange? = OnvistaExchange.values().firstOrNull { onVistaExchange ->
                retrievedName == onVistaExchange.exchangeName
            }

            if (exchange != null && notationId != null) {
                exchanges[exchange] = notationId
            }
        }

        return exchanges
    }

}