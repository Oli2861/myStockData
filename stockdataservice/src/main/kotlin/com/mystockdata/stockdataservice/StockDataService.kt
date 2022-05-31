package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.dailystockdata.OnvistaExchange
import com.mystockdata.stockdataservice.dailystockdata.OnvistaScraper
import com.mystockdata.stockdataservice.historicalstockdata.YahooFinanceScraper
import com.mystockdata.stockdataservice.historicalstockdata.YahooFinanceWebClient
import com.mystockdata.stockdataservice.stockdataevent.StockDataEvent
import com.mystockdata.stockdataservice.stockdataevent.StockDataEventType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StockDataService(
    @Autowired val yahooFinanceScraper: YahooFinanceScraper,
    @Autowired val onvistaScraper: OnvistaScraper
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StockDataService::class.java)
    }

    /**
     * Handles incoming events from scheduling service.
     */
    suspend fun handleEvent(stockDataEvent: StockDataEvent) {

        when (stockDataEvent.stockDataEventType) {
            StockDataEventType.RETRIEVE_DAILY_OHLCV -> retrieveDailyStockDataFromOnvista()
            StockDataEventType.RETRIEVE_HISTORIC_AGGREGATED_OHLCV -> retrieveAggregatedHistoricalStockDataFromYahooFinance()
            else -> logger.error("Unknown event type received: $stockDataEvent")
        }
    }

    suspend fun retrieveDailyStockData() {

    }

    suspend fun retrieveAggregatedHistoricalStockDataFromYahooFinance() {
        // TODO: Retrieve Symbol list
        val list = listOf<String>("VOW3.DE", "SOW.DE", "SAP.DE")
        val result = yahooFinanceScraper.retrieveAggregatedHistoricalStockData(list)
        logger.debug("Data from Yahoo Finance $result")
    }

    /**
     *  Retrieve OHLCV stock information for a list of ISINs identifying stocks of interest.
     */
    suspend fun retrieveDailyStockDataFromOnvista(exchange: OnvistaExchange = OnvistaExchange.XETRA) {
        // TODO: Retrieve isin-list of stocks to be collected
        val list = listOf("DE0007164600", "DE000A2GS401")
        val result = onvistaScraper.retrieveStockDataForMultipleStocks(list, exchange)
        logger.trace("Retrieved stock information: $result")
    }

}