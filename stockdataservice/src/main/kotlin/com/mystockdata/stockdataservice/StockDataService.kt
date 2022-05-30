package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.dailystockdata.DailyStockDataRetriever
import com.mystockdata.stockdataservice.dailystockdata.OnVistaExchange
import com.mystockdata.stockdataservice.stockdataevent.StockDataEvent
import com.mystockdata.stockdataservice.stockdataevent.StockDataEventType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StockDataService(
    @Autowired val dailyStockDataRetriever: DailyStockDataRetriever
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StockDataService::class.java)
    }

    /**
     * Handles incoming events from scheduling service.
     */
    suspend fun handleEvent(stockDataEvent: StockDataEvent) {

        when (stockDataEvent.stockDataEventType) {
            StockDataEventType.RETRIEVE_DAILY_OHLCV -> retrieveDailyStockData()
            else -> logger.error("Unknown event type received: $stockDataEvent")
        }
    }

    /**
     *  Retrieve OHLCV stock information for a list of ISINs identifying stocks of interest.
     */
    suspend fun retrieveDailyStockData() {
        //TODO: Retrieve isin-list of stocks to be collected; TODO: Include ticker data?
        val list = listOf("DE0007164600", "DE000A2GS401")
        val result = dailyStockDataRetriever.retrieveStockDataForMultipleStocks(list, OnVistaExchange.XETRA)
        logger.debug("Retrieved stock information: $result")
    }

}