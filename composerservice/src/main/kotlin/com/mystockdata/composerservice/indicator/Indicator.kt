package com.mystockdata.composerservice.indicator

import java.math.BigDecimal
import java.time.Instant

data class Indicator(
    val time: Instant,
    val symbol: String,
    val indicatorName: IndicatorName,
    val indicatorType: IndicatorType,
    val value: BigDecimal?
)

data class RequestedIndicator(val indicatorName: IndicatorName, val indicatorType: IndicatorType)

/**
 * Splits a list of Pair<IndicatorName, IndicatorType> into a list of names.
 * @return a pair containing a list of all technical indicators names and a list containing all fundamental indicator names.
 */
fun List<RequestedIndicator>.splitByType(): Pair<List<IndicatorName>, List<IndicatorName>>{
    val technicalIndicators = mutableListOf<IndicatorName>()
    val fundamentalIndicators = mutableListOf<IndicatorName>()

    forEach {
        when(it.indicatorType){
            IndicatorType.TECHNICAL_INDICATOR -> technicalIndicators.add(it.indicatorName)
            IndicatorType.FUNDAMENTAL_INDICATOR -> fundamentalIndicators.add(it.indicatorName)
        }
    }

    return Pair(technicalIndicators, fundamentalIndicators)
}

/**
 * Names of indicators.
 * @property SMA Simple Moving Average
 * @property PE_RATIO Price to earnings ratio.
 */
enum class IndicatorName(
    val indicatorName: String
)  {
    SMA("SMA"),
    PE_RATIO("PER")
}

enum class IndicatorType{
    TECHNICAL_INDICATOR, FUNDAMENTAL_INDICATOR
}