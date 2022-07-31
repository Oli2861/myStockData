package com.mystockdata.composerservice

import com.mystockdata.composerservice.TimeIndexedCSVBuilderConstants.PLACEHOLDER_VALUE
import com.mystockdata.composerservice.TimeIndexedCSVBuilderConstants.TIMESTAMP_COLUMN_NAME
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.core.io.InputStreamResource
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.time.Duration
import java.time.Instant
import kotlin.math.absoluteValue

object TimeIndexedCSVBuilderConstants {
    const val TIMESTAMP_COLUMN_NAME: String = "timestamp"
    const val PLACEHOLDER_VALUE: String = "null"
}

class TimeIndexedCSVBuilder(
    initialData: List<CsvEntry>,
    fillMissingValuesWithNextMatching: Boolean = false
) {
    var csvHeader: MutableList<String>
    var csvBody: MutableList<MutableList<String>>
    private var timeIndex: MutableList<Instant>

    init {
        val (csvHeader, csvBody, timeIndex) = toCSVComponents(initialData, fillMissingValuesWithNextMatching)
        this.csvHeader = csvHeader
        this.csvBody = csvBody
        this.timeIndex = timeIndex
    }

    /**
     * Builds a time-indexed csv based on provided data.
     * @param data Data of the csv file.
     * @return Triple containing the header & body of the csv and the time index.
     */
    private fun toCSVComponents(data: List<CsvEntry>, fillMissingValuesWithNextMatching: Boolean): Triple<MutableList<String>, MutableList<MutableList<String>>, MutableList<Instant>> {
        // Map the name of each column to all its entries.
        val columnMap = splitEntriesByColumnName(data)
        val timeColumn: List<Instant> = data.distinctBy { it.time }.map { it.time }.sortedDescending()

        // Build header based on the column names of the entries and "timestamp" as first column.
        val csvHeader: MutableList<String> = mutableListOf(TIMESTAMP_COLUMN_NAME)
        csvHeader.addAll(columnMap.keys.toList().sorted())

        val csvBody: MutableList<MutableList<String>> = mutableListOf()

        for ((rowIndex, time) in timeColumn.withIndex()) {
            // Insert list for the row.
            csvBody.add(mutableListOf())
            // Insert timestamp-index.
            csvBody[rowIndex].add(time.toString())

            for (columnName in csvHeader) {
                // Search whether there is a value to be inserted in this column or else insert a placeholder value.
                if (columnName == TIMESTAMP_COLUMN_NAME) continue
                val entriesMatchingTime: List<CsvEntry>? = columnMap[columnName]?.filter { it.time == time }
                if (!entriesMatchingTime.isNullOrEmpty()) {
                    csvBody[rowIndex].add(entriesMatchingTime.first().value ?: PLACEHOLDER_VALUE)
                } else {
                    if(fillMissingValuesWithNextMatching){
                        val closestEntry = columnMap[columnName]?.let { findClosestEntry(time, it) }
                        csvBody[rowIndex].add(closestEntry?.value ?: PLACEHOLDER_VALUE)
                    }else {
                        csvBody[rowIndex].add(PLACEHOLDER_VALUE)
                    }
                }
            }
        }

        return Triple(csvHeader, csvBody, timeColumn.toMutableList())
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
     * Adds a single column to the csv by matching searching for each existing row the closest matching provided entry.
     * @param csvEntries entries to be added.
     */
    private fun addColumn(csvEntries: List<CsvEntry>, columnName: String){
        csvHeader.add(columnName)
        // Search for each time index the entry that matches best.
        for ((rowIndex, time) in timeIndex.withIndex()) {
            // Next matching entry.
            val closestEntry = findClosestEntry(time, csvEntries) ?: continue
            // Set closest entry as value in the current row.
            csvBody[rowIndex].add(closestEntry.value ?: PLACEHOLDER_VALUE)
        }

    }

    /**
     * Finds the next closest entry.
     * @param instant to find the next closest entry to.
     * @param entries list of the entries.
     * @return next closest entry.
     */
    fun findClosestEntry(instant: Instant, entries: List<CsvEntry>): CsvEntry?{
        return entries.fold(null) { acc: CsvEntry?, element ->
            return@fold if (acc == null || Duration.between(element.time, instant).seconds.absoluteValue < Duration.between(acc.time, instant).seconds.absoluteValue) element else acc
        }
    }

    /**
     * Function to produce a csv based on lists containing the data.
     * @param csvBody Body of the csv.
     * @return InputStreamResource
     * As in https://codeburst.io/returning-csv-content-from-an-api-in-spring-boot-63ea82bbcf0f
     */
    fun buildCSV(csvHeader: Array<String>, csvBody: Array<Array<String>>): InputStreamResource {
        val byteArrayOutputStream = ByteArrayOutputStream().use { out ->
            CSVPrinter(PrintWriter(out), CSVFormat.DEFAULT.withHeader(*csvHeader)).use { csvPrinter ->
                csvBody.forEach { record ->
                    csvPrinter.printRecord(record.asList())
                }
                csvPrinter.flush()
                ByteArrayInputStream(out.toByteArray())
            }
        }

        return InputStreamResource(byteArrayOutputStream)
    }

}

