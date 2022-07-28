package com.mystockdata.stockdataservice.aggregatedpriceinformation

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant

@Component
class YahooFinanceScraper(
    @Qualifier("yahooFinanceWebClient") @Autowired val yahooFinanceWebClient: WebClient
) : AggregatedInformationProvider() {
    val formatter = SimpleDateFormat("yyyy-MM-dd")

    companion object {
        private val logger = LoggerFactory.getLogger(YahooFinanceScraper::class.java)
    }

    /**
     * Retrieves historical stock data from yahoo finance based on the endpoint https://query1.finance.yahoo.com/v7/finance/download/$stockSymbol .
     * @param stockSymbol Symbol identifying the stock on the exchange.
     * @param startDate Start of the time window.
     * @param endDate End of the time window.
     * @return Retrieved AggregatedPriceInformation.
     */
    override suspend fun retrieveHistoricalStockData(
        stockSymbol: String,
        startDate: Instant,
        endDate: Instant
    ): List<AggregatedPriceInformation>? {

        logger.trace("Retrieving historical stock data for $stockSymbol ranging from $startDate to $endDate")

        val csvResponse = yahooFinanceWebClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/v7/finance/download/$stockSymbol")
                    .queryParam("period1", startDate.toEpochMilli() / 1000)
                    .queryParam("period2", endDate.toEpochMilli() / 1000)
                    .queryParam("interval", "1d")
                    .queryParam("events", "history")
                    .queryParam("includeAdjustedClose", true)
                    .build()
            }.retrieve()
            .awaitBody<String>()

        val list = parseCSVString(csvResponse, stockSymbol)
        logger.trace("Retrieved stock data $list")

        return list
    }

    /**
     * Transforms a CSV String into a list of the contained stock data.
     * @param csvString String containing the comma separated data.
     * @param stockSymbol Symbol of the associated stock.
     * @return List of the retrieved stock data.
     */
    private fun parseCSVString(csvString: String, stockSymbol: String): List<AggregatedPriceInformation>? {

        val responseSplitByLines = csvString.lines()
        if (responseSplitByLines.isEmpty()) return null

        val headerLine = responseSplitByLines.firstOrNull() ?: return null
        // Check whether header ist existing and remove it
        if (headerLine != "Date,Open,High,Low,Close,Adj Close,Volume") return null
        val listBody = responseSplitByLines.subList(1, responseSplitByLines.size)

        val list = mutableListOf<AggregatedPriceInformation>()

        for (line: String in listBody) {
            val stockDataOHLCV = parseLine(line, stockSymbol)
            if (stockDataOHLCV != null){
                list.add(stockDataOHLCV)
            }else{
                logger.debug("Unable to parse line: $line")
            }
        }

        return if (list.isEmpty()) null else list
    }

    /**
     * Parses a line of retrieved data into a AggregatedPriceInformation object.
     * @param line Line to be parsed.
     * @param stockSymbol Symbol of the associated stock.
     * @return Instance of the AggregatedPriceInformation containing the retrieved data or null if an error occurs while parsing.
     */
    private fun parseLine(line: String, stockSymbol: String): AggregatedPriceInformation? {
        val properties = line.split(",")
        try {

            return AggregatedPriceInformation(
                symbol = stockSymbol,
                time = formatter.parse(properties[0]).toInstant(),
                open = properties[1].toBigDecimal(),
                high = properties[2].toBigDecimal(),
                low = properties[3].toBigDecimal(),
                close = properties[4].toBigDecimal(),
                adjClose = properties[5].toBigDecimal(),
                volume = properties[6].toInt()
            )

        } catch (e: Exception) {
            // Parsing failures are logged at trace level, other exceptions are logged at error level
            when (e) {
                is NumberFormatException -> logger.trace(e.stackTraceToString())
                is ParseException -> logger.trace(e.stackTraceToString())
                else -> logger.error(e.stackTraceToString())
            }
            return null
        }
    }
}
