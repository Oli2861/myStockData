package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedInformationProvider
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformation
import com.mystockdata.stockdataservice.persistence.AggregatedPriceInformationRepository
import com.mystockdata.stockdataservice.persistence.PrecisePriceInformationRepository
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformation
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationProvider
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationResponse
import com.mystockdata.stockdataservice.stockdataevent.StockDataEvent
import com.mystockdata.stockdataservice.stockdataevent.StockDataEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


@Service
class StockDataService(
    @Autowired private val aggregatedInformationProvider: AggregatedInformationProvider,
    @Autowired private val precisePriceInformationProvider: PrecisePriceInformationProvider,
    @Autowired private val precisePriceInformationRepository: PrecisePriceInformationRepository,
    @Autowired private val aggregatedPriceInformationRepository: AggregatedPriceInformationRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StockDataService::class.java)
    }

    /**
     * Handles incoming events from scheduling service.
     * @param stockDataEvent Received event.
     */
    suspend fun handleEvent(stockDataEvent: StockDataEvent) {

        when (stockDataEvent.stockDataEventType) {
            StockDataEventType.RETRIEVE_DAILY_OHLCV -> retrieveAggregatedInformationForDays(listOf("SAP.DE", "TSLA", "AMC", "GE", "ADS.DE", "ALV.DE", "BMW.DE", "SIE.DE", "PAH3.DE"), 1)
            StockDataEventType.RETRIEVE_HISTORIC_AGGREGATED_OHLCV -> retrieveAggregatedInformationForMonths(listOf("SAP.DE", "TSLA", "AMC", "GE", "ADS.DE", "ALV.DE", "BMW.DE", "SIE.DE", "PAH3.DE"), 1)
            StockDataEventType.START_RETRIEVING_LIVE_STOCK_DATA -> startRetrievingPrecisePriceInformation(listOf("SAP.DE", "TSLA", "AMC", "GE", "ADS.DE", "ALV.DE", "BMW.DE", "SIE.DE", "PAH3.DE"))
            StockDataEventType.STOP_RETRIEVING_LIVE_STOCK_DATA -> precisePriceInformationProvider.close()
        }

    }

    suspend fun retrieveAggregatedInformationForDays(symbols: List<String>, days: Long) =
        retrieveAggregatedPriceInformation(symbols, Instant.now().minus(days, ChronoUnit.DAYS), Instant.now())

    suspend fun retrieveAggregatedInformationForMonths(symbols: List<String>, months: Long) =
        retrieveAggregatedPriceInformation(symbols, ZonedDateTime.now().minusMonths(months).toInstant(), Instant.now())

    /**
     * Retrieves aggregated Price Information for all stocks on the watchlist.
     * @param start Start of the time window.
     * @param end End of the time window.
     * @return Flow containing the retrieved aggregated price information.
     */
    suspend fun retrieveAggregatedPriceInformation(
        symbols: List<String>,
        start: Instant,
        end: Instant
    ): Flow<AggregatedPriceInformation> {
        val priceInformationFlow: Flow<AggregatedPriceInformation> =
            aggregatedInformationProvider.retrieveHistoricalStockData(symbols, start, end).flatten().asFlow()
        aggregatedPriceInformationRepository.writeAggregatedPriceInformation(priceInformationFlow)
        return priceInformationFlow
    }

    /**
     * Get a List containing aggregated Stock Price Information of given symbols and a given time window.
     * @param symbols Stock symbols of interest.
     * @param start Start of the time window.
     * @param end End of the time window.
     * @return List containing the aggregated price information.
     */
    suspend fun getAggregatedPriceInformation(
        symbols: List<String>, start: Instant, end: Instant
    ) = aggregatedPriceInformationRepository.readAggregatedPriceInformation(symbols, start, end)


    /**
     * Start retrieving PrecisePriceInformation from a Precise Price Information Provider.
     */
    suspend fun startRetrievingPrecisePriceInformation(symbols: List<String>): Flow<PrecisePriceInformation> {
        // TODO: Retrieve Watchlist
        scope.launch {
            precisePriceInformationProvider.establishConnection(symbols)
            precisePriceInformationRepository.writePrecisePriceInformation(precisePriceInformationProvider.flow)
        }
        return precisePriceInformationProvider.flow
    }


    /**
     * Get a List of aggregated Stock Price Information of given symbols and a given time window.
     * @param symbols Stock symbols of interest.
     * @param start Start of the time window.
     * @param end End of the time window.
     * @return List containing the desired price information.
     */
    suspend fun getPrecisePriceInformation(
        symbols: List<String>, start: Instant, end: Instant
    ): List<PrecisePriceInformationResponse> =
        precisePriceInformationRepository.readPrecisePriceInformation(symbols, start, end)

}
/*
For testing purposes:
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