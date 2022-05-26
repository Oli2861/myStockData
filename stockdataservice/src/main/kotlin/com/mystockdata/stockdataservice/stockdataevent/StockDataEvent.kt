package com.mystockdata.stockdataservice.stockdataevent

data class StockDataEvent(val correlationId: String, val stockDataEventType: StockDataEventType)
enum class StockDataEventType{
    TEST
}