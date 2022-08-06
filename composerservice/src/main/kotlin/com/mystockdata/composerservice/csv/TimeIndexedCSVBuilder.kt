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
import java.time.Instant

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

                val valueToBePlaced = entriesMatchingTime?.firstOrNull { it.price != null && it.price != PLACEHOLDER_VALUE }
                        ?: missingValueHandlingStrategy.findValueToBePlaced(rowIndex, time, columnIndex, columnName, symbol, csvBody, columnMap[columnName], false)

                csvBody[rowIndex].add(valueToBePlaced)
            }
        }

        return Triple(csvHeader, csvBody, timeColumn.toMutableList())
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
            columnMap[columnName]?.let { addColumn(it, columnName) }
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
            val closestEntry = ClosestEntryStrategy.findValueToBePlaced(
                rowIndex, time, csvHeader.indexOf(columnName), columnName, csvEntries.firstOrNull()?.symbol ?: SYMBOL_MISSING_STRING, csvBody, csvEntries,false)
            // Set closest entry as value in the current row.
            csvBody[rowIndex].add(closestEntry)
        }
    }

    /**
     * @param colsToCalculateIndicatorsFor list of the cols to calculate indicators of.
     * @param calculationFunction callback function to perform the calculation in. Parameter is a list with the values of the column the indicator is calculated for.
     */
    fun addIndicator(
        indicatorName: IndicatorName,
        colsToCalculateIndicatorsFor: List<String>,
        ignoreNullColumns: Boolean = false,
        calculationFunction: (List<PriceEntry>) -> List<Indicator>,
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
                when (csvEntry) {
                    is TimeEntry -> stringRowList.add(csvEntry.time.toString())
                    is PriceEntry -> stringRowList.add(if (csvEntry.price == null) PLACEHOLDER_VALUE_STRING else csvEntry.price.toString())
                }
            }
            stringCSVBody.add(stringRowList)
        }
        return stringCSVBody
    }
}