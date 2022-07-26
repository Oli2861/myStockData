package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedInformationProvider
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformation
import com.mystockdata.stockdataservice.persistence.AggregatedPriceInformationRepository
import com.mystockdata.stockdataservice.persistence.PrecisePriceInformationRepository
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationProvider
import com.mystockdata.stockdataservice.precisepriceinformation.PriceInformationResponse
import com.mystockdata.stockdataservice.stockdataevent.StockDataEvent
import com.mystockdata.stockdataservice.stockdataevent.StockDataEventType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.time.Instant
import java.time.LocalDate


@Service
class StockDataService(
    @Autowired private val aggregatedInformationProvider: AggregatedInformationProvider,
    @Autowired private val precisePriceInformationProvider: PrecisePriceInformationProvider,
    @Autowired private val precisePriceInformationRepository: PrecisePriceInformationRepository,
    @Autowired private val aggregatedPriceInformationRepository: AggregatedPriceInformationRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StockDataService::class.java)
    }

    /**
     * Handles incoming events from scheduling service.
     */
    suspend fun handleEvent(stockDataEvent: StockDataEvent) {

        when (stockDataEvent.stockDataEventType) {
            StockDataEventType.RETRIEVE_DAILY_OHLCV -> retrieveAggregatedInformationForYesterday()
            StockDataEventType.RETRIEVE_HISTORIC_AGGREGATED_OHLCV -> retrieveAggregatedInformationForPastMonth()
            StockDataEventType.START_RETRIEVING_LIVE_STOCK_DATA -> startRetrievingPrecisePriceInformation()
            StockDataEventType.STOP_RETRIEVING_LIVE_STOCK_DATA -> precisePriceInformationProvider.close()
        }

    }

    suspend fun retrieveAggregatedInformationForYesterday() {
        // TODO: Retrieve Watchlist
        val list = listOf("VOW3.DE", "SOW.DE", "SAP.DE")
        val priceInformationFlow: Flow<AggregatedPriceInformation> =
            aggregatedInformationProvider.retrieveAggregatedInformationForYesterday(list).asFlow()
        aggregatedPriceInformationRepository.writeAggregatedPriceInformation(priceInformationFlow)
    }

    suspend fun retrieveAggregatedInformationForPastMonth() {
        // TODO: Retrieve Watchlist
        val list = listOf("VOW3.DE", "SOW.DE", "SAP.DE")
        val priceInformationFlow: Flow<AggregatedPriceInformation> =
            aggregatedInformationProvider.retrieveAggregatedInformationForPastMonth(list)
                .flatten()
                .asFlow()
        aggregatedPriceInformationRepository.writeAggregatedPriceInformation(priceInformationFlow)
    }

    suspend fun retrieveAggregatedInformation(days: Int): Flow<AggregatedPriceInformation> {
        val list = listOf("VOW3.DE", "SOW.DE", "SAP.DE")
        val priceInformationFlow: Flow<AggregatedPriceInformation> =
            aggregatedInformationProvider.retrieveHistoricalStockData(
                list,
                LocalDate.now().minusDays(days.toLong()),
                LocalDate.now()
            )
                .flatten()
                .asFlow()
        aggregatedPriceInformationRepository.writeAggregatedPriceInformation(priceInformationFlow)
        return priceInformationFlow
    }

    suspend fun startRetrievingPrecisePriceInformation() {
        // TODO: Retrieve Watchlist
        precisePriceInformationProvider.establishConnection(listOf("SAP.DE", "TSLA", "AMC"))
        precisePriceInformationRepository.writePrecisePriceInformation(precisePriceInformationProvider.flow)
    }

    /**
     * Produces a CSV File containing precise price information of a list of given securities. Result data is down sampled into 5 Minute intervalls.
     * @param symbols List of symbols of the desired securities.
     * @param start Start instant
     * @param end End instant
     * @return File name and InputStreamResource to produce the CSV File.
     */
    suspend fun getPrecisePriceInformationCSV(
        symbols: List<String>,
        start: Instant,
        end: Instant
    ): Pair<String, InputStreamResource> {
        val data = precisePriceInformationRepository.readPrecisePriceInformation(symbols, start, end)
        val sortedPriceInfoResponses: Map<String, List<PriceInformationResponse>> =
            sortPriceInformationResponsesIntoMap(data)

        val csvHeader: Array<String> = arrayOf("timestamp") + sortedPriceInfoResponses.keys.toList().toTypedArray()
        val csvBody = toCSVBody(sortedPriceInfoResponses)

        return Pair("precisePriceInformation.csv", toCSV(csvHeader, csvBody))
    }

    /**
     * Sorts given PriceInformationResponses into a map with the symbols as keys.
     * @param data List to be sorted into the map.
     * @return Map mapping symbol to a list of all PriceInformationResponses
     */
    private suspend fun sortPriceInformationResponsesIntoMap(data: List<PriceInformationResponse>): Map<String, List<PriceInformationResponse>> {
        val map = mutableMapOf<String, MutableList<PriceInformationResponse>>()
        val timeStampColumn = data

        data.forEach { priceInformationResponse ->
            val symbol = priceInformationResponse.symbol
            if (map.keys.contains(symbol)) {
                map[symbol]?.add(priceInformationResponse)
            } else {
                map.putIfAbsent(symbol, mutableListOf(priceInformationResponse))
            }
        }

        return map
    }

    /**
     * Function to produce the rows of a csv file. The data must be down sampled to have a price value for each symbol for each timestamp.
     * @param map Map mapping symbol to all retrieved PriceInformationResponse objects.
     * @return csv body (List<List<String>>) containing rows (List<String>)
     */
    suspend fun toCSVBody(map: Map<String, List<PriceInformationResponse>>): List<List<String>> {
        // Time column by using the timestamps of the first entry
        val timeColumn: List<Instant> = map[map.keys.first()]?.map { it.time } ?: listOf()
        val priceColumns: List<List<PriceInformationResponse>> = map.values.toList()

        val csvBody: MutableList<List<String>> = mutableListOf()
        // Build rows based on columns.
        for ((index, time) in timeColumn.withIndex()) {
            // List containing the values of the current Row.
            val row = mutableListOf<String>()
            // The First column of every row is the timestamp.
            row.add(time.toString())
            for (priceColumn in priceColumns) {
                // Add all price columns to the current row.
                val curr = priceColumn[index]
                // If there is no price for the current period, write "null" instead.
                row.add(if (curr.price == null) "null" else curr.price.toString())
            }
            csvBody.add(row)
        }
        return csvBody
    }

    /**
     * Function to produce a csv based on lists containing the data.
     * @param csvBody Body of the csv.
     * @return InputStreamResource
     * As in https://codeburst.io/returning-csv-content-from-an-api-in-spring-boot-63ea82bbcf0f
     */
    private fun toCSV(csvHeader: Array<String>, csvBody: List<List<String>>): InputStreamResource {
        val byteArrayOutputStream = ByteArrayOutputStream().use { out ->
            CSVPrinter(PrintWriter(out), CSVFormat.DEFAULT.withHeader(*csvHeader)).use { csvPrinter ->
                csvBody.forEach { record ->
                    csvPrinter.printRecord(record)
                }
                csvPrinter.flush()
                ByteArrayInputStream(out.toByteArray())
            }
        }

        return InputStreamResource(byteArrayOutputStream)
    }

    suspend fun getAggregatedPriceInformationCSV(symbols: List<String>): List<AggregatedPriceInformation> {
        return aggregatedPriceInformationRepository.readAggregatedPriceInformation(symbols)
    }


}
/*
suspend fun main() {
    val yahooFinanceScraper = YahooFinanceScraper(YahooFinanceWebClientConfig().yahooFinanceWebClient())
    val yahooWebSocketClient = YahooWebSocketClient()
    val precisePriceInformationRepository = PrecisePriceInformationRepository(InfluxDBClientKotlinFactory.create("influxdb", "token".toCharArray(), "mystockdata", "stockdata"), "stockdata")
    val stockDataService = StockDataService(yahooFinanceScraper, yahooWebSocketClient, precisePriceInformationRepository)
    //stockDataService.retrieveAggregatedInformationForYesterday()
    //stockDataService.retrieveAggregatedInformationForPastMonth()
    stockDataService.startRetrievingPrecisePriceInformation()
    delay(100L)
}
*/