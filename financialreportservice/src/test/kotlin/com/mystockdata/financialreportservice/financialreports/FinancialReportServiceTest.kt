package com.mystockdata.financialreportservice.financialreports

import com.mystockdata.financialreportservice.financialreportdatasource.BalanceType
import com.mystockdata.financialreportservice.financialreportdatasource.FinancialReportDataSource
import com.mystockdata.financialreportservice.financialreportdatasource.Item
import com.mystockdata.financialreportservice.financialreportdatasource.ItemType
import com.mystockdata.financialreportservice.financialreportinformation.ReportInfoDataSource
import com.mystockdata.financialreportservice.financialreportinformation.RetrievedReportInfo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.fail
import org.mockito.Mockito.times
import java.math.BigDecimal
import java.time.Instant

class FinancialReportServiceTest {

    private val financialReportDataSource: FinancialReportDataSource = Mockito.mock(FinancialReportDataSource::class.java)
    private val reportInfoDataSource: ReportInfoDataSource = Mockito.mock(ReportInfoDataSource::class.java)
    private val financialReportRepository: FinancialReportRepository = Mockito.mock(FinancialReportRepository::class.java)

    private val subject: FinancialReportService =
        FinancialReportService(financialReportDataSource, reportInfoDataSource, financialReportRepository)

    @Test
    fun testRetrieveReportByReportInfo(): Unit = runBlocking {
        val endDate = Date()
        val msPerDay = 86_400_000
        val startDate = Date(endDate.time - msPerDay)
        val previousPeriodStartDate = Date(startDate.time - msPerDay)
        val entityIdentifier = "sap"

        val retrievedReportInfo = RetrievedReportInfo(url = "test")
        val dataSourceResponse = listOf(
            Item(name = "ifrs1", unitRef = "EUR", start = startDate, endInstant = endDate, value = "100000", balance = BalanceType.CREDIT.typeName, type = ItemType.MONETARY_ITEM.typeName, entityIdentifier = entityIdentifier),
            Item(name = "ifrs2", unitRef = "EUR", start = startDate, endInstant = endDate, value = "500000", balance = BalanceType.CREDIT.typeName, type = ItemType.MONETARY_ITEM.typeName, entityIdentifier = entityIdentifier),
            Item(name = "ifrsText", start = startDate, endInstant = endDate, value = "text", entityIdentifier = entityIdentifier),
            Item(name = "ifrs2", unitRef = "EUR", start = previousPeriodStartDate, endInstant = startDate, value = "300000", balance = BalanceType.CREDIT.typeName, entityIdentifier = entityIdentifier),
            Item(name = "ifrs1", unitRef = "EUR", start = previousPeriodStartDate, endInstant = startDate, value = "200000", balance = BalanceType.CREDIT.typeName, type = ItemType.MONETARY_ITEM.typeName, entityIdentifier = entityIdentifier)
        )
        Mockito.`when`(financialReportDataSource.retrieveFacts(retrievedReportInfo.url))
            .thenReturn(dataSourceResponse.shuffled())

        val expected = listOf(
            FinancialReport(
                null,
                endDate,
                entityIdentifier,
                "unknown",
                listOf(
                    TextualFact(dataSourceResponse[2].name!!, startDate, endDate, dataSourceResponse[2].value!!),
                    MonetaryFact(dataSourceResponse[0].name!!, startDate, endDate, BigDecimal(100000), dataSourceResponse[0].unitRef!!, dataSourceResponse[0].balance!!),
                    MonetaryFact(dataSourceResponse[1].name!!, startDate, endDate, BigDecimal(500000), dataSourceResponse[1].unitRef!!, dataSourceResponse[1].balance!!)
                )
            ),
            FinancialReport(
                null,
                startDate,
                entityIdentifier,
                "unknown",
                listOf(
                    MonetaryFact(dataSourceResponse[4].name!!, previousPeriodStartDate, startDate, BigDecimal(200000), dataSourceResponse[4].unitRef!!, dataSourceResponse[4].balance!!),
                    TextualFact(dataSourceResponse[3].name!!, previousPeriodStartDate, startDate, "300000"),
                )
            )
        ).sortedBy { it.endOfReportingPeriod }

        val actual = subject.retrieveReportByReportInfo(retrievedReportInfo)?.sortedBy { it.endOfReportingPeriod }
        Mockito.verify(financialReportDataSource, times(1)).retrieveFacts(retrievedReportInfo.url)

        if(actual == null) {
            fail("Should not be null")
        } else {
            for ((index, _) in expected.withIndex()){
                Assertions.assertEquals(expected[index].endOfReportingPeriod, actual[index].endOfReportingPeriod)
                Assertions.assertEquals(expected[index].entityIdentifier, actual[index].entityIdentifier)
                Assertions.assertEquals(expected[index].entityIdentifierScheme, actual[index].entityIdentifierScheme)

                for ((factListIndex, _) in expected[index].factList.withIndex()){
                    Assertions.assertTrue(actual[index].factList.contains(expected[index].factList[factListIndex]))
                }
            }
        }

    }

    @Test
    fun testSplitByPeriodEnd() {
        val today = Date()
        val msPerDay = 86_400_000
        val yesterday = Date(today.time - msPerDay)
        val tomorrow = Date(today.time + msPerDay)
        val list = listOf(
            Item(name = "1", endInstant = today),
            Item(name = "2", endInstant = today),
            Item(name = "3", endInstant = today),
            Item(name = "4", endInstant = yesterday),
            Item(name = "5", endInstant = tomorrow),
        )

        val expectedMap = mapOf(
            yesterday to listOf(list[3]),
            today to listOf(list[0], list[1], list[2]),
            tomorrow to listOf(list[4])
        )

        val actualMap = subject.splitByPeriodEnd(list.shuffled())

        expectedMap.keys.forEach { date ->
            Assertions.assertTrue(actualMap[date]!!.containsAll(expectedMap[date]!!))
        }
    }

    @Test
    fun testCheckMainReportExists(){
        val retrievedReportInfo = RetrievedReportInfo(date = Date(), lei = "lei")
        val reports = listOf(
            FinancialReport(null, Date.from(Instant.now().minusSeconds(500)), "lei", "scheme", listOf<Fact>()),
            FinancialReport(null, Date.from(Instant.now().minusSeconds(5_000_000_000)), "lei", "scheme", listOf<Fact>())
        )
        val actual = subject.checkMainReportExists(retrievedReportInfo, reports)
        Assertions.assertTrue(actual)
    }

}