package com.mystockdata.composerservice.csv

import com.mystockdata.composerservice.stockdata.AggregatedPriceInformationResponse
import com.mystockdata.composerservice.stockdata.PrecisePriceInformationResponse
import java.math.BigDecimal
import java.time.Instant

/**
 * Represents an entry of a csv file.
 * @property time The csv produced here is always indexed by time. The instant hence influences in which row the entry will be placed.
 * @property columnName The columnName is used to place entries in the correct column.
 * @param value The value of the entry to be placed in a column.
 */
data class CsvEntry(val time: Instant, val columnName: String, var value: BigDecimal?)

/**
 * Produces a List<CsvEntry> for a provided list of AggregatedPriceInformation.
 * @param data List of AggregatedPriceInformation. Should be down sampled in order to produce a useful CSV Body.
 * @return list of the produced csv entries.
 */
fun aggregatedPriceInformationResponseToCSVEntry(
    data: List<AggregatedPriceInformationResponse>
): List<CsvEntry> {
    val entries: MutableList<CsvEntry> = mutableListOf()
    data.forEach {
        if (it.open != null) entries.add(CsvEntry(it.time, "open_${it.symbol}", it.open))
        if (it.high != null) entries.add(CsvEntry(it.time, "high_${it.symbol}", it.high))
        if (it.low != null) entries.add(CsvEntry(it.time, "low_${it.symbol}", it.low))
        if (it.close != null) entries.add(CsvEntry(it.time, "close_${it.symbol}", it.close))
        if (it.adjClose != null) entries.add(CsvEntry(it.time, "adjClose_${it.symbol}", it.adjClose))
        if (it.volume != null) entries.add(CsvEntry(it.time, "volume_${it.symbol}", it.volume?.toBigDecimal()))
    }
    return entries
}

/**
 * Produces a List<CsvEntry> for a provided list of PriceInformationResponse.
 * @param data List of PrecisePriceInformation. Should be down-sampled in order to produce a useful CSV Body.
 * @return list of the produced csv entries.
 */
fun precisePriceInformationResponseToCSV(data: List<PrecisePriceInformationResponse>): List<CsvEntry> = data.map { CsvEntry(it.time, it.symbol, it.price) }