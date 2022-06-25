package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedInformationProvider
import com.mystockdata.stockdataservice.aggregatedpriceinformation.YahooFinanceScraper
import com.mystockdata.stockdataservice.aggregatedpriceinformation.YahooFinanceWebClientConfig
import com.mystockdata.stockdataservice.precisepriceinformation.PreciseInformationProvider
import com.mystockdata.stockdataservice.precisepriceinformation.YahooWebClientHandler
import com.mystockdata.stockdataservice.stockdataevent.StockDataEvent
import com.mystockdata.stockdataservice.stockdataevent.StockDataEventType
import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StockDataService(
    @Autowired private val aggregatedInformationProvider: AggregatedInformationProvider,
    @Autowired private val preciseInformationProvider: PreciseInformationProvider
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
            StockDataEventType.STOP_RETRIEVING_LIVE_STOCK_DATA -> preciseInformationProvider.closeConnection()
        }

    }

    suspend fun retrieveAggregatedInformationForYesterday(){
        // TODO
        val list = listOf("VOW3.DE", "SOW.DE", "SAP.DE")
        //val result = yahooFinanceScraper.retrieveAggregatedHistoricalStockDataForPastMonth(list)
        val result =
            aggregatedInformationProvider.retrieveAggregatedInformationForYesterday(list)
        logger.debug("Data from Yahoo Finance $result")
    }

    suspend fun retrieveAggregatedInformationForPastMonth() {
        // TODO: Retrieve Watchlist
        val list = listOf("VOW3.DE", "SOW.DE", "SAP.DE")
        val result = aggregatedInformationProvider.retrieveAggregatedInformationForPastMonth(list)
        logger.debug("Data from Yahoo Finance $result")
    }

    suspend fun startRetrievingPrecisePriceInformation() {
        // TODO: Retrieve Watchlist
        preciseInformationProvider.establishConnection(listOf("SAP.DE", "TSLA", "AMC"))
    }

}

suspend fun main() {
    val yahooFinanceScraper = YahooFinanceScraper(YahooFinanceWebClientConfig().yahooFinanceWebClient())
    val yahooWebClientHandler = YahooWebClientHandler()
    val stockDataService = StockDataService(yahooFinanceScraper, yahooWebClientHandler)
    //stockDataService.retrieveAggregatedInformationForYesterday()
    //stockDataService.retrieveAggregatedInformationForPastMonth()
    stockDataService.startRetrievingPrecisePriceInformation()
    delay(100L)
}