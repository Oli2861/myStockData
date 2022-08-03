package com.mystockdata.composerservice.indicator

import com.mystockdata.composerservice.csv.CsvEntry
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

/**
 * Calculates simple moving average for all values of a given list of AggregatedPriceInformationResponses as far as possible.
 * @param data The AggregatedPriceInformationResponses. Can contain data for different symbols.
 * @param windowSize Size of the SAM-window.
 * @param fillMissingValue whether missing values should be filled with the previous one.
 * @return List containing lists of the calculated SMA for each symbol. Rounded to two decimal points.
 */
fun smaForMultipleSymbols(data: List<CsvEntry>, windowSize: Int = 14, fillMissingValue: Boolean = false): List<List<Indicator>>{
    val preparedData = if(fillMissingValue) TimeSeriesOperator.splitBySymbolAndFillMissingValues(data) else TimeSeriesOperator.splitBySymbol(data)
    return preparedData.map { listForASymbol -> smaForAllOfASymbol(listForASymbol, windowSize) }
}

/**
 * Calculates simple moving average for all values of a given list of AggregatedPriceInformationResponses as far as possible.
 * @param data The AggregatedPriceInformationResponses **with the same symbol**.
 * @param windowSize Size of the SAM-window.
 * @return list of the calculated SMA. Rounded to two decimal points.
 */
fun smaForAllOfASymbol(
    data: List<CsvEntry>,
    windowSize: Int = 14
): List<Indicator> {
    val smaList = mutableListOf<Indicator>()
    data.sortedByDescending { it.time }.forEach {
        smaList.add(calculateSMA(data, it.time, windowSize))
    }
    return smaList
}

/**
 * Calculates a simple moving average over the given data. The window starts at the element matching the instant and ends windowSize-elements later.
 * @param data The AggregatedPriceInformationResponses.
 * @param start Start instant to identify the start of the window.
 * @param windowSize Size of the SAM-window.
 * @return SMA rounded to two decimal points.
 */
fun calculateSMA(
    data: List<CsvEntry>,
    start: Instant,
    windowSize: Int = 14
): Indicator {

    val relevantData = reduceToRelevantData(data, start, windowSize)
        ?: return Indicator(start, data.first().columnName, IndicatorName.SMA, BigDecimal(0))

    val result = relevantData.map { it.value ?: BigDecimal(0) }
        .reduce { acc, num -> acc + num }
        .divide(windowSize.toBigDecimal(), 2, RoundingMode.HALF_UP)

    return Indicator(start, relevantData.last().columnName, IndicatorName.SMA, result)
}

/**
 * Reduces a list of AggregatedPriceInformationResponse into a sublist starting at the entry with the provided instant width length of windowSize. The list gets sorted by time descending before.
 * @param data The AggregatedPriceInformationResponses.
 * @param start Start instant to identify the start of the window.
 * @param windowSize Size of the window.
 * @return Sublist starting at the element which matches the start instant and stops windowSize-elements later.
 */
fun reduceToRelevantData(
    data: List<CsvEntry>,
    start: Instant,
    windowSize: Int
): List<CsvEntry>? {
    if (data.size < windowSize) return null

    val sorted = data.sortedByDescending { it.time }
    val startIndex = sorted.indexOfFirst { it.time == start }

    return if (startIndex + windowSize > data.size) null
    else sorted.subList(startIndex, startIndex + windowSize)
}