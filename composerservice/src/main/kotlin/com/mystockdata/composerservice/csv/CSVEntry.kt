package com.mystockdata.composerservice.csv

import com.mystockdata.composerservice.csv.CSVEntryConstants.ADJ_CLOSE_COLUMN_NAME_PREFIX
import com.mystockdata.composerservice.csv.CSVEntryConstants.CLOSE_COLUMN_NAME_PREFIX
import com.mystockdata.composerservice.csv.CSVEntryConstants.HIGH_COLUMN_NAME_PREFIX
import com.mystockdata.composerservice.csv.CSVEntryConstants.LOW_COLUMN_NAME_PREFIX
import com.mystockdata.composerservice.csv.CSVEntryConstants.OPEN_COLUMN_NAME_PREFIX
import com.mystockdata.composerservice.csv.CSVEntryConstants.VOLUME_COLUMN_NAME_PREFIX
import com.mystockdata.composerservice.stockdata.AggregatedPriceInformationResponse
import java.math.BigDecimal
import java.time.Instant

object CSVEntryConstants{
    const val OPEN_COLUMN_NAME_PREFIX = "open_"
    const val HIGH_COLUMN_NAME_PREFIX = "high_"
    const val LOW_COLUMN_NAME_PREFIX = "low_"
    const val CLOSE_COLUMN_NAME_PREFIX = "close_"
    const val ADJ_CLOSE_COLUMN_NAME_PREFIX = "adjClose_"
    const val VOLUME_COLUMN_NAME_PREFIX = "volume_"
}

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
fun List<AggregatedPriceInformationResponse>.toCSVEntryList(): List<CsvEntry> {
    val entries: MutableList<CsvEntry> = mutableListOf()
    forEach {
        if (it.open != null) entries.add(CsvEntry(it.time, "$OPEN_COLUMN_NAME_PREFIX${it.symbol}", it.open))
        if (it.high != null) entries.add(CsvEntry(it.time, "$HIGH_COLUMN_NAME_PREFIX${it.symbol}", it.high))
        if (it.low != null) entries.add(CsvEntry(it.time, "$LOW_COLUMN_NAME_PREFIX${it.symbol}", it.low))
        if (it.close != null) entries.add(CsvEntry(it.time, "$CLOSE_COLUMN_NAME_PREFIX${it.symbol}", it.close))
        if (it.adjClose != null) entries.add(CsvEntry(it.time, "$ADJ_CLOSE_COLUMN_NAME_PREFIX${it.symbol}", it.adjClose))
        if (it.volume != null) entries.add(CsvEntry(it.time, "$VOLUME_COLUMN_NAME_PREFIX${it.symbol}", it.volume?.toBigDecimal()))
    }
    return entries
}
