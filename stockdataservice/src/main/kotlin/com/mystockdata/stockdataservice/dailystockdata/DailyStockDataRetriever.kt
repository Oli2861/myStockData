package com.mystockdata.stockdataservice.dailystockdata

import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.math.BigDecimal

private fun reduceToNumbers(string: String): String = string.replace(Regex("[^0-9.,]"), "")
private fun onVistaMonetaryStringToBigDecimal(string: String): BigDecimal = string.replace(",", ".").toBigDecimal()
private fun onVistaVolumeStringToInt(string: String): Int = reduceToNumbers(string).replace(".", "").toInt()

@Component
class DailyStockDataRetriever(
    @Autowired val onVistaWebClient: WebClient
) {
    companion object {
        private val logger = LoggerFactory.getLogger(DailyStockDataRetriever::class.java)
    }

    /**
     * Searches the Onvista page for the url, which can be appended with query parameters. Onvista redirects the user if another marketplace is selected.
     * @param Isin of the desired security.
     * @return URL that can be appended by query parameters.
     */
    suspend fun getRedirectUrl(isin: String): String? {
        val response = onVistaWebClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/$isin")
                    .build()
            }
            .retrieve()
            .awaitBody<String>()
        val document = Jsoup.parse(response)
        return document.selectXpath("//link[@rel=\"canonical\"]")
            .first()
            ?.attributes()
            ?.get("href")
    }

    /**
     *  @param isin International Securities Identification Number of the security.
     *  @param tradingPlaceId Id of the trading place according to Onvista.
     */
    suspend fun retrieveStockData(isin: String, tradingPlaceId: Int = 22031): StockDataOHLCV? {
        // Get url that is appendable using query parameters
        val url: String = getRedirectUrl(isin) ?: return null
        // Remove baseUrl (https://www.onvista.de/aktien/SAP-Aktie-DE0007164600 => /SAP-Aktie-DE0007164600)
        val onvistaSecurityIdentifier = url.removePrefix("https://www.onvista.de/aktien")

        // Retrieve HTML using Spring WebClient
        val response = onVistaWebClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/$onvistaSecurityIdentifier")
                    .queryParam("notation", tradingPlaceId)
                    .build()
            }
            .retrieve()
            .awaitBody<String>()

        // Extract data
        val document = Jsoup.parse(response)
        val elementsWithHeaderAttribute = document.getElementsByAttribute("headers")

        var open: BigDecimal? = null
        var closePreviousDay: BigDecimal? = null
        var volume: Int? = null
        var high: BigDecimal? = null
        var low: BigDecimal? = null

        // The desired table rows all have an attribute named "headers"
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
        val stockDataOHLCV = toStockDataOHLCV(open, high, low, closePreviousDay, volume)
        logger.debug("Retrieved StockDataOHLCV: $stockDataOHLCV")
        return stockDataOHLCV
    }
}


suspend fun main() {
    val retriever = DailyStockDataRetriever(OnVistaWebClientConfig().onVistaWebClient())
    val retrievedData = retriever.retrieveStockData("DE0007164600")
    println(retrievedData)
}