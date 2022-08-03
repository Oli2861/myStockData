package com.mystockdata.composerservice.csv

import com.mystockdata.composerservice.indicator.IndicatorName
import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilderConstants.PLACEHOLDER_VALUE
import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilderConstants.TIMESTAMP_COLUMN_NAME
import com.mystockdata.composerservice.indicator.Indicator
import com.mystockdata.composerservice.indicator.IndicatorType
import com.mystockdata.composerservice.printMultiDimensionalList
import com.mystockdata.composerservice.stockdata.AggregatedPriceInformationResponse
import com.mystockdata.composerservice.stockdata.PrecisePriceInformationResponse
import com.mystockdata.composerservice.stockdata.toCSVEntryList
import com.mystockdata.composerservice.indicator.smaForAllOfASymbol
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.MathContext
import java.time.Instant

class TimeIndexedCSVBuilderTest {
    private val placeholderValue = PLACEHOLDER_VALUE
    private val placeholderValueString = PLACEHOLDER_VALUE.toString()

    @Test
    fun buildCSVOfAggregatedPriceInformation() {
        val now = Instant.now()

        val data = listOf(
            AggregatedPriceInformationResponse(now, "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformationResponse(now, "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005),
            AggregatedPriceInformationResponse(now.minusSeconds(10), "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformationResponse(now.minusSeconds(22), "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005)
        )

        val expectedHeader = arrayOf("timestamp") + arrayOf("open_SAP.DE", "high_SAP.DE","low_SAP.DE", "close_SAP.DE", "adjClose_SAP.DE", "volume_SAP.DE", "open_VW.DE", "high_VW.DE", "low_VW.DE", "close_VW.DE", "adjClose_VW.DE", "volume_VW.DE").sortedArray()

        val expectedBody: List<List<CsvEntry>> = listOf(
            listOf(CsvEntry(data[3].time, TIMESTAMP_COLUMN_NAME, data[3].time.toEpochMilli().toBigDecimal()),   CsvEntry(data[3].time, "adjClose_SAP.DE", placeholderValue),    CsvEntry(data[3].time, "adjClose_VW.DE", data[3].adjClose), CsvEntry(data[3].time, "close_SAP.DE", placeholderValue), CsvEntry(data[3].time, "close_VW.DE",  data[3].close), CsvEntry(data[3].time, "high_SAP.DE",  placeholderValue), CsvEntry(data[3].time, "high_VW.DE",  data[3].high), CsvEntry(data[3].time, "low_SAP.DE",  placeholderValue), CsvEntry(data[3].time, "low_VW.DE",  data[3].low), CsvEntry(data[3].time, "open_SAP.DE",  placeholderValue), CsvEntry(data[3].time, "open_VW.DE",  data[3].open), CsvEntry(data[3].time, "volume_SAP.DE",  placeholderValue), CsvEntry(data[3].time, "volume_VW.DE",  data[3].volume?.toBigDecimal())),
            listOf(CsvEntry(data[2].time, TIMESTAMP_COLUMN_NAME, data[2].time.toEpochMilli().toBigDecimal()),   CsvEntry(data[2].time, "adjClose_SAP.DE", data[2].adjClose),    CsvEntry(data[2].time, "adjClose_VW.DE", placeholderValue), CsvEntry(data[2].time, "close_SAP.DE", data[2].close), CsvEntry(data[2].time, "close_VW.DE",  placeholderValue), CsvEntry(data[2].time, "high_SAP.DE",  data[2].high), CsvEntry(data[2].time, "high_VW.DE",  placeholderValue), CsvEntry(data[2].time, "low_SAP.DE",   data[2].low), CsvEntry(data[2].time, "low_VW.DE",  placeholderValue), CsvEntry(data[2].time, "open_SAP.DE",  data[2].open), CsvEntry(data[2].time, "open_VW.DE",  placeholderValue), CsvEntry(data[2].time, "volume_SAP.DE",  data[2].volume?.toBigDecimal()), CsvEntry(data[2].time, "volume_VW.DE",  placeholderValue)),
            listOf(CsvEntry(data[0].time, TIMESTAMP_COLUMN_NAME, data[0].time.toEpochMilli().toBigDecimal()),   CsvEntry(data[0].time, "adjClose_SAP.DE", data[0].adjClose),    CsvEntry(data[0].time, "adjClose_VW.DE", data[1].adjClose), CsvEntry(data[0].time, "close_SAP.DE", data[0].close), CsvEntry(data[0].time, "close_VW.DE",  data[1].close), CsvEntry(data[0].time, "high_SAP.DE",  data[0].high), CsvEntry(data[0].time, "high_VW.DE",  data[1].high), CsvEntry(data[0].time, "low_SAP.DE",  data[0].low), CsvEntry(data[0].time, "low_VW.DE",  data[1].low), CsvEntry(data[0].time, "open_SAP.DE",  data[0].open), CsvEntry(data[0].time, "open_VW.DE",  data[1].open), CsvEntry(data[0].time, "volume_SAP.DE",  data[0].volume!!.toBigDecimal()), CsvEntry(data[0].time, "volume_VW.DE",  data[1].volume?.toBigDecimal())),
        )

        val subject = TimeIndexedCSVBuilder(data.shuffled().toCSVEntryList(), MissingValueHandlingStrategy.IGNORE)
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody

        if(true){
            println("expectedHeader")
            println(expectedHeader.toString())
            println("actualHeader")
            println(actualHeader.toString())
            println("expectedBody:")
            printMultiDimensionalList(expectedBody)
            println("actualBody:")
            printMultiDimensionalList(actualBody)
        }

        Assertions.assertEquals(expectedBody.size, actualBody.size)
        Assertions.assertEquals(expectedHeader.size, actualHeader.size)

        for ((rowIndex, row) in expectedBody.withIndex()) {
            for ((columnIndex, _) in row.withIndex()) {
                Assertions.assertEquals(expectedBody[rowIndex][columnIndex], actualBody[rowIndex][columnIndex])
            }
        }

        for((index, _) in expectedHeader.withIndex()){
            Assertions.assertEquals(expectedHeader[index], actualHeader[index])
        }

    }

