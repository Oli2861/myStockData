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

/**
 * Splits a list of Pair<IndicatorName, IndicatorType> into a list of names.
 * @return a pair containing a list of all technical indicators names and a list containing all fundamental indicator names.
 */
fun List<Pair<IndicatorName, IndicatorType>>.splitByType(): Pair<List<IndicatorName>, List<IndicatorName>>{
    val technicalIndicators = mutableListOf<IndicatorName>()
    val fundamentalIndicators = mutableListOf<IndicatorName>()

    forEach { (name, type) ->
        when(type){
            IndicatorType.TECHNICAL_INDICATOR -> technicalIndicators.add(name)
            IndicatorType.FUNDAMENTAL_INDICATOR -> fundamentalIndicators.add(name)
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
    PE_RATIO("PER"),
    EPS("EPS")
}

enum class IndicatorType{
    TECHNICAL_INDICATOR, FUNDAMENTAL_INDICATOR
}