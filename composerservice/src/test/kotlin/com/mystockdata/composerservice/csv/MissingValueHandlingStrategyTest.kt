package com.mystockdata.composerservice.csv

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class MissingValueHandlingStrategyTest {

    @Test
    fun findClosestEntryTest(){
        val instant = Instant.now().minusSeconds(5000)
        val list = listOf(
            PriceEntry(instant, "a", TimeIndexedCSVBuilderConstants.PLACEHOLDER_VALUE, "a"),
            PriceEntry(instant.minusSeconds(1000), "a", BigDecimal(10), "a"),
            PriceEntry(instant.plusSeconds(10), "a", BigDecimal(20), "a")
        )
        val actual = ClosestEntryStrategy.findValueToBePlaced(3,
            instant.plusSeconds(5), 1, "a", "a", listOf(), list, false
        )
        val expected = PriceEntry(instant.plusSeconds(5), list[2].columnName, list[2].price, "a")

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun findLastEntryTest(){
        val instant = Instant.now().minusSeconds(5000)
        val list = listOf(
            listOf(PriceEntry(instant, "a", BigDecimal(1), "a"), PriceEntry(instant, "b", BigDecimal(2), "b"), PriceEntry(instant, "c", BigDecimal(3), "c")),
            listOf(PriceEntry(instant, "a", BigDecimal(4), "a"), PriceEntry(instant, "b",
                TimeIndexedCSVBuilderConstants.PLACEHOLDER_VALUE, "b"), PriceEntry(instant, "c", BigDecimal(6), "c")),
            listOf(PriceEntry(instant, "a", BigDecimal(7), "a"), PriceEntry(instant, "a", BigDecimal(8), "a"), PriceEntry(instant, "a", BigDecimal(9), "a"))
        )
        val actual = LastValueStrategy.findValueToBePlaced(1, instant, 1, "b", "b", list, listOf(), false)
        val expected = list[0][1]
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun findLastEntryNoneExistingTest(){
        val instant = Instant.now().minusSeconds(5000)
        val list = listOf(
            listOf(PriceEntry(instant, "a", BigDecimal(1), "a"), PriceEntry(instant, "b", BigDecimal(2), "b"), PriceEntry(instant, "c", BigDecimal(3), "c")),
            listOf(PriceEntry(instant, "a", BigDecimal(4), "a"), PriceEntry(instant, "b",
                TimeIndexedCSVBuilderConstants.PLACEHOLDER_VALUE, "b"), PriceEntry(instant, "c", BigDecimal(6), "c")),
            listOf(PriceEntry(instant, "a", BigDecimal(7), "a"), PriceEntry(instant, "a", BigDecimal(8), "a"), PriceEntry(instant, "a", BigDecimal(9), "a"))
        )
        val actual = LastValueStrategy.findValueToBePlaced(1, instant, 1, "b", "b", list, listOf(), false)
        val expected = list[0][1]
        Assertions.assertEquals(expected, actual)
    }

}