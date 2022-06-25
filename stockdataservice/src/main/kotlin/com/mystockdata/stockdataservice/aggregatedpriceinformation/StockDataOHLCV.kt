package com.mystockdata.stockdataservice.aggregatedpriceinformation

import java.math.BigDecimal
import java.util.*

data class StockDataOHLCV(
    val symbol: String,
    val date: Date,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val adjClose: BigDecimal,
    val volume: Int
)
