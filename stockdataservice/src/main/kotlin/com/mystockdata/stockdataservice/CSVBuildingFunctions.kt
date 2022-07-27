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
fun aggregatedPriceInformationResponseToCSV(
    data: List<AggregatedPriceInformationResponse>
): Pair<Array<String>, Array<Array<String>>> {
    val columns: MutableList<Column> = mutableListOf()
    data.forEach {
        if (it.open != null) columns.add(Column(it.time, "open_${it.symbol}", it.open.toString()))
        if (it.high != null) columns.add(Column(it.time, "high_${it.symbol}", it.high.toString()))
        if (it.low != null) columns.add(Column(it.time, "low_${it.symbol}", it.low.toString()))
        if (it.close != null) columns.add(Column(it.time, "close_${it.symbol}", it.close.toString()))
        if (it.adjClose != null) columns.add(Column(it.time, "adjClose_${it.symbol}", it.adjClose.toString()))
        if (it.volume != null) columns.add(Column(it.time, "volume_${it.symbol}", it.volume.toString()))
    }
    return toCSVBody(columns)
}

/**
 * Produces a CSV Header and CSV Body for a provided list of PriceInformationResponse.
 * @param data List of PriceInformationResponse. Should be down sampled in order to produce a useful CSV Body.
 * @return Pair containing the CSV Header row and the CSV Body
 */
fun precisePriceInformationResponseToCSV(data: List<PrecisePriceInformationResponse>): Pair<Array<String>, Array<Array<String>>> =
    toCSVBody(data.map { Column(it.time, it.symbol, it.price.toString()) })

data class Column(val time: Instant, val name: String, val value: String?)


fun toCSVBody(data: List<Column>): Pair<Array<String>, Array<Array<String>>> {
    val timeColumn: List<Instant> = data.distinctBy { it.time }.map { it.time }.sortedDescending()
    val columnNames: List<String> = data.distinctBy { it.name }.map { it.name }.sorted()
    val csvHeader: Array<String> = arrayOf("timestamp") + columnNames.toTypedArray()

    // Amount of distinct time stamps = length of time column = max amount of rows
    // Amount of distinct symbols = amount of columns = max amount of columns + 1 for time column
    val csvBody: Array<Array<String>> = Array(timeColumn.size) { Array(columnNames.size + 1) { "null" } }

    for ((rowIndex, time) in timeColumn.withIndex()) {
        csvBody[rowIndex][0] = time.toString()

        val matchingPriceInfo = data.filter { it.time == time }

        for (priceInfo in matchingPriceInfo) {
            val columnIndex = columnNames.indexOf(priceInfo.name) + 1
            csvBody[rowIndex][columnIndex] = priceInfo.value.toString()
        }
    }

    //printMultiDimensionalArray(csvBody)
    return Pair(csvHeader, csvBody)
}