    @Test
    fun buildCSVOfAggregatedPriceInformationLastValueStrategyTest() {
        val now = Instant.now()

        val data = listOf(
            AggregatedPriceInformationResponse(now, "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformationResponse(now, "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005),
            AggregatedPriceInformationResponse(now.minusSeconds(10), "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformationResponse(now.minusSeconds(22), "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005)
        )

        val expectedHeader = arrayOf("timestamp") + arrayOf("open_SAP.DE", "high_SAP.DE","low_SAP.DE", "close_SAP.DE", "adjClose_SAP.DE", "volume_SAP.DE", "open_VW.DE", "high_VW.DE", "low_VW.DE", "close_VW.DE", "adjClose_VW.DE", "volume_VW.DE").sortedArray()


        val expectedBody: List<List<CsvEntry>> = listOf<List<CsvEntry>>(
            listOf(CsvEntry(data[3].time, TIMESTAMP_COLUMN_NAME, data[3].time.toEpochMilli().toBigDecimal()),   CsvEntry(data[3].time, "adjClose_SAP.DE", placeholderValue),    CsvEntry(data[3].time, "adjClose_VW.DE", data[3].adjClose), CsvEntry(data[3].time, "close_SAP.DE", placeholderValue), CsvEntry(data[3].time, "close_VW.DE",  data[3].close), CsvEntry(data[3].time, "high_SAP.DE",  placeholderValue), CsvEntry(data[3].time, "high_VW.DE",  data[3].high), CsvEntry(data[3].time, "low_SAP.DE",  placeholderValue), CsvEntry(data[3].time, "low_VW.DE",  data[3].low), CsvEntry(data[3].time, "open_SAP.DE",  placeholderValue), CsvEntry(data[3].time, "open_VW.DE",  data[3].open), CsvEntry(data[3].time, "volume_SAP.DE",  placeholderValue), CsvEntry(data[3].time, "volume_VW.DE",  data[3].volume?.toBigDecimal())),
            listOf(CsvEntry(data[2].time, TIMESTAMP_COLUMN_NAME, data[2].time.toEpochMilli().toBigDecimal()),   CsvEntry(data[2].time, "adjClose_SAP.DE", data[2].adjClose),    CsvEntry(data[2].time, "adjClose_VW.DE", data[1].adjClose), CsvEntry(data[2].time, "close_SAP.DE", data[2].close), CsvEntry(data[2].time, "close_VW.DE",  data[1].close), CsvEntry(data[2].time, "high_SAP.DE",  data[2].high), CsvEntry(data[2].time, "high_VW.DE",  data[1].high), CsvEntry(data[2].time, "low_SAP.DE",   data[2].low), CsvEntry(data[2].time, "low_VW.DE",  data[1].low), CsvEntry(data[2].time, "open_SAP.DE",  data[2].open), CsvEntry(data[2].time, "open_VW.DE",  data[1].open), CsvEntry(data[2].time, "volume_SAP.DE",  data[2].volume?.toBigDecimal()), CsvEntry(data[2].time, "volume_VW.DE",  data[3].volume?.toBigDecimal())),
            listOf(CsvEntry(data[0].time, TIMESTAMP_COLUMN_NAME, data[0].time.toEpochMilli().toBigDecimal()),   CsvEntry(data[0].time, "adjClose_SAP.DE", data[0].adjClose),    CsvEntry(data[0].time, "adjClose_VW.DE", data[1].adjClose), CsvEntry(data[0].time, "close_SAP.DE", data[0].close), CsvEntry(data[0].time, "close_VW.DE",  data[1].close), CsvEntry(data[0].time, "high_SAP.DE",  data[0].high), CsvEntry(data[0].time, "high_VW.DE",  data[1].high), CsvEntry(data[0].time, "low_SAP.DE",  data[2].low), CsvEntry(data[0].time, "low_VW.DE",  data[1].low), CsvEntry(data[0].time, "open_SAP.DE",  data[2].open), CsvEntry(data[0].time, "open_VW.DE",  data[1].open), CsvEntry(data[0].time, "volume_SAP.DE",  data[0].volume!!.toBigDecimal()), CsvEntry(data[0].time, "volume_VW.DE",  data[1].volume?.toBigDecimal())),
        )

        val subject = TimeIndexedCSVBuilder(data.shuffled().toCSVEntryList(), MissingValueHandlingStrategy.LAST_VALUE)
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody

        if(true){
            println("expectedHeader")
            println(expectedHeader.toString())
            println("actualHeader")
            println(actualHeader.toString())
            println("expectedBody:")
            printMultiDimensionalList(expectedBody)
            println("actualBody:")
            printMultiDimensionalList(actualBody)
        }

        Assertions.assertEquals(expectedBody.size, actualBody.size)
        Assertions.assertEquals(expectedHeader.size, actualHeader.size)

        for ((rowIndex, row) in expectedBody.withIndex()) {
            for ((columnIndex, _) in row.withIndex()) {
                Assertions.assertEquals(expectedBody[rowIndex][columnIndex], actualBody[rowIndex][columnIndex])
            }
        }

        for((index, _) in expectedHeader.withIndex()){
            Assertions.assertEquals(expectedHeader[index], actualHeader[index])
        }

    }

