package com.mystockdata.stockdataservice.aggregatedpriceinformation

import java.math.BigDecimal
import java.time.Instant

data class StockDataOHLCV(
    val symbol: String,
    val date: Instant,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val adjClose: BigDecimal,
    val volume: Int
)
