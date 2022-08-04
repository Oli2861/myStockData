package com.mystockdata.composerservice.csv

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
 */
abstract class CsvEntry(open val time: Instant, open val columnName: String)

data class PriceEntry(override val time: Instant, override val columnName: String, var price: BigDecimal?, val symbol: String): CsvEntry(time, columnName)

data class TimeEntry(override val time: Instant, override val columnName: String): CsvEntry(time, columnName)
