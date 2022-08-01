package com.mystockdata.composerservice.csv

import com.mystockdata.composerservice.IndicatorName
import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilderConstants.PLACEHOLDER_VALUE
import com.mystockdata.composerservice.printMultiDimensionalList
import com.mystockdata.composerservice.stockdata.AggregatedPriceInformationResponse
import com.mystockdata.composerservice.stockdata.PrecisePriceInformationResponse
import com.mystockdata.composerservice.technicalindicators.TechnicalIndicator
import com.mystockdata.composerservice.technicalindicators.smaForAllOfASymbol
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

        val expectedBody: List<List<BigDecimal?>> = listOf(
            listOf<BigDecimal?>(data[3].time.toEpochMilli().toBigDecimal(), placeholderValue, data[3].adjClose, placeholderValue, data[3].close, placeholderValue, data[3].high, placeholderValue, data[3].low, placeholderValue, data[3].open, placeholderValue, data[3].volume?.toBigDecimal()),
            listOf<BigDecimal?>(data[2].time.toEpochMilli().toBigDecimal(), data[2].adjClose, placeholderValue, data[2].close, placeholderValue, data[2].high, placeholderValue, data[2].low, placeholderValue, data[2].open, placeholderValue, data[2].volume?.toBigDecimal(), placeholderValue),
            listOf<BigDecimal?>(data[0].time.toEpochMilli().toBigDecimal(), data[0].adjClose, data[1].adjClose, data[0].close, data[1].close, data[0].high, data[1].high, data[0].low, data[1].low, data[0].open, data[1].open, data[0].volume?.toBigDecimal(), data[1].volume?.toBigDecimal()),
        )

        val subject = TimeIndexedCSVBuilder(aggregatedPriceInformationResponseToCSVEntry(data.shuffled()), MissingValueHandlingStrategy.IGNORE)
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody

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
    fun buildCSVOfAggregatedPriceInformationLastValueStrategyTest() {
        val now = Instant.now()

        val data = listOf(
            AggregatedPriceInformationResponse(now, "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformationResponse(now, "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005),
            AggregatedPriceInformationResponse(now.minusSeconds(10), "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformationResponse(now.minusSeconds(22), "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005)
        )

        val expectedHeader = arrayOf("timestamp") + arrayOf("open_SAP.DE", "high_SAP.DE","low_SAP.DE", "close_SAP.DE", "adjClose_SAP.DE", "volume_SAP.DE", "open_VW.DE", "high_VW.DE", "low_VW.DE", "close_VW.DE", "adjClose_VW.DE", "volume_VW.DE").sortedArray()

        val expectedBody: List<List<BigDecimal?>> = listOf(
            listOf(data[3].time.toEpochMilli().toBigDecimal(), placeholderValue, data[3].adjClose, placeholderValue, data[3].close, placeholderValue, data[3].high, placeholderValue, data[3].low, placeholderValue, data[3].open, placeholderValue, data[3].volume?.toBigDecimal()),
            listOf(data[2].time.toEpochMilli().toBigDecimal(), data[2].adjClose, data[1].adjClose, data[2].close,  data[1].close, data[2].high, data[1].high, data[2].low, data[1].low, data[2].open, data[1].open, data[2].volume?.toBigDecimal(), data[1].volume?.toBigDecimal()),
            listOf(data[0].time.toEpochMilli().toBigDecimal(), data[0].adjClose, data[1].adjClose, data[0].close, data[1].close, data[0].high, data[1].high, data[0].low, data[1].low, data[0].open, data[1].open, data[0].volume?.toBigDecimal(), data[1].volume?.toBigDecimal()),
        )

        val subject = TimeIndexedCSVBuilder(aggregatedPriceInformationResponseToCSVEntry(data.shuffled()), MissingValueHandlingStrategy.LAST_VALUE)
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody

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
    fun buildCSVOfAggregatedPriceInformationFillingMissingValues() {
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

        val expectedBody: List<List<BigDecimal?>> = listOf(
            listOf(BigDecimal(data[3].time.toEpochMilli()), data[2].adjClose, data[3].adjClose, data[2].close, data[3].close, data[2].high, data[3].high, data[2].low, data[3].low, data[2].open, data[3].open, data[2].volume?.toBigDecimal(), data[3].volume?.toBigDecimal()),
            listOf(BigDecimal(data[2].time.toEpochMilli()), data[2].adjClose, data[1].adjClose, data[2].close, data[1].close, data[2].high, data[1].high, data[2].low, data[1].low, data[2].open, data[1].open, data[2].volume?.toBigDecimal(), data[1].volume?.toBigDecimal()),
            listOf(BigDecimal(data[0].time.toEpochMilli()), data[0].adjClose, data[1].adjClose, data[0].close, data[1].close, data[0].high, data[1].high, data[0].low, data[1].low, data[0].open, data[1].open, data[0].volume?.toBigDecimal(), data[1].volume?.toBigDecimal()),
        )

        val subject = TimeIndexedCSVBuilder(aggregatedPriceInformationResponseToCSVEntry(data.shuffled()), MissingValueHandlingStrategy.NEXT_MATCHING)
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody

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

        val expectedBody: List<List<BigDecimal?>> = listOf(
            listOf(data[3].time.toEpochMilli().toBigDecimal(), data[2].adjClose, data[3].adjClose, data[2].close, data[3].close, data[2].high, data[3].high, data[2].low, data[3].low, data[2].open, data[3].open, data[2].volume?.toBigDecimal(), data[3].volume?.toBigDecimal()),
            listOf(data[2].time.toEpochMilli().toBigDecimal(), data[2].adjClose, data[1].adjClose, data[2].close, data[1].close, data[2].high, data[1].high, data[2].low, data[1].low, data[2].open, data[1].open, data[2].volume?.toBigDecimal(), data[1].volume?.toBigDecimal()),
            listOf(data[0].time.toEpochMilli().toBigDecimal(), data[2].adjClose, data[1].adjClose, data[0].close, data[1].close, data[0].high, data[1].high, data[0].low, data[1].low, data[0].open, data[1].open, data[0].volume?.toBigDecimal(), data[1].volume?.toBigDecimal())
        )

        val csvEntries = aggregatedPriceInformationResponseToCSVEntry(data).toMutableList()
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

        val subject = TimeIndexedCSVBuilder(precisePriceInformationResponseToCSV(data.shuffled()), MissingValueHandlingStrategy.IGNORE)
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody
        val expectedHeader = arrayOf("timestamp") + arrayOf("SAP.DE", "VW.DE", "TSLA").sortedArray()
        val expectedBody: List<List<BigDecimal?>> = listOf(
            listOf(data[7].time.toEpochMilli().toBigDecimal(), placeholderValue, placeholderValue, data[7].price),
            listOf(data[6].time.toEpochMilli().toBigDecimal(), placeholderValue, placeholderValue, data[6].price),
            listOf(data[4].time.toEpochMilli().toBigDecimal(), data[5].price, placeholderValue, data[4].price),
            listOf(data[2].time.toEpochMilli().toBigDecimal(), data[3].price, placeholderValue, data[2].price),
            listOf(data[0].time.toEpochMilli().toBigDecimal(), data[0].price, data[1].price, placeholderValue),
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

        val subject = TimeIndexedCSVBuilder(precisePriceInformationResponseToCSV(data.shuffled()), MissingValueHandlingStrategy.NEXT_MATCHING)
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody
        val expectedHeader = listOf("timestamp", "SAP.DE", "TSLA", "VW.DE")
        val expectedBody: List<List<BigDecimal?>> = listOf(
            listOf(BigDecimal(data[7].time.toEpochMilli()), data[5].price, data[1].price, data[7].price),
            listOf(BigDecimal(data[6].time.toEpochMilli()), data[5].price, data[1].price, data[6].price),
            listOf(BigDecimal(data[4].time.toEpochMilli()), data[5].price, data[1].price, data[4].price),
            listOf(BigDecimal(data[2].time.toEpochMilli()), data[3].price, data[1].price, data[2].price),
            listOf(BigDecimal(data[0].time.toEpochMilli()), data[0].price, data[1].price, data[2].price)
        )

        if(false) {
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
        val subject = TimeIndexedCSVBuilder(precisePriceInformationResponseToCSV(data), MissingValueHandlingStrategy.IGNORE)
        subject.addColumns(columns)

        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody

        val expectedHeader = listOf("timestamp", "SAP.DE", "TSLA", "VW.DE", rsiHeader, smaHeader)
        val expectedBody: List<List<BigDecimal?>> = listOf(
            listOf(data[7].time.toEpochMilli().toBigDecimal(), placeholderValue, placeholderValue, data[7].price, columns[1].value!!, columns[3].value!!),
            listOf(data[6].time.toEpochMilli().toBigDecimal(), placeholderValue, placeholderValue, data[6].price, columns[1].value!!, columns[3].value!!),
            listOf(data[4].time.toEpochMilli().toBigDecimal(), data[5].price, placeholderValue, data[4].price, columns[1].value!!, columns[2].value!!),
            listOf(data[2].time.toEpochMilli().toBigDecimal(), data[3].price, placeholderValue, data[2].price, columns[1].value!!, columns[2].value!!),
            listOf(data[0].time.toEpochMilli().toBigDecimal(), data[0].price, data[1].price, placeholderValue, columns[0].value!!, columns[2].value!!)
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
                TechnicalIndicator(data[0].time, symbol, IndicatorName.SMA, BigDecimal(105.36, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[1].time, symbol, IndicatorName.SMA, BigDecimal(106.07, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[2].time, symbol, IndicatorName.SMA, BigDecimal(106.43, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[3].time, symbol, IndicatorName.SMA, BigDecimal(106.43, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[4].time, symbol, IndicatorName.SMA, BigDecimal(106.07, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[5].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[6].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[7].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[8].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[9].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[10].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[11].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[12].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[13].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[14].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[15].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[16].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[17].time, symbol, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros())
            ),
            listOf(
                TechnicalIndicator(data[18].time, symbol1, IndicatorName.SMA, BigDecimal(214.64, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[19].time, symbol1, IndicatorName.SMA, BigDecimal(208.93, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[20].time, symbol1, IndicatorName.SMA, BigDecimal(209.29, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[21].time, symbol1, IndicatorName.SMA, BigDecimal(201.43, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[22].time, symbol1, IndicatorName.SMA, BigDecimal(201.07, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[23].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[24].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[25].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[26].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[27].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[28].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[29].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[30].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[31].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[32].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[33].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[34].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[35].time, symbol1, IndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros())
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
        )

        val expected = listOf(
            PrecisePriceInformationResponse(data[6].time, "SAP.DE", "GER", "OPEN", null),
            PrecisePriceInformationResponse(data[7].time, "SAP.DE", "GER", "OPEN", null),
            data[0],
            data[3],
            data[5]
        ).map { CsvEntry(it.time, it.symbol, it.price) }
            .sortedBy { it.time }
        val actual = TimeIndexedCSVBuilder(data.map { CsvEntry(it.time, it.symbol, it.price) }, MissingValueHandlingStrategy.LAST_VALUE).getColumn("SAP.DE")
        Assertions.assertEquals(expected, actual)
    }


    @Test
    fun findClosestEntryTest(){
        val instant = Instant.now().minusSeconds(5000)
        val list = listOf(
            CsvEntry(instant, "colName", PLACEHOLDER_VALUE),
            CsvEntry(instant.minusSeconds(1000), "colName", BigDecimal(10)),
            CsvEntry(instant.plusSeconds(10), "colName", BigDecimal(20))
        )
        val actual = TimeIndexedCSVBuilder(listOf(), MissingValueHandlingStrategy.IGNORE).findClosestEntryMissingValueStrategy(instant.plusSeconds(5), list, false)
        val expected = list[2]

        Assertions.assertEquals(expected.value, actual)
    }

    @Test
    fun findLastEntryTest(){
        val list = listOf(
            listOf(BigDecimal(1), BigDecimal(1), BigDecimal(1)),
            listOf(BigDecimal(1), PLACEHOLDER_VALUE, BigDecimal(1)),
            listOf(BigDecimal(1), BigDecimal(1), BigDecimal(1))
        )
        val actual = TimeIndexedCSVBuilder(listOf(), MissingValueHandlingStrategy.IGNORE).findLastEntryMissingValueStrategy(1, 1, list)
        val expected = list[0][1]
        Assertions.assertEquals(expected, actual)
    }
    @Test
    fun findLastEntryNoneExistingTest(){
        val list = listOf(
            listOf(BigDecimal(1), BigDecimal(100), BigDecimal(1)),
            listOf(BigDecimal(1), PLACEHOLDER_VALUE, BigDecimal(1)),
            listOf(BigDecimal(1), BigDecimal(2), BigDecimal(1))
        )
        val actual = TimeIndexedCSVBuilder(listOf(), MissingValueHandlingStrategy.IGNORE).findLastEntryMissingValueStrategy(1, 1, list)
        val expected = list[0][1]
        Assertions.assertEquals(expected, actual)
    }

}