package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedInformationProvider
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformation
import com.mystockdata.stockdataservice.persistence.AggregatedPriceInformationRepository
import com.mystockdata.stockdataservice.persistence.PrecisePriceInformationRepository
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformation
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationProvider
import com.mystockdata.stockdataservice.stockdataevent.StockDataEvent
import com.mystockdata.stockdataservice.stockdataevent.StockDataEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class StockDataService(
    @Autowired private val aggregatedInformationProvider: AggregatedInformationProvider,
    @Autowired private val precisePriceInformationProvider: PrecisePriceInformationProvider,
    @Autowired private val precisePriceInformationRepository: PrecisePriceInformationRepository,
    @Autowired private val aggregatedPriceInformationRepository: AggregatedPriceInformationRepository
) {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

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
        val priceInformationFlow: Flow<AggregatedPriceInformation> = aggregatedInformationProvider.retrieveAggregatedInformationForYesterday(list).asFlow()
        aggregatedPriceInformationRepository.writeAggregatedPriceInformation(priceInformationFlow)
    }

    suspend fun retrieveAggregatedInformationForPastMonth() {
        // TODO: Retrieve Watchlist
        val list = listOf("VOW3.DE", "SOW.DE", "SAP.DE")
        val priceInformationFlow: Flow<AggregatedPriceInformation> = aggregatedInformationProvider.retrieveAggregatedInformationForPastMonth(list)
            .flatten()
            .asFlow()
        aggregatedPriceInformationRepository.writeAggregatedPriceInformation(priceInformationFlow)
    }

    suspend fun retrieveAggregatedInformation(days: Int): Flow<AggregatedPriceInformation> {
        val list = listOf("VOW3.DE", "SOW.DE", "SAP.DE")
        val priceInformationFlow: Flow<AggregatedPriceInformation> = aggregatedInformationProvider.retrieveHistoricalStockData(list, LocalDate.now().minusDays(days.toLong()), LocalDate.now())
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