    @Test
    fun buildCSVOfAggregatedPriceInformationFillingMissingValues() {
        val now = Instant.now()

        val data = listOf(
            AggregatedPriceInformationResponse(now, "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformationResponse(now, "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005),
            AggregatedPriceInformationResponse(now.minusSeconds(10), "SAP.DE", BigDecimal(200), BigDecimal(201), BigDecimal(202), BigDecimal(203), BigDecimal(204), 205),
            AggregatedPriceInformationResponse(now.minusSeconds(22), "VW.DE", BigDecimal(3000), BigDecimal(3001), BigDecimal(3002), BigDecimal(3003), BigDecimal(3004), 3005)
        )
        val colNames = listOf( "open_SAP.DE", "high_SAP.DE","low_SAP.DE", "close_SAP.DE", "adjClose_SAP.DE", "volume_SAP.DE", "open_VW.DE", "high_VW.DE", "low_VW.DE", "close_VW.DE", "adjClose_VW.DE", "volume_VW.DE").sorted()
        val expectedHeader = mutableListOf(TIMESTAMP_COLUMN_NAME)
        expectedHeader.addAll(colNames)

        val expectedBody: List<List<CsvEntry>> = listOf(
            listOf(
                CsvEntry(data[3].time, TIMESTAMP_COLUMN_NAME, BigDecimal(data[3].time.toEpochMilli())),
                CsvEntry(data[3].time, "adjClose_SAP.DE",  data[2].adjClose),
                CsvEntry(data[3].time, "adjClose_VW.DE",  data[3].adjClose),
                CsvEntry(data[3].time, "close_SAP.DE",  data[2].close),
                CsvEntry(data[3].time, "close_VW.DE",  data[3].close),
                CsvEntry(data[3].time, "high_SAP.DE",  data[2].high),
                CsvEntry(data[3].time, "high_VW.DE",  data[3].high),
                CsvEntry(data[3].time, "low_SAP.DE",  data[2].low),
                CsvEntry(data[3].time, "low_VW.DE",  data[3].low),
                CsvEntry(data[3].time, "open_SAP.DE",  data[2].open),
                CsvEntry(data[3].time, "open_VW.DE",  data[3].open),
                CsvEntry(data[3].time, "volume_SAP.DE", data[2].volume?.toBigDecimal()),
                CsvEntry(data[3].time, "volume_VW.DE", data[3].volume?.toBigDecimal())
            ),
            listOf(
                CsvEntry(data[2].time, TIMESTAMP_COLUMN_NAME, BigDecimal(data[2].time.toEpochMilli())),
                CsvEntry(data[2].time, "adjClose_SAP.DE",  data[2].adjClose),
                CsvEntry(data[2].time, "adjClose_VW.DE",  data[1].adjClose),
                CsvEntry(data[2].time, "close_SAP.DE",  data[2].close),
                CsvEntry(data[2].time, "close_VW.DE",  data[1].close),
                CsvEntry(data[2].time, "high_SAP.DE",  data[2].high),
                CsvEntry(data[2].time, "high_VW.DE",  data[1].high),
                CsvEntry(data[2].time, "low_SAP.DE",  data[2].low),
                CsvEntry(data[2].time, "low_VW.DE",  data[1].low),
                CsvEntry(data[2].time, "open_SAP.DE",  data[2].open),
                CsvEntry(data[2].time, "open_VW.DE",  data[1].open),
                CsvEntry(data[2].time, "volume_SAP.DE", data[2].volume?.toBigDecimal()),
                CsvEntry(data[2].time, "volume_VW.DE", data[1].volume?.toBigDecimal()),
            ),
            listOf(
                CsvEntry(data[0].time, TIMESTAMP_COLUMN_NAME, BigDecimal(data[0].time.toEpochMilli())),
                CsvEntry(data[0].time, "adjClose_SAP.DE",  data[0].adjClose),
                CsvEntry(data[0].time, "adjClose_VW.DE",  data[1].adjClose),
                CsvEntry(data[0].time, "close_SAP.DE",  data[0].close),
                CsvEntry(data[0].time, "close_VW.DE",  data[1].close),
                CsvEntry(data[0].time, "high_SAP.DE",  data[0].high),
                CsvEntry(data[0].time, "high_VW.DE",  data[1].high),
                CsvEntry(data[0].time, "low_SAP.DE",  data[0].low),
                CsvEntry(data[0].time, "low_VW.DE",  data[1].low),
                CsvEntry(data[0].time, "open_SAP.DE",  data[0].open),
                CsvEntry(data[0].time, "open_VW.DE",  data[1].open),
                CsvEntry(data[0].time, "volume_SAP.DE", data[0].volume?.toBigDecimal()),
                CsvEntry(data[0].time, "volume_VW.DE", data[1].volume?.toBigDecimal()),
            ),
        )

        val subject = TimeIndexedCSVBuilder(data.shuffled().toCSVEntryList(), MissingValueHandlingStrategy.NEXT_MATCHING)
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody

        if(true){
            println("expectedHeader")
            println(expectedHeader.toString())
            println("actualHeader")
            println(actualHeader.toString())
            println("expectedBody:")
            printMultiDimensionalList(expectedBody)
            println("actualBody:")
            printMultiDimensionalList(actualBody)
        }

        Assertions.assertEquals(expectedBody.size, actualBody.size)
        Assertions.assertEquals(expectedHeader.size, actualHeader.size)

        for ((rowIndex, row) in expectedBody.withIndex()) {
            for ((columnIndex, _) in row.withIndex()) {
                Assertions.assertEquals(expectedBody[rowIndex][columnIndex], actualBody[rowIndex][columnIndex])
            }
        }

        for((index, _) in expectedHeader.withIndex()){
            Assertions.assertEquals(expectedHeader[index], actualHeader[index])
        }

    }

