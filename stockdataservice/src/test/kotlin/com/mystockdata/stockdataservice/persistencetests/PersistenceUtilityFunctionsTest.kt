package com.mystockdata.stockdataservice.persistencetests

import com.mystockdata.stockdataservice.utility.createFilter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PersistenceUtilityFunctionsTest {

    @Test
    fun createFilterTest(){
        val expected = "|> filter(fn: (r) => r[\"_field\"] == \"open\" or r[\"_field\"] == \"high\" or r[\"_field\"] == \"low\" or r[\"_field\"] == \"close\" or r[\"_field\"] == \"adjClose\" or r[\"_field\"] == \"volume\")"
        val actual = createFilter("_field", listOf("open", "high", "low", "close", "adjClose", "volume"))
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun createFilterTest1(){
        val expected = "|> filter(fn: (r) => r[\"symbol\"] == \"SAP.DE\" or r[\"symbol\"] == \"SOW.DE\")"
        val actual = createFilter("symbol", listOf("SAP.DE", "SOW.DE"))
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun createFilterTest2(){
        val expected = "|> filter(fn: (r) => r[\"_measurement\"] == \"AggregatedPriceInformation\")"
        val actual = createFilter("_measurement", listOf("AggregatedPriceInformation"))
        Assertions.assertEquals(expected, actual)
    }

}