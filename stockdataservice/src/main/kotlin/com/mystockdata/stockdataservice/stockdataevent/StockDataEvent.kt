package com.mystockdata.stockdataservice.stockdataevent

import java.time.Instant

/**
 * Events sent to the stock data service.
 */
data class StockDataEvent(
    val correlationId: String,
    val stockDataEventType: StockDataEventType,
    val start: Instant?,
    val end: Instant?,
    val symbols: Set<String>?
)

/**
 * Specifies the type of StockDataEvent.
 */
enum class StockDataEventType {
    RETRIEVE_AGGREGATED, START_RETRIEVING_PRECISE, STOP_RETRIEVING_PRECISE
}