    @Test
    fun buildCSVOfAggregatedPriceInformationFillingMissingValuesNullString() {
        val now = Instant.now()

        val data = listOf(
            AggregatedPriceInformationResponse(now, "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformationResponse(now, "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005),
            AggregatedPriceInformationResponse(now.minusSeconds(10), "SAP.DE", BigDecimal(200), BigDecimal(201), BigDecimal(202), BigDecimal(203), BigDecimal(204), 205),
            AggregatedPriceInformationResponse(now.minusSeconds(22), "VW.DE", BigDecimal(3000), BigDecimal(3001), BigDecimal(3002), BigDecimal(3003), BigDecimal(3004), 3005)
        )
        val colNames = listOf( "open_SAP.DE", "high_SAP.DE","low_SAP.DE", "close_SAP.DE", "adjClose_SAP.DE", "volume_SAP.DE", "open_VW.DE", "high_VW.DE", "low_VW.DE", "close_VW.DE", "adjClose_VW.DE", "volume_VW.DE").sorted()
        val expectedHeader = mutableListOf("timestamp")
        expectedHeader.addAll(colNames)

        val expectedBody: List<List<CsvEntry>> = listOf(
            listOf(CsvEntry(data[3].time, TIMESTAMP_COLUMN_NAME, BigDecimal(data[3].time.toEpochMilli())), CsvEntry(data[3].time, "adjClose_SAP.DE",  data[2].adjClose), CsvEntry(data[3].time, "adjClose_VW.DE",  data[3].adjClose), CsvEntry(data[3].time, "close_SAP.DE",  data[2].close), CsvEntry(data[3].time, "close_VW.DE",  data[3].close), CsvEntry(data[3].time, "high_SAP.DE",  data[2].high), CsvEntry(data[3].time, "high_VW.DE",  data[3].high), CsvEntry(data[3].time, "low_SAP.DE",  data[2].low), CsvEntry(data[3].time, "low_VW.DE",  data[3].low), CsvEntry(data[3].time, "open_SAP.DE",  data[2].open), CsvEntry(data[3].time, "open_VW.DE",  data[3].open), CsvEntry(data[3].time, "volume_SAP.DE", data[2].volume?.toBigDecimal()), CsvEntry(data[3].time, "volume_VW.DE", data[3].volume?.toBigDecimal())),
            listOf(CsvEntry(data[2].time, TIMESTAMP_COLUMN_NAME, BigDecimal(data[2].time.toEpochMilli())), CsvEntry(data[2].time, "adjClose_SAP.DE",  data[2].adjClose), CsvEntry(data[2].time, "adjClose_VW.DE",  data[1].adjClose), CsvEntry(data[2].time, "close_SAP.DE",  data[2].close), CsvEntry(data[2].time, "close_VW.DE",  data[1].close), CsvEntry(data[2].time, "high_SAP.DE",  data[2].high), CsvEntry(data[2].time, "high_VW.DE",  data[1].high), CsvEntry(data[2].time, "low_SAP.DE",  data[2].low), CsvEntry(data[2].time, "low_VW.DE",  data[1].low), CsvEntry(data[2].time, "open_SAP.DE",  data[2].open), CsvEntry(data[2].time, "open_VW.DE",  data[1].open), CsvEntry(data[2].time, "volume_SAP.DE", data[2].volume?.toBigDecimal()), CsvEntry(data[2].time, "volume_VW.DE", data[1].volume?.toBigDecimal()),),
            listOf(CsvEntry(data[0].time, TIMESTAMP_COLUMN_NAME, BigDecimal(data[0].time.toEpochMilli())), CsvEntry(data[0].time, "adjClose_SAP.DE",  data[2].adjClose), CsvEntry(data[0].time, "adjClose_VW.DE",  data[1].adjClose), CsvEntry(data[0].time, "close_SAP.DE",  data[0].close), CsvEntry(data[0].time, "close_VW.DE",  data[1].close), CsvEntry(data[0].time, "high_SAP.DE",  data[0].high), CsvEntry(data[0].time, "high_VW.DE",  data[1].high), CsvEntry(data[0].time, "low_SAP.DE",  data[0].low), CsvEntry(data[0].time, "low_VW.DE",  data[1].low), CsvEntry(data[0].time, "open_SAP.DE",  data[0].open), CsvEntry(data[0].time, "open_VW.DE",  data[1].open), CsvEntry(data[0].time, "volume_SAP.DE", data[0].volume?.toBigDecimal()), CsvEntry(data[0].time, "volume_VW.DE", data[1].volume?.toBigDecimal()),),
        )

        val csvEntries = data.toCSVEntryList().toMutableList()
        val nullValueEntry = csvEntries.find { it.time == data[0].time && it.columnName ==  "adjClose_SAP.DE"}
        csvEntries.replaceAll {
            if(it == nullValueEntry){
                CsvEntry(nullValueEntry.time, nullValueEntry.columnName, PLACEHOLDER_VALUE)
            } else{
                it
            }
        }
        val subject = TimeIndexedCSVBuilder(csvEntries, MissingValueHandlingStrategy.NEXT_MATCHING)
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody

        if(true){
            println("expectedHeader")
            println(expectedHeader.toString())
            println("actualHeader")
            println(actualHeader.toString())
            println("expectedBody:")
            printMultiDimensionalList(expectedBody)
            println("actualBody:")
            printMultiDimensionalList(actualBody)
        }

        Assertions.assertEquals(expectedBody.size, actualBody.size)
        Assertions.assertEquals(expectedHeader.size, actualHeader.size)

        for ((rowIndex, row) in expectedBody.withIndex()) {
            for ((columnIndex, _) in row.withIndex()) {
                Assertions.assertEquals(expectedBody[rowIndex][columnIndex], actualBody[rowIndex][columnIndex])
            }
        }

        for((index, _) in expectedHeader.withIndex()){
            Assertions.assertEquals(expectedHeader[index], actualHeader[index])
        }

    }

