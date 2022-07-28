package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationResponse
import com.mystockdata.stockdataservice.indicators.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.MathContext
import java.time.Instant

class TechnicalIndicatorTest {

    @Test
    fun smaForMultipleSymbolsTest(){
        val now = Instant.now()
        val symbol = "SAP.DE"
        val symbol2 = "VW.DE"
        val data = listOf(
            AggregatedPriceInformationResponse(now, symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(10), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(105), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(20), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(30), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(115), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(40), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(50), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(101), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(60), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(102), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(70), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(103), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(80), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(104), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(90), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(105), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(100), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(106), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(110), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(107), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(120), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(108), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(130), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(109), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(140), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(150), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(160), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(170), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now, symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(200), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(10), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(205), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(20), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(220), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(30), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(215), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(40), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(200), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(50), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(201), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(60), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(222), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(70), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(203), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(80), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(204), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(90), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(205), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(100), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(226), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(110), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(227), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(120), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(238), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(130), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(239), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(140), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(120), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(150), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(210), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(160), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(170), symbol2, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(210), BigDecimal(100) ,100)
        )

        val expectedList = listOf(
            listOf(
                TechnicalIndicator(data[0].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(105.36, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[1].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(106.07, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[2].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(106.43, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[3].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(106.43, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[4].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(106.07, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[5].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[6].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[7].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[8].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[9].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[10].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[11].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[12].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[13].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[14].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[15].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[16].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[17].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros())
            ),
            listOf(
                TechnicalIndicator(data[18].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(214.64, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[19].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(208.93, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[20].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(209.29, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[21].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(201.43, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[22].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(201.07, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[23].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[24].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[25].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[26].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[27].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[28].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[29].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[30].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[31].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[32].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[33].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[34].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                TechnicalIndicator(data[35].time, symbol2, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros())
            )
        )
        val actualList = smaForMultipleSymbols(data.shuffled().sortedBy { it.symbol })

        for ((subListNum, sublist) in expectedList.withIndex()){
            println(actualList[subListNum].toString())
            for((index, expected) in sublist.withIndex()){
                Assertions.assertEquals(expectedList[subListNum][index].time, actualList[subListNum][index].time)
                Assertions.assertEquals(expectedList[subListNum][index].symbol, actualList[subListNum][index].symbol)
                Assertions.assertEquals(expectedList[subListNum][index].type, actualList[subListNum][index].type)
                Assertions.assertEquals(expectedList[subListNum][index].value, actualList[subListNum][index].value)
            }
            Assertions.assertEquals(expectedList[subListNum].size, actualList[subListNum].size)
        }
        Assertions.assertEquals(expectedList.size, actualList.size)

    }

    @Test
    fun smaForAllOfASymbolTest(){
        val now = Instant.now()
        val symbol = "SAP.DE"
        val data = listOf(
            AggregatedPriceInformationResponse(now, symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(10), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(105), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(20), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(30), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(115), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(40), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(50), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(101), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(60), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(102), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(70), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(103), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(80), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(104), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(90), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(105), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(100), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(106), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(110), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(107), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(120), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(108), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(130), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(109), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(140), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(150), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(160), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(170), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100)
        )

        val expectedList = listOf(
            TechnicalIndicator(data[0].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(105.36, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[1].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(106.07, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[2].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(106.43, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[3].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(106.43, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[4].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(106.07, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[5].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[6].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[7].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[8].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[9].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[10].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[11].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[12].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[13].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[14].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[15].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[16].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
            TechnicalIndicator(data[17].time, symbol, TechnicalIndicatorName.SMA, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros())
        )

        val actualList = smaForAllOfASymbol(data.shuffled())
        for ((index, expected) in expectedList.withIndex()){
            val actual = actualList[index]
            // println("$index\t $expected\t$actual")
            Assertions.assertEquals(expected.time, actual.time)
            Assertions.assertEquals(expected.symbol, actual.symbol)
            Assertions.assertEquals(expected.type, actual.type)
            Assertions.assertEquals(expected.value, actual.value)
        }
        Assertions.assertEquals(expectedList.size, actualList.size)

    }


    @Test
    fun buildSMATest(){
        val now = Instant.now()
        val symbol = "SAP.DE"
        val data = listOf(
            AggregatedPriceInformationResponse(now, symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(10), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(105), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(20), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(30), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(115), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(40), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(50), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(101), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(60), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(102), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(70), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(103), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(80), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(104), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(90), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(105), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(100), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(106), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(110), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(107), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(120), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(108), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(130), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(109), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(140), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(150), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(160), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(170), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100)
        )

        val actual = calculateSMA(data.shuffled(), data[1].time, 14)
        Assertions.assertEquals(data[1].time, actual.time)
        Assertions.assertEquals(symbol, actual.symbol)
        Assertions.assertEquals(TechnicalIndicatorName.SMA, actual.type)
        Assertions.assertEquals(BigDecimal(106.07, MathContext.DECIMAL32).stripTrailingZeros(), actual.value)
    }


    @Test
    fun reduceToRelevantDataTest(){
        val now = Instant.now()
        val symbol = "SAP.DE"
        val data = listOf(
            AggregatedPriceInformationResponse(now, symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(10), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(105), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(20), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(30), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(115), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(40), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(50), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(101), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(60), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(102), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(70), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(103), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(80), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(104), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(90), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(105), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(100), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(106), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(110), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(107), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(120), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(108), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(130), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(109), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(140), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(150), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(160), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(170), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100)
        )
        val start = data[1]
        val expected = data.subList(1, 15)
        val actual = reduceToRelevantData(data.shuffled(), start.time, windowSize = 14)
        if(actual == null){
            Assertions.fail("Should not be null")
        }else{
            for((index, element) in expected.withIndex()){
                Assertions.assertEquals(element, actual[index])
            }
        }

    }

    @Test
    fun reduceToRelevantDataTestOutOfBounds(){
        val now = Instant.now()
        val symbol = "SAP.DE"
        val data = listOf(
            AggregatedPriceInformationResponse(now, symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(10), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(105), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(20), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(30), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(115), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(40), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(50), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(101), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(60), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(102), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(70), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(103), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(80), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(104), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(90), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(105), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(100), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(106), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(110), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(107), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(120), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(108), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(130), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(109), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(140), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(150), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(160), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100),
            AggregatedPriceInformationResponse(now.minusSeconds(170), symbol, BigDecimal(100), BigDecimal(100), BigDecimal(100), BigDecimal(110), BigDecimal(100) ,100)
        )
        val start = data[10]
        val actual = reduceToRelevantData(data.shuffled(), start.time, windowSize = 14)
        Assertions.assertTrue(actual == null)

    }

}