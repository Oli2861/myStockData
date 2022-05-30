package com.mystockdata.stockdataservice.stockdataevent

/**
 * Events incoming from scheduling service.
 */
data class StockDataEvent(val correlationId: String, val stockDataEventType: StockDataEventType)

/**
 * Specifies the type of StockDataEvent.
 */
enum class StockDataEventType{
    RETRIEVE_DAILY_OHLCV
}