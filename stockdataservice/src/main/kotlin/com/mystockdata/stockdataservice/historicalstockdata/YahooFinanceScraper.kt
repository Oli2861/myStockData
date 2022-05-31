package com.mystockdata.stockdataservice.historicalstockdata

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.text.SimpleDateFormat
import java.time.*
import java.util.*

@Component
class YahooFinanceScraper(
    @Autowired val webClient: WebClient
) {
    val formatter = SimpleDateFormat("yyyy-MM-dd")

    companion object {
        private val logger = LoggerFactory.getLogger(YahooFinanceScraper::class.java)
    }

    suspend fun retrieveAggregatedHistoricalStockData(symbols: List<String>): List<List<YahooFinanceStockDataOHLCV>?> =
        symbols.mapNotNull { retrieveHistoricalStockData(it) }

    /**
     *  @param stockSymbol
     *  @param startDate
     *  @param endDate
     *  @return List of the retrieved stock data.
     */
    suspend fun retrieveHistoricalStockData(
        stockSymbol: String,
        startDate: LocalDate = LocalDate.now().minusMonths(1),
        endDate: LocalDate = LocalDate.now()
    ): List<YahooFinanceStockDataOHLCV>? {
        // Transform dates.
        val adjustedStartTimeStamp = Date.from(startDate.atStartOfDay().toInstant(ZoneOffset.UTC)).time / 1000
        val adjustedEndTimeStamp = Date.from(endDate.atStartOfDay().toInstant(ZoneOffset.UTC)).time / 1000
        logger.trace("Retrieving historical stock data for $stockSymbol ranging from $startDate to $endDate")

        val csvResponse = webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/v7/finance/download/$stockSymbol")
                    .queryParam("period1", adjustedStartTimeStamp)
                    .queryParam("period2", adjustedEndTimeStamp)
                    .queryParam("interval", "1d")
                    .queryParam("events", "history")
                    .queryParam("includeAdjustedClose", true)
                    .build()
            }.retrieve()
            .awaitBody<String>()

        val list = retrieveDataFromCSV(csvResponse, stockSymbol)
        logger.trace("Retrieved stock data $list")

        return list
    }

    /**
     * Transforms a CSV String into a list of the contained stock data.
     * @param csvString String containing the comma separated data.
     * @param stockSymbol Symbol of the associated stock.
     * @return List of the retrieved stock data.
     */
    private fun retrieveDataFromCSV(csvString: String, stockSymbol: String): List<YahooFinanceStockDataOHLCV>? {
        val list = mutableListOf<YahooFinanceStockDataOHLCV>()
        val responseSplitByLines = csvString.lines()

        val headerLine = responseSplitByLines.firstOrNull() ?: return null
        if (headerLine != "Date,Open,High,Low,Close,Adj Close,Volume") return null
        // Remove list header
        val listBody = responseSplitByLines.subList(1, responseSplitByLines.size)

        for (line: String in listBody) {
            val properties = line.split(",")
            list.add(
                YahooFinanceStockDataOHLCV(
                    symbol = stockSymbol,
                    date = formatter.parse(properties[0]),
                    open = properties[1].toBigDecimal(),
                    high = properties[2].toBigDecimal(),
                    low = properties[3].toBigDecimal(),
                    close = properties[4].toBigDecimal(),
                    adjClose = properties[5].toBigDecimal(),
                    volume = properties[6].toInt()
                )
            )
        }
        return list
    }
}