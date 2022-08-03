package com.mystockdata.composerservice.indicator

import com.mystockdata.composerservice.csv.CsvEntry
import com.mystockdata.composerservice.printMultiDimensionalList
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class TimeSeriesOperatorTest {

    @Test
    fun splitBySymbolTest() {
        val now = Instant.now()
        val symbol = "SAP.DE"
        val symbol1 = "VW.DE"

        val data = listOf(
            CsvEntry(now, symbol, BigDecimal(100)),
            CsvEntry(now.minusSeconds(10), symbol, BigDecimal(105)),
            CsvEntry(now.minusSeconds(20), symbol, BigDecimal(110)),
            CsvEntry(now.minusSeconds(30), symbol, BigDecimal(115)),
            CsvEntry(now.minusSeconds(40), symbol1, BigDecimal(100)),
            CsvEntry(now.minusSeconds(50), symbol1, BigDecimal(101)),
            CsvEntry(now.minusSeconds(60), symbol1, BigDecimal(102)),
            CsvEntry(now.minusSeconds(70), symbol1, BigDecimal(103))
        )
        val expectedList = listOf(
            data.filter { it.columnName == symbol }.sortedByDescending { it.time },
            data.filter { it.columnName == symbol1 }.sortedByDescending { it.time }
        )

        val actualList = TimeSeriesOperator.splitBySymbol(data.shuffled())

        if(false){
            println("expectedList:")
            printMultiDimensionalList(expectedList)
            println("actualList:")
            printMultiDimensionalList(actualList)
        }

        for((listIndex, sublist) in expectedList.withIndex()){
            for ((index, expected) in sublist.withIndex()){
                val actual = actualList[listIndex][index]
                //println("$index\t $expected\t$actual")
                Assertions.assertEquals(expected.time, actual.time)
                Assertions.assertEquals(expected.columnName, actual.columnName)
                Assertions.assertEquals(expected.value, actual.value)
            }
        }

        Assertions.assertEquals(expectedList.size, actualList.size)
    }

    @Test
    fun splitBySymbolAndFillMissingValuesTest(){
        val now = Instant.now()
        val symbol = "SAP.DE"
        val symbol1 = "VW.DE"

        val data = listOf(
            CsvEntry(now, symbol, BigDecimal(100)),
            CsvEntry(now.minusSeconds(10), symbol, BigDecimal(105)),
            CsvEntry(now.minusSeconds(20), symbol, BigDecimal(110)),
            CsvEntry(now.minusSeconds(30), symbol, BigDecimal(115)),
            CsvEntry(now.minusSeconds(40), symbol1, BigDecimal(100)),
            CsvEntry(now.minusSeconds(50), symbol1, null),
            CsvEntry(now.minusSeconds(60), symbol1, BigDecimal(102)),
            CsvEntry(now.minusSeconds(70), symbol1, BigDecimal(103))
        )
        val expectedList = listOf(
            data.filter { it.columnName == symbol },
            listOf(data[4], CsvEntry(data[5].time, data[5].columnName, data[4].value), data[6], data[7])
        )
        val actualList = TimeSeriesOperator.splitBySymbolAndFillMissingValues(data)

        for((listIndex, sublist) in expectedList.withIndex()){
            for ((index, expected) in sublist.withIndex()){
                val actual = actualList[listIndex][index]
                // println("$index\t $expected\t$actual")
                Assertions.assertEquals(expected.time, actual.time)
                Assertions.assertEquals(expected.columnName, actual.columnName)
                Assertions.assertEquals(expected.value, actual.value)
            }
        }

        Assertions.assertEquals(expectedList.size, actualList.size)
    }
}