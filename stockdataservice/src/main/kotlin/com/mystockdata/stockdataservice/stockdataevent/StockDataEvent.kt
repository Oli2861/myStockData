package com.mystockdata.stockdataservice.stockdataevent

import java.time.Instant

data class StockDataEvent(
    val correlationId: String,
    val stockDataEventType: StockDataEventType,
    val start: Instant?,
    val end: Instant?
)

enum class StockDataEventType {
    RETRIEVE_AGGREGATED, START_RETRIEVING_PRECISE, STOP_RETRIEVING_PRECISE
}