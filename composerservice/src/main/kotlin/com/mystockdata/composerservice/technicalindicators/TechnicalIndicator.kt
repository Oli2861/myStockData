package com.mystockdata.composerservice.technicalindicators

import com.mystockdata.composerservice.stockdata.AggregatedPriceInformation
import java.math.BigDecimal
import java.time.Instant

data class TechnicalIndicator(
    val time: Instant,
    val symbol: String,
    val type: TechnicalIndicatorName,
    val value: BigDecimal
)

enum class TechnicalIndicatorName(val indicatorName: String) {
    SMA("sma"),
    RSI("rsi"),
    MACD("macd")
}

/**
 * Splits a list of AggregatedPriceInformationResponse into multiple lists which contain all AggregatedPriceInformationResponses of a symbol.
 * @param data AggregatedPriceInformationResponses
 * @return List containing lists of AggregatedPriceInformationResponses for each symbol.
 */
fun splitBySymbol(data: List<AggregatedPriceInformation>): List<List<AggregatedPriceInformation>> {
    val symbols: List<String> = data.distinctBy { it.symbol }.map { it.symbol }
    val splitData: MutableList<List<AggregatedPriceInformation>> = mutableListOf()
    for (symbol in symbols) {
        val curr = mutableListOf<AggregatedPriceInformation>()
        data.filter { symbol == it.symbol }.forEach {
            curr.add(it)
        }
        splitData.add(curr)
    }
    return splitData
}