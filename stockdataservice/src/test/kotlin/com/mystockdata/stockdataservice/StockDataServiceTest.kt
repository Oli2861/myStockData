package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedInformationProvider
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformation
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationResponse
import com.mystockdata.stockdataservice.indicators.TechnicalIndicatorName
import com.mystockdata.stockdataservice.indicators.smaForMultipleSymbols
import com.mystockdata.stockdataservice.persistence.AggregatedPriceInformationRepository
import com.mystockdata.stockdataservice.persistence.PrecisePriceInformationRepository
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationProvider
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import org.mockito.Mockito
import org.mockito.Mockito.times

class StockDataServiceTest {

    private val aggregatedPriceInformationMock = Mockito.mock(AggregatedInformationProvider::class.java)
    private val precisePriceInformationProviderMock = Mockito.mock(PrecisePriceInformationProvider::class.java)
    private val precisePriceInformationRepositoryMock = Mockito.mock(PrecisePriceInformationRepository::class.java)
    private val aggregatedPriceInformationRepositoryMock = Mockito.mock(AggregatedPriceInformationRepository::class.java)

    private val subject = StockDataService(
        aggregatedPriceInformationMock,
        precisePriceInformationProviderMock,
        precisePriceInformationRepositoryMock,
        aggregatedPriceInformationRepositoryMock
    )

    @Test
    fun getAggregatedPriceInformationCSVTest() = runBlocking {
        val now = Instant.now()
        val tesla = "TSLA"
        val sap = "SAP"

        val data = listOf(
            AggregatedPriceInformationResponse(now, tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(567), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(1), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), null, BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(2), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(839.17), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(3), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(824.46), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(4), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(776.58), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(5), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(805.29), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(6), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), null, BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(7), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), null, BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(8), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(816.72), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(9), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(815.11), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(10), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(742.5), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(11), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(345), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(12), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(987), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(13), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(14), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(876), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(15), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(435), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(16), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(98), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(17), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(456), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(18), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(324), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(19), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(987), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(20), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(21), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(867), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(22), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(23), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(24), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(98), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(25), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(26), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(27), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(324.9), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(28), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(29), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(30), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(31), tesla, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now, sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(567), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(1), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(805.123), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(2), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(839.17), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(3), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(824.46), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(4), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(8976.58), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(5), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(805.29), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(6), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(805.29), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(7), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(805.29), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(8), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(816.72), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(9), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(815.11), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(10), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(742.5), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(11), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(6456), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(12), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(987), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(13), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(14), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(876), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(15), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(435), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(16), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(98), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(17), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(456), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(18), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(19), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(987), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(20), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(21), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(867), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(22), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(23465), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(23), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(24), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(98), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(25), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(26), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(27), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(324.9), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(28), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(29), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(30), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(654), BigDecimal(5), 0),
            AggregatedPriceInformationResponse(now.minusSeconds(31), sap, BigDecimal(1), BigDecimal(2), BigDecimal(3), BigDecimal(234), BigDecimal(5), 0),
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
        Mockito.`when`(aggregatedPriceInformationRepositoryMock.readAggregatedPriceInformation(symbols, now, end)).thenReturn(data)
        val actual = subject.getAggregatedPriceInformationCSV(symbols, now, end, indicatorNames)
        Mockito.verify(aggregatedPriceInformationRepositoryMock, times(1)).readAggregatedPriceInformation(symbols, now, end)
        val expectedContent = expected.inputStream.readAllBytes()
        val actualContent = actual.inputStream.readAllBytes()
        for((index, byte) in expectedContent.withIndex()){
            Assertions.assertEquals(expectedContent[index], actualContent[index])
        }
    }

}