    @Test
    fun buildCSVOfPrecisePriceInformation() {

        val now = Instant.now()

        val data = listOf(
            PrecisePriceInformationResponse(now, "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformationResponse(now, "TSLA", "NYSE", "OPEN", BigDecimal(1000)),
            PrecisePriceInformationResponse(now.minusSeconds(20), "VW.DE", "GER", "OPEN", BigDecimal(104)),
            PrecisePriceInformationResponse(now.minusSeconds(20), "SAP.DE", "GER", "OPEN", BigDecimal(110)),
            PrecisePriceInformationResponse(now.minusSeconds(30), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformationResponse(now.minusSeconds(30), "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformationResponse(now.minusSeconds(40), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformationResponse(now.minusSeconds(50), "VW.DE", "GER", "OPEN", BigDecimal(101)),
        )

        val subject = TimeIndexedCSVBuilder(data.shuffled().toCSVEntryList(), MissingValueHandlingStrategy.IGNORE)
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody
        val expectedHeader = arrayOf("timestamp") + arrayOf("SAP.DE", "VW.DE", "TSLA").sortedArray()

        val expectedBody: List<List<CsvEntry>> = listOf(
            listOf(CsvEntry(data[7].time, TIMESTAMP_COLUMN_NAME,    BigDecimal(data[7].time.toEpochMilli())),  CsvEntry(data[7]. time, data[5].symbol, placeholderValue),  CsvEntry(data[7].time, data[1].symbol, placeholderValue),  data[7].toCSVEntry()),
            listOf(CsvEntry(data[6].time, TIMESTAMP_COLUMN_NAME,    BigDecimal(data[6].time.toEpochMilli())),  CsvEntry(data[6]. time, data[5].symbol, placeholderValue),  CsvEntry(data[6].time, data[1].symbol, placeholderValue),  data[6].toCSVEntry()),
            listOf(CsvEntry(data[4].time, TIMESTAMP_COLUMN_NAME,    BigDecimal(data[4].time.toEpochMilli())),  data[5].toCSVEntry(),                                       CsvEntry(data[4].time, data[1].symbol, placeholderValue),  data[4].toCSVEntry()),
            listOf(CsvEntry(data[2].time, TIMESTAMP_COLUMN_NAME,    BigDecimal(data[2].time.toEpochMilli())),  data[3].toCSVEntry(),                                       CsvEntry(data[2].time, data[1].symbol, placeholderValue),  data[2].toCSVEntry()),
            listOf(CsvEntry(data[0].time, TIMESTAMP_COLUMN_NAME,    BigDecimal(data[0].time.toEpochMilli())),  data[0].toCSVEntry(),                                       data[1].toCSVEntry(),                                   CsvEntry(data[1].time, data[2].symbol, placeholderValue))
        )

        if(false){
            println("expectedHeader")
            println(expectedHeader.toString())
            println("actualHeader")
            println(actualHeader.toString())
            println("expectedBody:")
            printMultiDimensionalList(expectedBody)
            println("actualBody:")
            printMultiDimensionalList(actualBody)
        }

        Assertions.assertEquals(expectedBody.size, actualBody.size)
        Assertions.assertEquals(expectedHeader.size, actualHeader.size)

        for ((rowIndex, row) in expectedBody.withIndex()) {
            for ((columnIndex, _) in row.withIndex()) {
                Assertions.assertEquals(expectedBody[rowIndex][columnIndex], actualBody[rowIndex][columnIndex])
            }
        }

        for((index, _) in expectedHeader.withIndex()){
            Assertions.assertEquals(expectedHeader[index], actualHeader[index])
        }

    }

    @Test
    fun buildCSVOfPrecisePriceInformationFillingMissingValuesNextMatching() {

        val now = Instant.now()

        val data = listOf(
            PrecisePriceInformationResponse(now, "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformationResponse(now, "TSLA", "NYSE", "OPEN", BigDecimal(1000)),
            PrecisePriceInformationResponse(now.minusSeconds(20), "VW.DE", "GER", "OPEN", BigDecimal(104)),
            PrecisePriceInformationResponse(now.minusSeconds(20), "SAP.DE", "GER", "OPEN", BigDecimal(110)),
            PrecisePriceInformationResponse(now.minusSeconds(30), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformationResponse(now.minusSeconds(30), "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformationResponse(now.minusSeconds(40), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformationResponse(now.minusSeconds(50), "VW.DE", "GER", "OPEN", BigDecimal(101)),
        )

        val subject = TimeIndexedCSVBuilder(data.shuffled().toCSVEntryList(), MissingValueHandlingStrategy.NEXT_MATCHING)
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody
        val expectedHeader = listOf("timestamp", "SAP.DE", "TSLA", "VW.DE")
        val expectedBody: List<List<CsvEntry>> = listOf(
            listOf(CsvEntry(data[7].time, TIMESTAMP_COLUMN_NAME,    BigDecimal(data[7].time.toEpochMilli())),  CsvEntry(data[7]. time, data[5].symbol, data[5].price),  CsvEntry(data[7].time, data[1].symbol, data[1].price),  data[7].toCSVEntry()),
            listOf(CsvEntry(data[6].time, TIMESTAMP_COLUMN_NAME,    BigDecimal(data[6].time.toEpochMilli())),  CsvEntry(data[6]. time, data[5].symbol, data[5].price),  CsvEntry(data[6].time, data[1].symbol, data[1].price),  data[6].toCSVEntry()),
            listOf(CsvEntry(data[4].time, TIMESTAMP_COLUMN_NAME,    BigDecimal(data[4].time.toEpochMilli())),  data[5].toCSVEntry(),                                    CsvEntry(data[4].time, data[1].symbol, data[1].price),  data[4].toCSVEntry()),
            listOf(CsvEntry(data[2].time, TIMESTAMP_COLUMN_NAME,    BigDecimal(data[2].time.toEpochMilli())),  data[3].toCSVEntry(),                                    CsvEntry(data[2].time, data[1].symbol, data[1].price),  data[2].toCSVEntry()),
            listOf(CsvEntry(data[0].time, TIMESTAMP_COLUMN_NAME,    BigDecimal(data[0].time.toEpochMilli())),  data[0].toCSVEntry(),                                    data[1].toCSVEntry(),                                   CsvEntry(data[1].time, data[2].symbol, data[2].price))
        )

        if(true) {
            println("expectedHeader")
            println(expectedHeader.toString())
            println("actualHeader")
            println(actualHeader.toString())
            println("expectedBody:")
            printMultiDimensionalList(expectedBody)
            println("actualBody:")
            printMultiDimensionalList(actualBody)
        }

        Assertions.assertEquals(expectedBody.size, actualBody.size)
        Assertions.assertEquals(expectedHeader.size, actualHeader.size)

        for ((rowIndex, row) in expectedBody.withIndex()) {
            for ((columnIndex, _) in row.withIndex()) {
                Assertions.assertEquals(expectedBody[rowIndex][columnIndex], actualBody[rowIndex][columnIndex])
            }
        }

        for((index, _) in expectedHeader.withIndex()){
            Assertions.assertEquals(expectedHeader[index], actualHeader[index])
        }

    }

    @Test
    fun addColumnsTest(){

        val now = Instant.now()

        val data = listOf(
            PrecisePriceInformationResponse(now, "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformationResponse(now, "TSLA", "NYSE", "OPEN", BigDecimal(1000)),
            PrecisePriceInformationResponse(now.minusSeconds(20), "VW.DE", "GER", "OPEN", BigDecimal(104)),
            PrecisePriceInformationResponse(now.minusSeconds(20), "SAP.DE", "GER", "OPEN", BigDecimal(110)),
            PrecisePriceInformationResponse(now.minusSeconds(30), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformationResponse(now.minusSeconds(30), "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformationResponse(now.minusSeconds(40), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformationResponse(now.minusSeconds(50), "VW.DE", "GER", "OPEN", BigDecimal(101)),
        )

        val smaHeader = "SMA"
        val rsiHeader = "RSI"
        val columns = listOf(
            CsvEntry(now, rsiHeader, BigDecimal(10)),
            CsvEntry(now.minusSeconds(20), rsiHeader, BigDecimal(20)),
            CsvEntry(now.minusSeconds(20), smaHeader, BigDecimal(30)),
            CsvEntry(now.minusSeconds(50), smaHeader, BigDecimal(40)),
        )
        val subject = TimeIndexedCSVBuilder(data.toCSVEntryList(), MissingValueHandlingStrategy.IGNORE)
        subject.addColumns(columns)

        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody

        val expectedHeader = listOf(TIMESTAMP_COLUMN_NAME, "SAP.DE", "TSLA", "VW.DE", rsiHeader, smaHeader)

        val expectedBody: List<List<CsvEntry>> = listOf(
            listOf(CsvEntry(data[7].time, TIMESTAMP_COLUMN_NAME, data[7].time.toEpochMilli().toBigDecimal()),  CsvEntry(data[7].time, data[0].symbol, placeholderValue),   CsvEntry(data[7].time, data[1].symbol, placeholderValue),    CsvEntry(data[7].time, data[7].symbol, data[7].price),       CsvEntry(data[7].time, rsiHeader, columns[1].value),  CsvEntry(data[7].time, smaHeader, columns[3].value)),
            listOf(CsvEntry(data[6].time, TIMESTAMP_COLUMN_NAME, data[6].time.toEpochMilli().toBigDecimal()),  CsvEntry(data[6].time, data[0].symbol, placeholderValue),   CsvEntry(data[6].time, data[1].symbol, placeholderValue),    CsvEntry(data[6].time, data[7].symbol, data[6].price),       CsvEntry(data[6].time, rsiHeader, columns[1].value),  CsvEntry(data[6].time, smaHeader, columns[3].value)),
            listOf(CsvEntry(data[4].time, TIMESTAMP_COLUMN_NAME, data[4].time.toEpochMilli().toBigDecimal()),  CsvEntry(data[4].time, data[0].symbol, data[5].price),      CsvEntry(data[4].time, data[1].symbol, placeholderValue),    CsvEntry(data[4].time, data[7].symbol, data[4].price),       CsvEntry(data[4].time, rsiHeader, columns[1].value),  CsvEntry(data[4].time, smaHeader, columns[2].value)),
            listOf(CsvEntry(data[2].time, TIMESTAMP_COLUMN_NAME, data[2].time.toEpochMilli().toBigDecimal()),  CsvEntry(data[2].time, data[0].symbol, data[3].price),      CsvEntry(data[2].time, data[1].symbol, placeholderValue),    CsvEntry(data[2].time, data[7].symbol, data[2].price),       CsvEntry(data[2].time, rsiHeader, columns[1].value),  CsvEntry(data[2].time, smaHeader, columns[2].value)),
            listOf(CsvEntry(data[0].time, TIMESTAMP_COLUMN_NAME, data[0].time.toEpochMilli().toBigDecimal()),  CsvEntry(data[0].time, data[0].symbol, data[0].price),      CsvEntry(data[0].time, data[1].symbol, data[1].price),       CsvEntry(data[0].time, data[7].symbol, placeholderValue),    CsvEntry(data[0].time, rsiHeader, columns[0].value),  CsvEntry(data[0].time, smaHeader, columns[2].value))
        )

        if(false){
            println("expectedHeader")
            println(expectedHeader.toString())
            println("actualHeader")
            println(actualHeader.toString())
            println("expectedBody:")
            printMultiDimensionalList(expectedBody)
            println("actualBody:")
            printMultiDimensionalList(actualBody)
        }

        Assertions.assertEquals(expectedBody.size, actualBody.size)
        Assertions.assertEquals(expectedHeader.size, actualHeader.size)

        for ((rowIndex, row) in expectedBody.withIndex()) {
            for ((columnIndex, _) in row.withIndex()) {
                Assertions.assertEquals(expectedBody[rowIndex][columnIndex], actualBody[rowIndex][columnIndex])
            }
        }

        for((index, _) in expectedHeader.withIndex()){
            Assertions.assertEquals(expectedHeader[index], actualHeader[index])
        }
    }

    @Test
    fun addIndicatorTest(){
        // Prepare data
        val now = Instant.now()
        val symbol = "SAP.DE"
        val symbol1 = "VW.DE"
        val data = listOf(
            CsvEntry(now, symbol, BigDecimal(100)),
            CsvEntry(now.minusSeconds(10), symbol, BigDecimal(105)),
            CsvEntry(now.minusSeconds(20), symbol, BigDecimal(110)),
            CsvEntry(now.minusSeconds(30), symbol, BigDecimal(115)),
            CsvEntry(now.minusSeconds(40), symbol, BigDecimal(100)),
            CsvEntry(now.minusSeconds(50), symbol, BigDecimal(101)),
            CsvEntry(now.minusSeconds(60), symbol, BigDecimal(102)),
            CsvEntry(now.minusSeconds(70), symbol, BigDecimal(103)),
            CsvEntry(now.minusSeconds(80), symbol, BigDecimal(104)),
            CsvEntry(now.minusSeconds(90), symbol, BigDecimal(105)),
            CsvEntry(now.minusSeconds(100), symbol, BigDecimal(106)),
            CsvEntry(now.minusSeconds(110), symbol, BigDecimal(107)),
            CsvEntry(now.minusSeconds(120), symbol, BigDecimal(108)),
            CsvEntry(now.minusSeconds(130), symbol, BigDecimal(109)),
            CsvEntry(now.minusSeconds(140), symbol, BigDecimal(110)),
            CsvEntry(now.minusSeconds(150), symbol, BigDecimal(110)),
            CsvEntry(now.minusSeconds(160), symbol, BigDecimal(110)),
            CsvEntry(now.minusSeconds(170), symbol,BigDecimal(110)),
            CsvEntry(now, symbol1, BigDecimal(200)),
            CsvEntry(now.minusSeconds(10), symbol1, BigDecimal(205)),
            CsvEntry(now.minusSeconds(20), symbol1, BigDecimal(220)),
            CsvEntry(now.minusSeconds(30), symbol1, BigDecimal(215)),
            CsvEntry(now.minusSeconds(40), symbol1, BigDecimal(200)),
            CsvEntry(now.minusSeconds(50), symbol1, BigDecimal(201)),
            CsvEntry(now.minusSeconds(60), symbol1, BigDecimal(222)),
            CsvEntry(now.minusSeconds(70), symbol1, BigDecimal(203)),
            CsvEntry(now.minusSeconds(80), symbol1, BigDecimal(204)),
            CsvEntry(now.minusSeconds(90), symbol1, BigDecimal(205)),
            CsvEntry(now.minusSeconds(100), symbol1, BigDecimal(226)),
            CsvEntry(now.minusSeconds(110), symbol1, BigDecimal(227)),
            CsvEntry(now.minusSeconds(120), symbol1, BigDecimal(238)),
            CsvEntry(now.minusSeconds(130), symbol1, BigDecimal(239)),
            CsvEntry(now.minusSeconds(140), symbol1, BigDecimal(120)),
            CsvEntry(now.minusSeconds(150), symbol1, BigDecimal(210)),
            CsvEntry(now.minusSeconds(160), symbol1, BigDecimal(110)),
            CsvEntry(now.minusSeconds(170), symbol1,BigDecimal(210))
        )

        val smaResults = listOf(
            listOf(
                Indicator(data[0].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(105.36, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[1].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(106.07, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[2].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(106.43, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[3].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(106.43, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[4].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(106.07, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[5].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[6].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[7].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[8].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[9].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[10].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[11].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[12].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[13].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[14].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[15].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[16].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[17].time, symbol, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros())
            ),
            listOf(
                Indicator(data[18].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(214.64, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[19].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(208.93, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[20].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(209.29, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[21].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(201.43, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[22].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(201.07, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[23].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[24].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[25].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[26].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[27].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[28].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[29].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[30].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[31].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[32].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[33].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[34].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[35].time, symbol1, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros())
            )
        )

        val expectedList = smaResults.map { list ->
            list.map {
                    indicator -> CsvEntry(indicator.time, "SMA_${indicator.symbol}", indicator.value)
            }.sortedBy { it.time }
        }

        val subject = TimeIndexedCSVBuilder(data.shuffled(), MissingValueHandlingStrategy.LAST_VALUE)
        subject.addIndicator(IndicatorName.SMA, listOf("SAP.DE", "VW.DE")){ list ->
            return@addIndicator smaForAllOfASymbol(list)
        }

        val actualSMASymbol = subject.getColumn("SMA_SAP.DE")
        val actualSMASymbol1 = subject.getColumn("SMA_VW.DE")
        val actualList = listOf(actualSMASymbol, actualSMASymbol1)

        if(false){
            println("actualHeader:")
            println(subject.csvHeader.toString())
            println("actualBody:")
            printMultiDimensionalList(subject.csvBody)
            println("expectedList:")
            printMultiDimensionalList(expectedList)
            println("actualList:")
            printMultiDimensionalList(actualList)
        }

        // Test
        for ((subListNum, sublist) in expectedList.withIndex()){
            //println(actualList[subListNum].toString())
            for((index, _) in sublist.withIndex()){
                Assertions.assertEquals(expectedList[subListNum][index].time, actualList[subListNum][index].time)
                Assertions.assertEquals(expectedList[subListNum][index].columnName, actualList[subListNum][index].columnName)
                Assertions.assertEquals(expectedList[subListNum][index].value, actualList[subListNum][index].value)
            }
            Assertions.assertEquals(expectedList[subListNum].size, actualList[subListNum].size)
        }
        Assertions.assertEquals(expectedList.size, actualList.size)

    }

    @Test
    fun getColumnTest(){
        val now = Instant.now()

        val data = listOf(
            PrecisePriceInformationResponse(now, "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformationResponse(now, "TSLA", "NYSE", "OPEN", BigDecimal(1000)),
            PrecisePriceInformationResponse(now.minusSeconds(20), "VW.DE", "GER", "OPEN", BigDecimal(104)),
            PrecisePriceInformationResponse(now.minusSeconds(20), "SAP.DE", "GER", "OPEN", BigDecimal(110)),
            PrecisePriceInformationResponse(now.minusSeconds(30), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformationResponse(now.minusSeconds(30), "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformationResponse(now.minusSeconds(40), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformationResponse(now.minusSeconds(50), "VW.DE", "GER", "OPEN", BigDecimal(101)),
        ).toCSVEntryList()

        val expected = listOf(
            CsvEntry(data[6].time, "SAP.DE",null),
            CsvEntry(data[7].time, "SAP.DE", null),
            CsvEntry(data[0].time, "SAP.DE", data[0].value),
            CsvEntry(data[3].time, "SAP.DE", data[3].value),
            CsvEntry(data[5].time, "SAP.DE", data[5].value),
        ).sortedBy { it.time }
        val actual = TimeIndexedCSVBuilder(data, MissingValueHandlingStrategy.LAST_VALUE).getColumn("SAP.DE")
        if(true){
            println(expected.toString())
            println(actual.toString())
        }
        Assertions.assertEquals(expected, actual)
    }


    @Test
    fun findClosestEntryTest(){
        val instant = Instant.now().minusSeconds(5000)
        val list = listOf(
            CsvEntry(instant, "a", PLACEHOLDER_VALUE),
            CsvEntry(instant.minusSeconds(1000), "b", BigDecimal(10)),
            CsvEntry(instant.plusSeconds(10), "c", BigDecimal(20))
        )
        val actual = TimeIndexedCSVBuilder(listOf(), MissingValueHandlingStrategy.IGNORE).findClosestEntryMissingValueStrategy(
            instant.plusSeconds(5), "c" ,list, false
        )
        val expected = CsvEntry(instant.plusSeconds(5), list[2].columnName, list[2].value)

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun findLastEntryTest(){
        val instant = Instant.now().minusSeconds(5000)
        val list = listOf(
            listOf(CsvEntry(instant, "a", BigDecimal(1)), CsvEntry(instant, "b", BigDecimal(2)), CsvEntry(instant, "c", BigDecimal(3))),
            listOf(CsvEntry(instant, "a", BigDecimal(4)), CsvEntry(instant, "b", PLACEHOLDER_VALUE), CsvEntry(instant, "c", BigDecimal(6))),
            listOf(CsvEntry(instant, "a", BigDecimal(7)), CsvEntry(instant, "a", BigDecimal(8)), CsvEntry(instant, "a", BigDecimal(9)))
        )
        val actual = TimeIndexedCSVBuilder(listOf(), MissingValueHandlingStrategy.IGNORE).findLastEntryMissingValueStrategy(1, 1, "b",instant, list)
        val expected = list[0][1]
        Assertions.assertEquals(expected, actual)
    }
    @Test
    fun findLastEntryNoneExistingTest(){
        val instant = Instant.now().minusSeconds(5000)
        val list = listOf(
            listOf(CsvEntry(instant, "a", BigDecimal(1)), CsvEntry(instant, "b", BigDecimal(2)), CsvEntry(instant, "c", BigDecimal(3))),
            listOf(CsvEntry(instant, "a", BigDecimal(4)), CsvEntry(instant, "b", PLACEHOLDER_VALUE), CsvEntry(instant, "c", BigDecimal(6))),
            listOf(CsvEntry(instant, "a", BigDecimal(7)), CsvEntry(instant, "a", BigDecimal(8)), CsvEntry(instant, "a", BigDecimal(9)))
        )
        val actual = TimeIndexedCSVBuilder(listOf(), MissingValueHandlingStrategy.IGNORE).findLastEntryMissingValueStrategy(1, 1, "b",instant, list)
        val expected = list[0][1]
        Assertions.assertEquals(expected, actual)
    }

}