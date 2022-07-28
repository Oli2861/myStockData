package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationResponse
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationResponse
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.springframework.core.io.InputStreamResource
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.time.Instant

/**
 * Function to produce a csv based on lists containing the data.
 * @param csvBody Body of the csv.
 * @return InputStreamResource
 * As in https://codeburst.io/returning-csv-content-from-an-api-in-spring-boot-63ea82bbcf0f
 */
fun toCSVFile(csvHeader: Array<String>, csvBody: Array<Array<String>>): InputStreamResource {
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

/**
 * Produces a CSV Header and CSV Body for a provided list of InfluxAggregatedPriceInformationResponse.
 * @param data List of InfluxAggregatedPriceInformationResponse. Should be down sampled in order to produce a useful CSV Body.
 * @return Pair containing the CSV Header row and the CSV Body
 */
fun aggregatedPriceInformationResponseToCSVColumns(
    data: List<AggregatedPriceInformationResponse>
): List<CsvEntry> {
    val entries: MutableList<CsvEntry> = mutableListOf()
    data.forEach {
        if (it.open != null) entries.add(CsvEntry(it.time, "open_${it.symbol}", it.open.toString()))
        if (it.high != null) entries.add(CsvEntry(it.time, "high_${it.symbol}", it.high.toString()))
        if (it.low != null) entries.add(CsvEntry(it.time, "low_${it.symbol}", it.low.toString()))
        if (it.close != null) entries.add(CsvEntry(it.time, "close_${it.symbol}", it.close.toString()))
        if (it.adjClose != null) entries.add(CsvEntry(it.time, "adjClose_${it.symbol}", it.adjClose.toString()))
        if (it.volume != null) entries.add(CsvEntry(it.time, "volume_${it.symbol}", it.volume.toString()))
    }
    return entries
}

/**
 * Produces a CSV Header and CSV Body for a provided list of PriceInformationResponse.
 * @param data List of PriceInformationResponse. Should be down sampled in order to produce a useful CSV Body.
 * @return Pair containing the CSV Header row and the CSV Body
 */
fun precisePriceInformationResponseToCSV(data: List<PrecisePriceInformationResponse>): Pair<Array<String>, Array<Array<String>>> =
    toCSVBody(data.map { CsvEntry(it.time, it.symbol, it.price.toString()) })

/**
 * Represents an entry of a csv file.
 * @property time The csv produced here is always indexed by time. The instant hence influences in which row the entry will be placed.
 * @property columnName The name is used to place entries in the correct column.
 * @param value The value of the entry to be placed in a column.
 */
data class CsvEntry(val time: Instant, val columnName: String, val value: String?)

/**
 * Builds a time-indexed csv based on provided data.
 * @param data Data of the csv file.
 * @return Pair containing the header and the body of the csv.
 */
fun toCSVBody(data: List<CsvEntry>): Pair<Array<String>, Array<Array<String>>> {
    val timeColumn: List<Instant> = data.distinctBy { it.time }.map { it.time }.sortedDescending()
    // Build header based on the column names of the entries and "timestamp" as first column
    val columnNames: List<String> = data.distinctBy { it.columnName }.map { it.columnName }.sorted()
    val csvHeader: Array<String> = arrayOf("timestamp") + columnNames.toTypedArray()

    // Amount of distinct time stamps = length of time column = max amount of rows
    // Amount of distinct symbols = amount of columns = max amount of columns + 1 for time column
    val csvBody: Array<Array<String>> = Array(timeColumn.size) { Array(columnNames.size + 1) { "null" } }

    for ((rowIndex, time) in timeColumn.withIndex()) {
        csvBody[rowIndex][0] = time.toString()
        // Filter for all entries to be placed in this row
        val matchingPriceInfo = data.filter { it.time == time }

        for (priceInfo in matchingPriceInfo) {
            val columnIndex = columnNames.indexOf(priceInfo.columnName) + 1
            csvBody[rowIndex][columnIndex] = priceInfo.value.toString()
        }
    }

    //printMultiDimensionalArray(csvBody)
    return Pair(csvHeader, csvBody)
}
