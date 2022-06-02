package com.mystockdata.schedulingservice.stockdataevent

data class StockDataEvent(val correlationId: String, val stockDataEventType: StockDataEventType)
enum class StockDataEventType{
    RETRIEVE_DAILY_OHLCV, RETRIEVE_HISTORIC_AGGREGATED_OHLCV, START_RETRIEVING_LIVE_STOCK_DATA, STOP_RETRIEVING_LIVE_STOCK_DATA
}