package com.mystockdata.schedulingservice.stockdataevent

data class StockDataEvent(val correlationId: String, val stockDataEventType: StockDataEventType)
enum class StockDataEventType{
    TEST
}