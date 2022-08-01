package com.mystockdata.composerservice.csv

import com.mystockdata.composerservice.Indicator
import com.mystockdata.composerservice.IndicatorName
import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilderConstants.PLACEHOLDER_VALUE
import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilderConstants.PLACEHOLDER_VALUE_STRING
import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilderConstants.TIMESTAMP_COLUMN_NAME
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.core.io.InputStreamResource
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import kotlin.math.absoluteValue

object TimeIndexedCSVBuilderConstants {
    const val TIMESTAMP_COLUMN_NAME: String = "timestamp"
    val PLACEHOLDER_VALUE: BigDecimal? = null
    const val PLACEHOLDER_VALUE_STRING: String = "null"
}

enum class MissingValueHandlingStrategy {
    IGNORE,
    LAST_VALUE,
    NEXT_MATCHING
}

class TimeIndexedCSVBuilder(
    initialData: List<CsvEntry>,
    missingValueHandlingStrategy: MissingValueHandlingStrategy
) {
    var csvHeader: MutableList<String>
    var csvBody: MutableList<MutableList<BigDecimal?>>
    private var timeIndex: MutableList<Instant>

    init {
        val (csvHeader, csvBody, timeIndex) = toCSVComponents(initialData, missingValueHandlingStrategy)
        this.csvHeader = csvHeader
        this.csvBody = csvBody
        this.timeIndex = timeIndex
    }

    /**
     * Builds a time-indexed csv based on provided data.
     * @param data Data of the csv file.
     * @return Triple containing the header & body of the csv and the time index.
     */
    private fun toCSVComponents(
        data: List<CsvEntry>,
        missingValueHandlingStrategy: MissingValueHandlingStrategy
    ): Triple<MutableList<String>, MutableList<MutableList<BigDecimal?>>, MutableList<Instant>> {

        // Map the name of each column to all its entries.
        val columnMap = splitEntriesByColumnName(data)
        val timeColumn: List<Instant> = data.distinctBy { it.time }.map { it.time }.sorted()

        // Build header based on the column names of the entries and "timestamp" as first column.
        val csvHeader: MutableList<String> = mutableListOf(TIMESTAMP_COLUMN_NAME)
        csvHeader.addAll(columnMap.keys.toList().sorted())

        val csvBody: MutableList<MutableList<BigDecimal?>> = mutableListOf()

        for ((rowIndex, time) in timeColumn.withIndex()) {
            // Insert list for the row.
            csvBody.add(mutableListOf())
            // Insert timestamp-index as the first value of the row.
            csvBody[rowIndex].add(time.toEpochMilli().toBigDecimal())
            // Place the values of all columns.
            for ((columnIndex, columnName) in csvHeader.withIndex()) {
                // Search whether there is a value to be inserted in this column or else insert a placeholder value.
                if (columnName == TIMESTAMP_COLUMN_NAME) continue
                val entriesMatchingTime: List<CsvEntry>? = columnMap[columnName]?.filter { it.time == time }
                val valueToBePlaced = findValueToBePlaced(
                    missingValueHandlingStrategy, entriesMatchingTime,
                    rowIndex, columnIndex, csvBody, time, columnMap, columnName
                )
                csvBody[rowIndex].add(valueToBePlaced)
            }
        }

        return Triple(csvHeader, csvBody, timeColumn.toMutableList())
    }

    /**
     * Resolve the value to be placed.
     * @return the value to be placed.
     */
    private fun findValueToBePlaced(
        missingValueHandlingStrategy: MissingValueHandlingStrategy,
        entriesMatchingTime: List<CsvEntry>?,
        rowIndex: Int,
        columnIndex: Int,
        csvBody: MutableList<MutableList<BigDecimal?>>,
        time: Instant,
        columnMap: Map<String, MutableList<CsvEntry>>,
        columnName: String
    ): BigDecimal? {
        return when (missingValueHandlingStrategy) {

            MissingValueHandlingStrategy.IGNORE -> {
                if (entriesMatchingTime.isNullOrEmpty()) {
                    PLACEHOLDER_VALUE
                } else {
                    entriesMatchingTime.firstOrNull { it.value != null && it.value != PLACEHOLDER_VALUE }?.value
                        ?: PLACEHOLDER_VALUE
                }
            }

            MissingValueHandlingStrategy.LAST_VALUE -> {
                if (entriesMatchingTime.isNullOrEmpty()) {
                    findLastEntryMissingValueStrategy(rowIndex, columnIndex, csvBody)
                } else {
                    entriesMatchingTime.firstOrNull { it.value != null && it.value != PLACEHOLDER_VALUE }?.value
                        ?: findLastEntryMissingValueStrategy(rowIndex, columnIndex, csvBody)
                }
            }

            MissingValueHandlingStrategy.NEXT_MATCHING -> {
                // Use the first matching entry which value is not null or "null" or otherwise use the closest entry. If both are null use the PLACEHOLDER_VALUE.
                if (entriesMatchingTime.isNullOrEmpty()) {
                    findClosestEntryMissingValueStrategy(time, columnMap[columnName], false)
                } else {
                    // Use the first matching entry which value is not null or "null" or otherwise use the closest entry. If both are null use the PLACEHOLDER_VALUE.
                    entriesMatchingTime.firstOrNull { it.value != null && it.value != PLACEHOLDER_VALUE }?.value
                        ?: findClosestEntryMissingValueStrategy(time, columnMap[columnName], false)
                }
            }
        }
    }


    /**
     * Finds the last value existing in the column.
     * @param rowIndex Index of the column.
     * @param columnIndex Index of the column.
     * @return The last known value in the column or PLACEHOLDER_VALUE if none exists.
     */
    fun findLastEntryMissingValueStrategy(
        rowIndex: Int,
        columnIndex: Int,
        csvBody: List<List<BigDecimal?>>
    ): BigDecimal? {
        // rowIndex - 1 because the current row is not considered.
        for (rowIdx in rowIndex - 1 downTo 0) {
            val curr = csvBody[rowIdx][columnIndex]
            if (curr != PLACEHOLDER_VALUE) return curr
        }
        return PLACEHOLDER_VALUE
    }

    /**
     * Finds the next closest entry measured in duration.
     * @param instant to find the next closest entry to.
     * @param entries list of the entries.
     * @return next closest entry or PLACEHOLDER_VALUE if none found.
     */
    fun findClosestEntryMissingValueStrategy(
        instant: Instant,
        entries: List<CsvEntry?>?,
        allowNullString: Boolean
    ): BigDecimal? {
        if (entries.isNullOrEmpty()) return PLACEHOLDER_VALUE
        val closestEntry: CsvEntry? = entries.fold(null) { acc: CsvEntry?, element ->
            return@fold if (
                element == null ||
                (!allowNullString && (element.value == null || element.value == PLACEHOLDER_VALUE))
            ) {
                acc
            } else {
                if (
                    acc == null ||
                    Duration.between(element.time, instant).seconds.absoluteValue
                    < Duration.between(acc.time, instant).seconds.absoluteValue
                ) element else acc

            }
        }
        return closestEntry?.value ?: PLACEHOLDER_VALUE
    }


    /**
     * Splits a list of csv entries into a map mapping their column names to a list of corresponding entries.
     * @param data list of csv entries to be split.
     * @return Map mapping the column name to a list of corresponding csv entries.
     */
    private fun splitEntriesByColumnName(data: List<CsvEntry>): Map<String, MutableList<CsvEntry>> {
        val columnMap = mutableMapOf<String, MutableList<CsvEntry>>()
        data.forEach {
            if (columnMap[it.columnName] == null) {
                columnMap[it.columnName] = mutableListOf()
                columnMap[it.columnName]?.add(it)
            } else {
                columnMap[it.columnName]?.add(it)
            }
        }
        return columnMap
    }

    /**
     * Adds one or multiple columns to the csv.
     * @param data list of entries to be added to the csv.
     */
    fun addColumns(data: List<CsvEntry>) {
        val columnMap = splitEntriesByColumnName(data)
        columnMap.keys.forEach { columnName ->
            columnMap[columnName]?.let { it -> addColumn(it, columnName) }
        }
    }

    /**
     * @param colsToCalculateIndicatorsFor list of the cols to calculate indicators of.
     * @param calculationFunction callback function to perform the calculation in. Parameter is a list with the values of the column the indicator is calculated for.
     */
    fun addIndicator(
        indicatorName: IndicatorName,
        colsToCalculateIndicatorsFor: List<String>,
        calculationFunction: (List<CsvEntry>) -> List<Indicator>
    ) {
        colsToCalculateIndicatorsFor.map {
            getColumn(it)
        }.forEach { column ->
            val calculatedIndicator = calculationFunction.invoke(column)
            if (calculatedIndicator.isEmpty()) return@forEach
            val header = "${indicatorName.indicatorName}_${calculatedIndicator.firstNotNullOfOrNull { it.symbol }}"
            addColumn(
                calculatedIndicator.map { CsvEntry(it.time, header, it.value) },
                header
            )
        }
    }

    fun getColumn(columnName: String) = getColumn(csvHeader.indexOf(columnName))

    private fun getColumn(columnIndex: Int): List<CsvEntry> {
        return timeIndex.mapIndexed { rowIndex, time ->
            CsvEntry(time, csvHeader[columnIndex], csvBody[rowIndex][columnIndex])
        }
    }


    /**
     * Adds a single column to the csv by matching searching for each existing row the closest matching provided entry.
     * @param csvEntries entries to be added.
     */
    private fun addColumn(csvEntries: List<CsvEntry?>, columnName: String) {
        csvHeader.add(columnName)

        // Search for each time index the entry that matches best.
        for ((rowIndex, time) in timeIndex.withIndex()) {
            // Next matching entry.
            val closestEntry = findClosestEntryMissingValueStrategy(time, csvEntries, false)
            if (closestEntry != PLACEHOLDER_VALUE) {
                // Set closest entry as value in the current row.
                csvBody[rowIndex].add(closestEntry)
            }
        }

    }


    /**
     * Function to produce a csv based on csvHeader and csvBody.
     * @return InputStreamResource
     * As in https://codeburst.io/returning-csv-content-from-an-api-in-spring-boot-63ea82bbcf0f
     */
    fun buildCSV(): InputStreamResource {
        val stringBody = toStringBody()

        val byteArrayOutputStream = ByteArrayOutputStream().use { out ->
            CSVPrinter(PrintWriter(out), CSVFormat.DEFAULT.withHeader(*csvHeader.toTypedArray())).use { csvPrinter ->
                stringBody.forEach { record ->
                    csvPrinter.printRecord(record)
                }
                csvPrinter.flush()
                ByteArrayInputStream(out.toByteArray())
            }
        }

        return InputStreamResource(byteArrayOutputStream)
    }

    /**
     * Transforms the csvBody into a list of strings.
     */
    private fun toStringBody(): List<List<String>> {
        val stringCSVBody = mutableListOf<MutableList<String>>()
        csvBody.forEach { rowList ->
            val stringRowList = mutableListOf<String>()
            stringRowList.add(rowList[0]?.toLong()?.let { Instant.ofEpochMilli(it).toString() }
                ?: PLACEHOLDER_VALUE_STRING)
            rowList.forEachIndexed { columnIndex, bigDecimal ->
                if (columnIndex != 0) {
                    if (bigDecimal == PLACEHOLDER_VALUE) {
                        stringRowList.add(PLACEHOLDER_VALUE_STRING)
                    } else {
                        stringRowList.add(bigDecimal.toString())
                    }
                }
            }
            stringCSVBody.add(stringRowList)
        }
        return stringCSVBody
    }

}

