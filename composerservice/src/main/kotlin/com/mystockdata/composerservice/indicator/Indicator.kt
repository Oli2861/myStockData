package com.mystockdata.composerservice.indicator

import java.math.BigDecimal
import java.time.Instant

data class Indicator(
    val time: Instant,
    val symbol: String,
    val indicatorName: IndicatorName,
    val indicatorType: IndicatorType,
    val value: BigDecimal
)

/**
 * Names of indicators.
 * @property SMA Simple Moving Average
 * @property PE_RATIO Price to earnings ratio.
 */
enum class IndicatorName(
    val indicatorName: String
)  {
    SMA("SMA"),
    PE_RATIO("PER"),
    EPS("EPS")
}

enum class IndicatorType{
    TECHNICAL_INDICATOR, FUNDAMENTAL_INDICATOR
}