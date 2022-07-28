package com.mystockdata.stockdataservice.indicators

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationResponse
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

/**
 * Calculates simple moving average for all values of a given list of AggregatedPriceInformationResponses as far as possible.
 * @param data The AggregatedPriceInformationResponses. Can contain data for different symbols.
 * @param windowSize Size of the SAM-window.
 * @return List containing lists of the calculated SMA for each symbol. Rounded to two decimal points.
 */
fun smaForMultipleSymbols(data: List<AggregatedPriceInformationResponse>, windowSize: Int = 14): List<List<TechnicalIndicator>>{
    val sorted = splitBySymbol(data)
    return sorted.map { listForASymbol -> smaForAllOfASymbol(listForASymbol, windowSize) }
}

/**
 * Calculates simple moving average for all values of a given list of AggregatedPriceInformationResponses as far as possible.
 * @param data The AggregatedPriceInformationResponses **with the same symbol**.
 * @param windowSize Size of the SAM-window.
 * @return list of the calculated SMA. Rounded to two decimal points.
 */
fun smaForAllOfASymbol(
    data: List<AggregatedPriceInformationResponse>,
    windowSize: Int = 14
): List<TechnicalIndicator> {
    val smaList = mutableListOf<TechnicalIndicator>()
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
    data: List<AggregatedPriceInformationResponse>,
    start: Instant,
    windowSize: Int = 14
): TechnicalIndicator {

    val relevantData = reduceToRelevantData(data, start, windowSize)
        ?: return TechnicalIndicator(start, data.first().symbol, TechnicalIndicatorName.SMA, BigDecimal(0))

    val result = relevantData.map { it.close ?: BigDecimal(0) }
        .reduce { acc, num -> acc + num }
        .divide(windowSize.toBigDecimal(), 2, RoundingMode.HALF_UP)

    return TechnicalIndicator(start, relevantData.last().symbol, TechnicalIndicatorName.SMA, result)
}

/**
 * Reduces a list of AggregatedPriceInformationResponse into a sublist starting at the entry with the provided instant width length of windowSize. The list gets sorted by time descending before.
 * @param data The AggregatedPriceInformationResponses.
 * @param start Start instant to identify the start of the window.
 * @param windowSize Size of the window.
 * @return Sublist starting at the element which matches the start instant and stops windowSize-elements later.
 */
fun reduceToRelevantData(
    data: List<AggregatedPriceInformationResponse>,
    start: Instant,
    windowSize: Int
): List<AggregatedPriceInformationResponse>? {
    if (data.size < windowSize) return null

    val sorted = data.sortedByDescending { it.time }
    val startIndex = sorted.indexOfFirst { it.time == start }

    return if (startIndex + windowSize > data.size) null
    else sorted.subList(startIndex, startIndex + windowSize)
}