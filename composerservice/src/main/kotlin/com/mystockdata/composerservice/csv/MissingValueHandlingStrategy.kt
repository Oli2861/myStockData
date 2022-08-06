package com.mystockdata.composerservice.csv

import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilderConstants.PLACEHOLDER_VALUE
import java.time.Duration
import java.time.Instant
import kotlin.math.absoluteValue

interface MissingValueHandlingStrategy {
    /**
     * Find the value to be placed in a specific row and column of a csvBody.
     * @param rowIndex index of the row the sought value will be placed in.
     * @param time timestamp of the current row for which the value is to be valid.
     * @param columnIndex index of the column sought value will be placed in.
     * @param columnName name of the column the value will be placed in.
     * @param symbol the symbol valid for all entries of the column.
     * @param csvBody the csv body the value is to be placed in.
     * @param entriesInSameColumn other entries in the same column.
     * @param allowNullString whether a null string can be placed.
     * @return the value to be placed.
     */
    fun findValueToBePlaced(
        rowIndex: Int,
        time: Instant,
        columnIndex: Int,
        columnName: String,
        symbol: String,
        csvBody: List<List<CsvEntry>>,
        entriesInSameColumn: List<PriceEntry?>?,
        allowNullString: Boolean = false
    ): PriceEntry
}

object IgnoreStrategy : MissingValueHandlingStrategy {
    /**
     * Ignores missing values and places an empty value.
     * @see MissingValueHandlingStrategy
     */
    override fun findValueToBePlaced(
        rowIndex: Int,
        time: Instant,
        columnIndex: Int,
        columnName: String,
        symbol: String,
        csvBody: List<List<CsvEntry>>,
        entriesInSameColumn: List<PriceEntry?>?,
        allowNullString: Boolean
    ): PriceEntry = PriceEntry(time, columnName, PLACEHOLDER_VALUE, symbol)
}

object LastValueStrategy : MissingValueHandlingStrategy {
    /**
     * Finds the last existing value in the column.
     * @see MissingValueHandlingStrategy
     * @return the last value in the column or a price entry with the placeholder value if none found.
     */
    override fun findValueToBePlaced(
        rowIndex: Int,
        time: Instant,
        columnIndex: Int,
        columnName: String,
        symbol: String,
        csvBody: List<List<CsvEntry>>,
        entriesInSameColumn: List<PriceEntry?>?,
        allowNullString: Boolean
    ): PriceEntry {
        // rowIndex - 1 because the current row is not considered.
        for (rowIdx in rowIndex - 1 downTo 0) {
            val curr = csvBody[rowIdx][columnIndex]
            (curr as? PriceEntry)?.let {
                if (if (allowNullString) true else it.price != PLACEHOLDER_VALUE)
                    return PriceEntry(time, columnName, it.price, symbol)
            }
        }
        return PriceEntry(time, columnName, PLACEHOLDER_VALUE, symbol)
    }
}

object ClosestEntryStrategy : MissingValueHandlingStrategy {
    /**
     * Finds the next closest entry measured in duration.
     * @see MissingValueHandlingStrategy
     * @return next closest entry or PLACEHOLDER_VALUE if none found.
     */
    override fun findValueToBePlaced(
        rowIndex: Int,
        time: Instant,
        columnIndex: Int,
        columnName: String,
        symbol: String,
        csvBody: List<List<CsvEntry>>,
        entriesInSameColumn: List<PriceEntry?>?,
        allowNullString: Boolean
    ): PriceEntry {
        if (entriesInSameColumn.isNullOrEmpty()) return PriceEntry(
            time, columnName,
            PLACEHOLDER_VALUE, symbol
        )
        val closestEntry: PriceEntry? = entriesInSameColumn.fold(null) { acc: PriceEntry?, element ->
            return@fold if (element == null || (!allowNullString && (element.price == null || element.price == PLACEHOLDER_VALUE))) {
                acc
            } else {
                if (acc == null || Duration.between(element.time, time).seconds.absoluteValue < Duration.between(acc.time, time).seconds.absoluteValue)
                    element else acc
            }
        }
        return PriceEntry(time, columnName, closestEntry?.price ?: PLACEHOLDER_VALUE, symbol)
    }
}
