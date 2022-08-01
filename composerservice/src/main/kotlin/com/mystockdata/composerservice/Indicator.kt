package com.mystockdata.composerservice

import com.mystockdata.composerservice.csv.CsvEntry
import java.math.BigDecimal
import java.time.Instant

abstract class Indicator {
    abstract val time: Instant
    abstract val symbol: String
    abstract val indicatorName: IndicatorName
    abstract val value: BigDecimal
}

enum class IndicatorName(
    val indicatorName: String
)  {
    SMA("SMA"),
    RSI("RSI"),
    MACD("MACD")
}