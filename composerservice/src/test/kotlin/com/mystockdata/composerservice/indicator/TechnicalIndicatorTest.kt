package com.mystockdata.composerservice.indicator

import com.mystockdata.composerservice.csv.CsvEntry
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.MathContext
import java.time.Instant

class IndicatorTest {

    @Test
    fun smaForMultipleSymbolsTest(){
        val now = Instant.now()
        val symbol = "SAP.DE"
        val symbol2 = "VW.DE"
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
            CsvEntry(now, symbol2, BigDecimal(200)),
            CsvEntry(now.minusSeconds(10), symbol2, BigDecimal(205)),
            CsvEntry(now.minusSeconds(20), symbol2, BigDecimal(220)),
            CsvEntry(now.minusSeconds(30), symbol2, BigDecimal(215)),
            CsvEntry(now.minusSeconds(40), symbol2, BigDecimal(200)),
            CsvEntry(now.minusSeconds(50), symbol2, BigDecimal(201)),
            CsvEntry(now.minusSeconds(60), symbol2, BigDecimal(222)),
            CsvEntry(now.minusSeconds(70), symbol2, BigDecimal(203)),
            CsvEntry(now.minusSeconds(80), symbol2, BigDecimal(204)),
            CsvEntry(now.minusSeconds(90), symbol2, BigDecimal(205)),
            CsvEntry(now.minusSeconds(100), symbol2, BigDecimal(226)),
            CsvEntry(now.minusSeconds(110), symbol2, BigDecimal(227)),
            CsvEntry(now.minusSeconds(120), symbol2, BigDecimal(238)),
            CsvEntry(now.minusSeconds(130), symbol2, BigDecimal(239)),
            CsvEntry(now.minusSeconds(140), symbol2, BigDecimal(120)),
            CsvEntry(now.minusSeconds(150), symbol2, BigDecimal(210)),
            CsvEntry(now.minusSeconds(160), symbol2, BigDecimal(110)),
            CsvEntry(now.minusSeconds(170), symbol2,BigDecimal(210))
        )

        val expectedList = listOf(
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
                Indicator(data[18].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(214.64, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[19].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(208.93, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[20].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(209.29, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[21].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(201.43, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[22].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(201.07, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[23].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[24].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[25].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[26].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[27].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[28].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[29].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[30].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[31].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[32].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[33].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[34].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros()),
                Indicator(data[35].time, symbol2, IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR, BigDecimal(0, MathContext.DECIMAL32).stripTrailingZeros())
            )
        )
        val actualList = smaForMultipleSymbols(data.shuffled().sortedBy { it.columnName })

        for ((subListNum, sublist) in expectedList.withIndex()){
            //println(actualList[subListNum].toString())
            for((index, _) in sublist.withIndex()){
                Assertions.assertEquals(expectedList[subListNum][index].time, actualList[subListNum][index].time)
                Assertions.assertEquals(expectedList[subListNum][index].symbol, actualList[subListNum][index].symbol)
                Assertions.assertEquals(expectedList[subListNum][index].indicatorName, actualList[subListNum][index].indicatorName)
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
            CsvEntry(now.minusSeconds(170), symbol, BigDecimal(110))
        )

        val expectedList = listOf(
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
        )

        val actualList = smaForAllOfASymbol(data.shuffled())
        for ((index, expected) in expectedList.withIndex()){
            val actual = actualList[index]
            // println("$index\t $expected\t$actual")
            Assertions.assertEquals(expected.time, actual.time)
            Assertions.assertEquals(expected.symbol, actual.symbol)
            Assertions.assertEquals(expected.indicatorName, actual.indicatorName)
            Assertions.assertEquals(expected.value, actual.value)
        }
        Assertions.assertEquals(expectedList.size, actualList.size)

    }


    @Test
    fun buildSMATest(){
        val now = Instant.now()
        val symbol = "SAP.DE"
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
            CsvEntry(now.minusSeconds(170), symbol, BigDecimal(110))
        )

        val actual = calculateSMA(data.shuffled(), data[1].time, 14)
        Assertions.assertEquals(data[1].time, actual.time)
        Assertions.assertEquals(symbol, actual.symbol)
        Assertions.assertEquals(IndicatorName.SMA, actual.indicatorName)
        Assertions.assertEquals(BigDecimal(106.07, MathContext.DECIMAL32).stripTrailingZeros(), actual.value)
    }


    @Test
    fun reduceToRelevantDataTest(){
        val now = Instant.now()
        val symbol = "SAP.DE"
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
            CsvEntry(now.minusSeconds(170), symbol, BigDecimal(110))
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
            CsvEntry(now.minusSeconds(170), symbol, BigDecimal(110))
        )
        val start = data[10]
        val actual = reduceToRelevantData(data.shuffled(), start.time, windowSize = 14)
        Assertions.assertTrue(actual == null)

    }

}