package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationResponse
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationResponse
import com.mystockdata.stockdataservice.utility.printMultiDimensionalArray
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class CSVBuilderTest {
    private val placeholder = "null"

    @Test
    fun testAggregatedPriceInformationToCSV() {
        val now = Instant.now()

        val data = listOf(
            AggregatedPriceInformationResponse(now, "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformationResponse(now, "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005),
            AggregatedPriceInformationResponse(now.minusSeconds(10), "SAP.DE", BigDecimal(100), BigDecimal(101), BigDecimal(102), BigDecimal(103), BigDecimal(104), 105),
            AggregatedPriceInformationResponse(now.minusSeconds(22), "VW.DE", BigDecimal(1000), BigDecimal(1001), BigDecimal(1002), BigDecimal(1003), BigDecimal(1004), 1005)
        )

        val expectedHeader = arrayOf("timestamp") + arrayOf("open_SAP.DE", "high_SAP.DE","low_SAP.DE", "close_SAP.DE", "adjClose_SAP.DE", "volume_SAP.DE", "open_VW.DE", "high_VW.DE", "low_VW.DE", "close_VW.DE", "adjClose_VW.DE", "volume_VW.DE").sortedArray()



        val expectedBody: Array<Array<String>> = arrayOf(
            arrayOf(data[0].time.toString(), data[0].adjClose.toString(), data[1].adjClose.toString(), data[0].close.toString(), data[1].close.toString(), data[0].high.toString(), data[1].high.toString(), data[0].low.toString(), data[1].low.toString(), data[0].open.toString(), data[1].open.toString(), data[0].volume.toString(), data[1].volume.toString()),
            arrayOf(data[2].time.toString(), data[2].adjClose.toString(), placeholder, data[2].close.toString(), placeholder, data[2].high.toString(), placeholder, data[2].low.toString(), placeholder, data[2].open.toString(), placeholder, data[2].volume.toString(), placeholder),
            arrayOf(data[3].time.toString(), placeholder, data[3].adjClose.toString(), placeholder, data[3].close.toString(), placeholder, data[3].high.toString(), placeholder, data[3].low.toString(), placeholder, data[3].open.toString(), placeholder, data[3].volume.toString()),
        )

        val dataShuffled = data.shuffled()
        val (actualHeader, actualBody) = aggregatedPriceInformationResponseToCSV(dataShuffled)

        if(false){
            println("actualBody:")
            printMultiDimensionalArray(actualBody)
            println("expectedBody:")
            printMultiDimensionalArray(expectedBody)
        }

        Assertions.assertArrayEquals(expectedHeader, actualHeader)
        for ((rowIndex, row) in expectedBody.withIndex()) {
            for ((columnIndex, str) in row.withIndex()) {
                Assertions.assertEquals(expectedBody[rowIndex][columnIndex], actualBody[rowIndex][columnIndex])
            }
        }

    }

    @Test
    fun testPrecisePriceInformationResponseToCSV() {

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

        val dataShuffled = data.shuffled()
        val (actualHeader, actualBody) = precisePriceInformationResponseToCSV(dataShuffled)
        val expectedHeader = arrayOf("timestamp") + arrayOf("SAP.DE", "VW.DE", "TSLA").sortedArray()
        val expectedBody: Array<Array<String>> = arrayOf(
            arrayOf(data[0].time.toString(), data[0].price.toString(), data[1].price.toString(), placeholder),
            arrayOf(data[2].time.toString(), data[3].price.toString(), placeholder, data[2].price.toString()),
            arrayOf(data[4].time.toString(), data[5].price.toString(), placeholder, data[4].price.toString()),
            arrayOf(data[6].time.toString(), placeholder, placeholder, data[6].price.toString()),
            arrayOf(data[7].time.toString(), placeholder, placeholder, data[7].price.toString())
        )

        if(false){
            println("actualBody:")
            printMultiDimensionalArray(actualBody)
            println("expectedBody:")
            printMultiDimensionalArray(expectedBody)
        }

        Assertions.assertArrayEquals(expectedHeader, actualHeader)
        for ((rowIndex, row) in expectedBody.withIndex()) {
            for ((columnIndex, str) in row.withIndex()) {
                Assertions.assertEquals(expectedBody[rowIndex][columnIndex], actualBody[rowIndex][columnIndex])
            }
        }

    }

}