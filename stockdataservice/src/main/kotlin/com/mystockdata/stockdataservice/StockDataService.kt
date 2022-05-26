package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.stockdataevent.StockDataEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StockDataService {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StockDataService::class.java)
    }

    fun handleEvent(stockDataEvent: StockDataEvent) {
        //TODO: Implement function
    }

}