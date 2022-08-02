package com.mystockdata.composerservice.csv

import java.math.BigDecimal
import java.time.Instant

/**
 * Represents an entry of a csv file.
 * @property time The csv produced here is always indexed by time. The instant hence influences in which row the entry will be placed.
 * @property columnName The columnName is used to place entries in the correct column.
 * @param value The value of the entry to be placed in a column.
 */
data class CsvEntry(val time: Instant, val columnName: String, var value: BigDecimal?)
