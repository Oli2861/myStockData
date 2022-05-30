package com.mystockdata.schedulingservice.stockdataevent

data class StockDataEvent(val correlationId: String, val stockDataEventType: StockDataEventType)
enum class StockDataEventType{
    RETRIEVE_DAILY_OHLCV
}