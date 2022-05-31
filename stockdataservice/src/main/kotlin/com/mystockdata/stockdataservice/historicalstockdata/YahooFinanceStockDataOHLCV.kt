package com.mystockdata.stockdataservice.historicalstockdata

import java.math.BigDecimal
import java.util.*

data class YahooFinanceStockDataOHLCV(
    val symbol: String,
    val date: Date,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val adjClose: BigDecimal,
    val volume: Int
)
