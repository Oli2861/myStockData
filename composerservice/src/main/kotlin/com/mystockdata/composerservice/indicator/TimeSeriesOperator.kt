package com.mystockdata.composerservice.indicator

import com.mystockdata.composerservice.csv.PriceEntry
import java.math.BigDecimal

/**
 * A collection of functions on lists of TimeSeriesDataPoints.
 */
object TimeSeriesOperator {

    /**
     * Splits a list of TimeSeriesDataPoint into multiple lists which contain all TimeSeriesDataPoint of a symbol.
     * @param data AggregatedPriceInformationResponses
     * @return List containing lists of TimeSeriesDataPoint for each symbol.
     */
    fun splitBySymbol(data: List<PriceEntry>): List<List<PriceEntry>> {
        val symbols: List<String> = data.distinctBy { it.columnName }.map { it.columnName }.sorted()
        val splitData: MutableList<List<PriceEntry>> = mutableListOf()
        for (symbol in symbols) {
            val curr = mutableListOf<PriceEntry>()
            data.filter { symbol == it.columnName }.forEach {
                curr.add(it)
            }
            splitData.add(curr.sortedByDescending { it.time })
        }
        return splitData
    }

    /**
     * Splits a list of TimeSeriesDataPoint into multiple lists which contain all TimeSeriesDataPoint of a symbol and fills missing values with the previous one.
     * @param data AggregatedPriceInformationResponses
     * @return List containing lists of TimeSeriesDataPoint for each symbol.
     */
    fun splitBySymbolAndFillMissingValues(data: List<PriceEntry>): List<List<PriceEntry>> {
        val splittedData = splitBySymbol(data)

        for (subList in splittedData) {
            // Fill missing values in sublist.
            for ((index, timeSeriesDataPoint) in subList.withIndex()) {
                if (timeSeriesDataPoint.price == null) {
                    // Search previous value.
                    for (i in index downTo 0) {
                        val currValue: BigDecimal? = subList[i].price
                        if (currValue != null) {
                            timeSeriesDataPoint.price = currValue
                        }
                    }
                }
            }
        }
        return splittedData
    }
}