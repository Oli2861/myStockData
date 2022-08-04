package com.mystockdata.composerservice.csv

import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilderConstants.PLACEHOLDER_VALUE
import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilderConstants.PLACEHOLDER_VALUE_STRING
import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilderConstants.SYMBOL_MISSING_STRING
import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilderConstants.TIMESTAMP_COLUMN_NAME
import com.mystockdata.composerservice.indicator.Indicator
import com.mystockdata.composerservice.indicator.IndicatorName
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
    const val SYMBOL_MISSING_STRING: String = "symbol missing"
}

/**
 * Class used to build comma separated files.
 * @param initialData data used as a base of the csv files. The initial data provided is used to define the timeIndex.
 * @param missingValueHandlingStrategy how missing values should be handled.
 * @property csvHeader header of the CSV file.
 * @property csvBody body of the CSV file.
 * @property timeIndex timestamps in the first column of the csv file.
 */
class TimeIndexedCSVBuilder(
    initialData: List<PriceEntry>,
    missingValueHandlingStrategy: MissingValueHandlingStrategy
) {
    var csvHeader: MutableList<String>
    var csvBody: MutableList<MutableList<CsvEntry>>
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
        data: List<PriceEntry>,
        missingValueHandlingStrategy: MissingValueHandlingStrategy
    ): Triple<MutableList<String>, MutableList<MutableList<CsvEntry>>, MutableList<Instant>> {

        // Map the name of each column to all its entries.
        val columnMap = splitEntriesByColumnName(data)
        val timeColumn: List<Instant> = data.distinctBy { it.time }.map { it.time }.sorted()

        // Build header based on the column names of the entries and "timestamp" as first column.
        val csvHeader: MutableList<String> = mutableListOf(TIMESTAMP_COLUMN_NAME)
        csvHeader.addAll(columnMap.keys.toList().sorted())

        val csvBody: MutableList<MutableList<CsvEntry>> = mutableListOf()

        for ((rowIndex, time) in timeColumn.withIndex()) {
            // Insert list for the row.
            csvBody.add(mutableListOf())
            // Insert timestamp-index as the first value of the row.
            csvBody[rowIndex].add(TimeEntry(time, TIMESTAMP_COLUMN_NAME))
            // Place the values of all columns.
            for ((columnIndex, columnName) in csvHeader.withIndex()) {
                // Search whether there is a value to be inserted in this column or else insert a placeholder value.
                if (columnName == TIMESTAMP_COLUMN_NAME) continue
                val entriesMatchingTime: List<PriceEntry>? =
                    columnMap[columnName]?.filter { it.time == time }
                val symbol = columnMap[columnName]?.firstOrNull()?.symbol ?: SYMBOL_MISSING_STRING
                val valueToBePlaced = findValueToBePlaced(
                    missingValueHandlingStrategy, entriesMatchingTime, symbol,
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
        entriesMatchingTime: List<PriceEntry>?,
        symbol: String,
        rowIndex: Int,
        columnIndex: Int,
        csvBody: MutableList<MutableList<CsvEntry>>,
        time: Instant,
        columnMap: Map<String, MutableList<PriceEntry>>,
        columnName: String
    ): PriceEntry {

        return when (missingValueHandlingStrategy) {

            MissingValueHandlingStrategy.IGNORE -> {
                if (entriesMatchingTime.isNullOrEmpty()) {
                    PriceEntry(time, columnName, PLACEHOLDER_VALUE, symbol)
                } else {
                    entriesMatchingTime.firstOrNull { it.price != null && it.price != PLACEHOLDER_VALUE } ?: PriceEntry(time, columnName, PLACEHOLDER_VALUE, symbol)
                }
            }

            MissingValueHandlingStrategy.LAST_VALUE -> {
                if (entriesMatchingTime.isNullOrEmpty()) {
                    findLastEntryMissingValueStrategy(rowIndex, columnIndex, columnName, time, symbol, csvBody)
                } else {
                    entriesMatchingTime.firstOrNull { it.price != null && it.price != PLACEHOLDER_VALUE }
                        ?: findLastEntryMissingValueStrategy(rowIndex, columnIndex, columnName, time, symbol, csvBody)
                }
            }

            MissingValueHandlingStrategy.NEXT_MATCHING -> {
                // Use the first matching entry which value is not null or "null" or otherwise use the closest entry. If both are null use the PLACEHOLDER_VALUE.
                if (entriesMatchingTime.isNullOrEmpty()) {
                    findClosestEntryMissingValueStrategy(time, columnName, columnMap[columnName], symbol,false)
                } else {
                    // Use the first matching entry which value is not null or "null" or otherwise use the closest entry. If both are null use the PLACEHOLDER_VALUE.
                    entriesMatchingTime.firstOrNull { it.price != null && it.price != PLACEHOLDER_VALUE }
                        ?: findClosestEntryMissingValueStrategy(time, columnName, columnMap[columnName], symbol, false)
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
        columnName: String,
        time: Instant,
        symbol: String,
        csvBody: List<List<CsvEntry>>
    ): PriceEntry {
        // rowIndex - 1 because the current row is not considered.
        for (rowIdx in rowIndex - 1 downTo 0) {
            val curr = csvBody[rowIdx][columnIndex]
            (curr as? PriceEntry)?.let {
                if (it.price != PLACEHOLDER_VALUE) return PriceEntry(time, columnName, it.price, symbol)
            }
        }
        return PriceEntry(time, columnName, PLACEHOLDER_VALUE, symbol)
    }

    /**
     * Finds the next closest entry measured in duration.
     * @param time to find the next closest entry to.
     * @param entries list of the entries.
     * @return next closest entry or PLACEHOLDER_VALUE if none found.
     */
    fun findClosestEntryMissingValueStrategy(
        time: Instant,
        columnName: String,
        entries: List<PriceEntry?>?,
        symbol: String,
        allowNullString: Boolean
    ): PriceEntry {
        if (entries.isNullOrEmpty()) return PriceEntry(time, columnName, PLACEHOLDER_VALUE, symbol)
        val closestEntry: PriceEntry? = entries.fold(null) { acc: PriceEntry?, element ->
            return@fold if (element == null || (!allowNullString && (element.price == null || element.price == PLACEHOLDER_VALUE))) {
                acc
            } else {
                if (acc == null || Duration.between(element.time, time).seconds.absoluteValue < Duration.between(acc.time, time).seconds.absoluteValue)
                    element else acc
            }
        }
        return PriceEntry(time, columnName, closestEntry?.price ?: PLACEHOLDER_VALUE, symbol)
    }


    /**
     * Splits a list of csv entries into a map mapping their column names to a list of corresponding entries.
     * @param data list of csv entries to be split.
     * @return Map mapping the column name to a list of corresponding csv entries.
     */
    private fun splitEntriesByColumnName(data: List<PriceEntry>): Map<String, MutableList<PriceEntry>> {
        val columnMap = mutableMapOf<String, MutableList<PriceEntry>>()
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
    fun addColumns(data: List<PriceEntry>) {
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
        calculationFunction: (List<PriceEntry>) -> List<Indicator>
    ) {
        colsToCalculateIndicatorsFor.map {
            getColumn(it)
        }.map {
            it.mapNotNull { entry -> entry as? PriceEntry }
        }.forEach { column ->
            val calculatedIndicator = calculationFunction.invoke(column)
            if (calculatedIndicator.isEmpty()) return@forEach
            val header = "${indicatorName.indicatorName}_${calculatedIndicator.firstNotNullOfOrNull { it.symbol }}"
            addColumn(calculatedIndicator.map { PriceEntry(it.time, header, it.value, it.symbol) }, header)
        }
    }

    fun getColumn(columnName: String) = getColumn(csvHeader.indexOf(columnName))

    private fun getColumn(columnIndex: Int): List<CsvEntry> {
        return List(timeIndex.size) { rowIndex ->
            csvBody[rowIndex][columnIndex]
        }
    }

    /**
     * Adds a single column to the csv by matching searching for each existing row the closest matching provided entry.
     * @param csvEntries entries to be added.
     */
    private fun addColumn(csvEntries: List<PriceEntry>, columnName: String) {
        csvHeader.add(columnName)
        // Search for each time index the entry that matches best.
        for ((rowIndex, time) in timeIndex.withIndex()) {
            // Next matching entry.
            val closestEntry = findClosestEntryMissingValueStrategy(time, columnName, csvEntries, csvEntries.firstOrNull()?.symbol
                ?: SYMBOL_MISSING_STRING, false)
            // Set closest entry as value in the current row.
            csvBody[rowIndex].add(closestEntry)
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
     * Transforms the csvBody into a list of strings. Replaces placeholder values with the placeholder value string.
     * @return csv body with strings instead of bigdecimals.
     */
    private fun toStringBody(): List<List<String>> {
        val stringCSVBody = mutableListOf<MutableList<String>>()
        csvBody.forEach { rowList ->
            val stringRowList = mutableListOf<String>()

            rowList.forEachIndexed { _, csvEntry ->
               when(csvEntry){
                   is TimeEntry -> stringRowList.add(csvEntry.time.toString())
                   is PriceEntry -> stringRowList.add(if (csvEntry.price == null ) PLACEHOLDER_VALUE_STRING else csvEntry.price.toString())
               }
            }
            stringCSVBody.add(stringRowList)
        }
        return stringCSVBody
    }
}