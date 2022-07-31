package com.mystockdata.composerservice

import com.mystockdata.composerservice.stockdata.AggregatedPriceInformation
import com.mystockdata.composerservice.stockdata.PrecisePriceInformation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class TimeIndexedCSVBuilderTest {
    private val placeholder = "null"

    @Test
    fun buildCSVOfAggregatedPriceInformation() {
        val now = Instant.now()

        val data = listOf(
            AggregatedPriceInformation(now, "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformation(now, "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005),
            AggregatedPriceInformation(now.minusSeconds(10), "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformation(now.minusSeconds(22), "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005)
        )

        val expectedHeader = arrayOf("timestamp") + arrayOf("open_SAP.DE", "high_SAP.DE","low_SAP.DE", "close_SAP.DE", "adjClose_SAP.DE", "volume_SAP.DE", "open_VW.DE", "high_VW.DE", "low_VW.DE", "close_VW.DE", "adjClose_VW.DE", "volume_VW.DE").sortedArray()

        val expectedBody: List<List<String>> = listOf(
            listOf(data[0].time.toString(), data[0].adjClose.toString(), data[1].adjClose.toString(), data[0].close.toString(), data[1].close.toString(), data[0].high.toString(), data[1].high.toString(), data[0].low.toString(), data[1].low.toString(), data[0].open.toString(), data[1].open.toString(), data[0].volume.toString(), data[1].volume.toString()),
            listOf(data[2].time.toString(), data[2].adjClose.toString(), placeholder, data[2].close.toString(), placeholder, data[2].high.toString(), placeholder, data[2].low.toString(), placeholder, data[2].open.toString(), placeholder, data[2].volume.toString(), placeholder),
            listOf(data[3].time.toString(), placeholder, data[3].adjClose.toString(), placeholder, data[3].close.toString(), placeholder, data[3].high.toString(), placeholder, data[3].low.toString(), placeholder, data[3].open.toString(), placeholder, data[3].volume.toString()),
        )

        val subject = TimeIndexedCSVBuilder(aggregatedPriceInformationResponseToCSVEntry(data.shuffled()))
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
            AggregatedPriceInformation(now, "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformation(now, "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005),
            AggregatedPriceInformation(now.minusSeconds(10), "SAP.DE", BigDecimal(200), BigDecimal(201), BigDecimal(202), BigDecimal(203), BigDecimal(204), 205),
            AggregatedPriceInformation(now.minusSeconds(22), "VW.DE", BigDecimal(3000), BigDecimal(3001), BigDecimal(3002), BigDecimal(3003), BigDecimal(3004), 3005)
        )
        val colNames = listOf( "open_SAP.DE", "high_SAP.DE","low_SAP.DE", "close_SAP.DE", "adjClose_SAP.DE", "volume_SAP.DE", "open_VW.DE", "high_VW.DE", "low_VW.DE", "close_VW.DE", "adjClose_VW.DE", "volume_VW.DE").sorted()
        val expectedHeader = mutableListOf("timestamp")
        expectedHeader.addAll(colNames)

        val expectedBody: List<List<String>> = listOf(
            listOf(data[0].time.toString(), data[0].adjClose.toString(), data[1].adjClose.toString(), data[0].close.toString(), data[1].close.toString(), data[0].high.toString(), data[1].high.toString(), data[0].low.toString(), data[1].low.toString(), data[0].open.toString(), data[1].open.toString(), data[0].volume.toString(), data[1].volume.toString()),
            listOf(data[2].time.toString(), data[2].adjClose.toString(), data[1].adjClose.toString(), data[2].close.toString(), data[1].close.toString(), data[2].high.toString(), data[1].high.toString(), data[2].low.toString(), data[1].low.toString(), data[2].open.toString(), data[1].open.toString(), data[2].volume.toString(), data[1].volume.toString()),
            listOf(data[3].time.toString(), data[2].adjClose.toString(), data[3].adjClose.toString(), data[2].close.toString(), data[3].close.toString(), data[2].high.toString(), data[3].high.toString(), data[2].low.toString(), data[3].low.toString(), data[2].open.toString(), data[3].open.toString(), data[2].volume.toString(), data[3].volume.toString()),
        )

        val subject = TimeIndexedCSVBuilder(aggregatedPriceInformationResponseToCSVEntry(data.shuffled()), true)
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
            PrecisePriceInformation(now, "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformation(now, "TSLA", "NYSE", "OPEN", BigDecimal(1000)),
            PrecisePriceInformation(now.minusSeconds(20), "VW.DE", "GER", "OPEN", BigDecimal(104)),
            PrecisePriceInformation(now.minusSeconds(20), "SAP.DE", "GER", "OPEN", BigDecimal(110)),
            PrecisePriceInformation(now.minusSeconds(30), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformation(now.minusSeconds(30), "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformation(now.minusSeconds(40), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformation(now.minusSeconds(50), "VW.DE", "GER", "OPEN", BigDecimal(101)),
        )

        val subject = TimeIndexedCSVBuilder(precisePriceInformationResponseToCSV(data.shuffled()))
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody
        val expectedHeader = arrayOf("timestamp") + arrayOf("SAP.DE", "VW.DE", "TSLA").sortedArray()
        val expectedBody: List<List<String>> = listOf(
            listOf(data[0].time.toString(), data[0].price.toString(), data[1].price.toString(), placeholder),
            listOf(data[2].time.toString(), data[3].price.toString(), placeholder, data[2].price.toString()),
            listOf(data[4].time.toString(), data[5].price.toString(), placeholder, data[4].price.toString()),
            listOf(data[6].time.toString(), placeholder, placeholder, data[6].price.toString()),
            listOf(data[7].time.toString(), placeholder, placeholder, data[7].price.toString())
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
    fun buildCSVOfPrecisePriceInformationFillingMissingValues() {

        val now = Instant.now()

        val data = listOf(
            PrecisePriceInformation(now, "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformation(now, "TSLA", "NYSE", "OPEN", BigDecimal(1000)),
            PrecisePriceInformation(now.minusSeconds(20), "VW.DE", "GER", "OPEN", BigDecimal(104)),
            PrecisePriceInformation(now.minusSeconds(20), "SAP.DE", "GER", "OPEN", BigDecimal(110)),
            PrecisePriceInformation(now.minusSeconds(30), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformation(now.minusSeconds(30), "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformation(now.minusSeconds(40), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformation(now.minusSeconds(50), "VW.DE", "GER", "OPEN", BigDecimal(101)),
        )

        val subject = TimeIndexedCSVBuilder(precisePriceInformationResponseToCSV(data.shuffled()), true)
        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody
        val expectedHeader = arrayOf("timestamp") + arrayOf("SAP.DE", "VW.DE", "TSLA").sortedArray()
        val expectedBody: List<List<String>> = listOf(
            listOf(data[0].time.toString(), data[0].price.toString(), data[1].price.toString(), data[2].price.toString()),
            listOf(data[2].time.toString(), data[3].price.toString(), data[1].price.toString(), data[2].price.toString()),
            listOf(data[4].time.toString(), data[5].price.toString(), data[1].price.toString(), data[4].price.toString()),
            listOf(data[6].time.toString(), data[5].price.toString(), data[1].price.toString(), data[6].price.toString()),
            listOf(data[7].time.toString(), data[5].price.toString(), data[1].price.toString(), data[7].price.toString())
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
    fun addColumnsTest(){

        val now = Instant.now()

        val data = listOf(
            PrecisePriceInformation(now, "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformation(now, "TSLA", "NYSE", "OPEN", BigDecimal(1000)),
            PrecisePriceInformation(now.minusSeconds(20), "VW.DE", "GER", "OPEN", BigDecimal(104)),
            PrecisePriceInformation(now.minusSeconds(20), "SAP.DE", "GER", "OPEN", BigDecimal(110)),
            PrecisePriceInformation(now.minusSeconds(30), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformation(now.minusSeconds(30), "SAP.DE", "GER", "OPEN", BigDecimal(190)),
            PrecisePriceInformation(now.minusSeconds(40), "VW.DE", "GER", "OPEN", BigDecimal(102)),
            PrecisePriceInformation(now.minusSeconds(50), "VW.DE", "GER", "OPEN", BigDecimal(101)),
        )

        val smaHeader = "SMA"
        val rsiHeader = "RSI"
        val columns = listOf(
            CsvEntry(now, rsiHeader, "10"),
            CsvEntry(now.minusSeconds(20), rsiHeader, "20"),
            CsvEntry(now.minusSeconds(20), smaHeader, "30"),
            CsvEntry(now.minusSeconds(50), smaHeader, "40"),
        )
        val subject = TimeIndexedCSVBuilder(precisePriceInformationResponseToCSV(data))
        subject.addColumns(columns)

        val actualHeader = subject.csvHeader
        val actualBody = subject.csvBody

        val expectedHeader = listOf("timestamp", "SAP.DE", "TSLA", "VW.DE", rsiHeader, smaHeader)
        val expectedBody: List<List<String>> = listOf(
            listOf(data[0].time.toString(), data[0].price.toString(), data[1].price.toString(), placeholder, columns[0].value!!, columns[2].value!!),
            listOf(data[2].time.toString(), data[3].price.toString(), placeholder, data[2].price.toString(), columns[1].value!!, columns[2].value!!),
            listOf(data[4].time.toString(), data[5].price.toString(), placeholder, data[4].price.toString(), columns[1].value!!, columns[2].value!!),
            listOf(data[6].time.toString(), placeholder, placeholder, data[6].price.toString(), columns[1].value!!, columns[3].value!!),
            listOf(data[7].time.toString(), placeholder, placeholder, data[7].price.toString(), columns[1].value!!, columns[3].value!!)
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
    fun findClosestEntryTest(){
        val instant = Instant.now().minusSeconds(5000)
        val list = listOf(
            CsvEntry(instant, "colName", "value"),
            CsvEntry(instant.minusSeconds(1000), "colName", "value1"),
            CsvEntry(instant.plusSeconds(10), "colName", "value2")
        )
        val actual = TimeIndexedCSVBuilder(listOf()).findClosestEntry(instant.plusSeconds(5), list)
        val expected = list[0]
        Assertions.assertEquals(expected, actual)
    }

/*
    @Test
    fun addColumnTest() = runBlocking {
        val now = Instant.now()
        val tesla = "TSLA"
        val sap = "SAP"

        val data = listOf(
            AggregatedPriceInformation(now, tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(567), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(1), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), null, BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(2), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(839.17), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(3), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(824.46), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(4), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(776.58), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(5), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(805.29), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(6), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), null, BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(7), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), null, BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(8), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(816.72), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(9), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(815.11), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(10), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(742.5), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(11), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(345), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(12), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(987), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(13), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(14), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(876), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(15), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(435), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(16), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(98), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(17), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(456), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(18), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(324), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(19), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(987), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(20), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(21), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(867), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(22), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(23), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(24), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(98), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(25), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(26), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(27), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(324.9), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(28), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(29), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(30), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(31), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformation(now, sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(567), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(1), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(805.123), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(2), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(839.17), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(3), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(824.46), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(4), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(8976.58), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(5), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(805.29), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(6), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(805.29), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(7), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(805.29), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(8), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(816.72), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(9), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(815.11), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(10), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(742.5), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(11), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(6456), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(12), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(987), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(13), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(14), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(876), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(15), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(435), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(16), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(98), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(17), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(456), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(18), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(19), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(987), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(20), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(21), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(867), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(22), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(23465), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(23), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(24), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(98), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(25), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(26), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(27), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(324.9), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(28), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(29), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(30), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformation(now.minusSeconds(31), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
        )

        val symbols = listOf(sap, tesla)
        val end = now.minusSeconds(13)
        val indicatorNames = listOf(TechnicalIndicatorName.SMA.indicatorName)

        // Expected
        val ohlcv = aggregatedPriceInformationResponseToCSVColumns(data)
        val sma = smaForMultipleSymbols(data).flatten().map { CsvEntry(it.time, "${it.type.indicatorName}_${it.symbol}", it.value.toString()) }
        val (csvHeader, csvBody) = toCSVBody(ohlcv + sma)

        val expected = toCSVFile(csvHeader, csvBody)

        // Actual
        val actual = subject.getAggregatedPriceInformationCSV(symbols, now, end, indicatorNames)

        val expectedContent = expected.inputStream.readAllBytes()
        val actualContent = actual.inputStream.readAllBytes()
        for((index, byte) in expectedContent.withIndex()){
            Assertions.assertEquals(expectedContent[index], actualContent[index])
        }
    }
 */
}