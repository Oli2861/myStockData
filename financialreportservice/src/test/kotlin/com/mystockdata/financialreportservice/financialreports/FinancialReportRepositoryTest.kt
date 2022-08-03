package com.mystockdata.financialreportservice.financialreports

import com.mystockdata.financialreportservice.financialreportdatasource.BalanceType
import com.mystockdata.financialreportservice.financialreportdatasource.Item
import com.mystockdata.financialreportservice.financialreportdatasource.ItemType
import com.mystockdata.financialreportservice.utility.addDays
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.math.BigDecimal
import java.util.*

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner::class)
@DataMongoTest
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [FinancialreportserviceApplication::class], properties = ["classpath:application-test.yml"], properties = ["spring.main.web-application-type=reactive"])
class FinancialReportRepositoryTest(
    @Autowired val financialReportRepository: FinancialReportRepository
) {
    // Test data
    private val endDate = Date()
    private val msPerDay = 86_400_000
    private val startDate = Date(endDate.time - msPerDay)
    private val previousPeriodStartDate = Date(startDate.time - msPerDay)
    private val entityIdentifier = "sap"
    private val unknownEntityIdentifier = "unknown"

    private val data = listOf(
        Item(
            name = "ifrs1",
            unitRef = "EUR",
            start = startDate,
            endInstant = endDate,
            value = "100000",
            balance = BalanceType.CREDIT.typeName,
            type = ItemType.MONETARY_ITEM.typeName,
            entityIdentifier = entityIdentifier
        ),
        Item(
            name = "ifrs2",
            unitRef = "EUR",
            start = startDate,
            endInstant = endDate,
            value = "500000",
            balance = BalanceType.CREDIT.typeName,
            type = ItemType.MONETARY_ITEM.typeName,
            entityIdentifier = entityIdentifier
        ),
        Item(
            name = "ifrsText",
            start = startDate,
            endInstant = endDate,
            value = "text",
            entityIdentifier = entityIdentifier
        ),
        Item(
            name = "ifrs2",
            unitRef = "EUR",
            start = previousPeriodStartDate,
            endInstant = startDate,
            value = "300000",
            balance = BalanceType.CREDIT.typeName,
            entityIdentifier = entityIdentifier
        ),
        Item(
            name = "ifrs1",
            unitRef = "EUR",
            start = previousPeriodStartDate,
            endInstant = startDate,
            value = "200000",
            balance = BalanceType.CREDIT.typeName,
            type = ItemType.MONETARY_ITEM.typeName,
            entityIdentifier = entityIdentifier
        )
    )

    private val financialReports = listOf(
        FinancialReport(
            null,
            endDate,
            entityIdentifier,
            unknownEntityIdentifier,
            listOf(
                TextualFact(data[2].name!!, startDate, endDate, data[2].value!!),
                MonetaryFact(
                    data[0].name!!,
                    startDate,
                    endDate,
                    BigDecimal(100000),
                    data[0].unitRef!!,
                    data[0].balance!!
                ),
                MonetaryFact(
                    data[1].name!!,
                    startDate,
                    endDate,
                    BigDecimal(500000),
                    data[1].unitRef!!,
                    data[1].balance!!
                )
            )
        ),
        FinancialReport(
            null,
            startDate,
            entityIdentifier,
            unknownEntityIdentifier,
            listOf(
                MonetaryFact(
                    data[4].name!!,
                    previousPeriodStartDate,
                    startDate,
                    BigDecimal(200000),
                    data[4].unitRef!!,
                    data[4].balance!!
                ),
                TextualFact(data[3].name!!, previousPeriodStartDate, startDate, "300000"),
            )
        )
    ).sortedBy { it.endOfReportingPeriod }

    @BeforeEach
    fun clearDB() = runBlocking {
        financialReportRepository.deleteAll()
    }

    @Test
    fun testFindAllAndWrite() = runBlocking {
        val response = financialReportRepository.saveAll(financialReports).toList()
        Assertions.assertTrue(testReportListsEquals(financialReports, response))

        val retrieved = financialReportRepository.findAll().toList()
        Assertions.assertTrue(testReportListsEquals(financialReports, retrieved))
    }

    @Test
    fun getFinancialReportByEntityIdentifierInAndEndOfReportingPeriodBetweenTest() = runBlocking {
        val response = financialReportRepository.saveAll(financialReports).toList()
        Assertions.assertTrue(testReportListsEquals(financialReports, response))

        val retrieveByLeiAndDates =
            financialReportRepository.getFinancialReportByEntityIdentifierInAndEndOfReportingPeriodBetween(
                listOf(entityIdentifier),
                Date(previousPeriodStartDate.time - 1),
                Date(endDate.time + 1)
            ).toList()

        Assertions.assertTrue(testReportListsEquals(financialReports, retrieveByLeiAndDates))
    }

    @Test
    fun findFinancialReportByEntityIdentifierInTest() = runBlocking {
        val response = financialReportRepository.saveAll(financialReports).toList()
        Assertions.assertTrue(testReportListsEquals(financialReports, response))

        val retrieveByLeiAndDates =
            financialReportRepository.findFinancialReportByEntityIdentifierIn(listOf("sap")).toList()
        Assertions.assertTrue(testReportListsEquals(financialReports, retrieveByLeiAndDates))
    }

    @Test
    fun findFinancialReportByEndOfReportingPeriodBetween() = runBlocking {
        val response = financialReportRepository.saveAll(financialReports).toList()
        Assertions.assertTrue(testReportListsEquals(financialReports, response))

        val retrieveByLeiAndDates = financialReportRepository.findFinancialReportByEndOfReportingPeriodBetween(
            Date(previousPeriodStartDate.time - 1),
            Date(endDate.time + 1)
        ).toList()

        Assertions.assertTrue(testReportListsEquals(financialReports, retrieveByLeiAndDates))
    }

    @Test
    fun findFinancialReportByEntityIdentifierIsAndEndOfReportingPeriodIsTest() = runBlocking {
        val saved = financialReportRepository.saveAll(financialReports).toList()
        val response: FinancialReport? = financialReportRepository.getFinancialReportByEntityIdentifierIsAndEndOfReportingPeriodBetween(
            financialReports[1].entityIdentifier,
            financialReports[1].endOfReportingPeriod.addDays(-1)!!,
            financialReports[1].endOfReportingPeriod.addDays(1)!!
        ).toList().firstOrNull()
        Assertions.assertEquals(financialReports[1].entityIdentifier, response!!.entityIdentifier)
        Assertions.assertEquals(financialReports[1].entityIdentifierScheme, response!!.entityIdentifierScheme)
        Assertions.assertEquals(financialReports[1].factList, response!!.factList)
        Assertions.assertEquals(financialReports[1].endOfReportingPeriod, response!!.endOfReportingPeriod)

    }

    private fun testReportListsEquals(a: List<FinancialReport>, b: List<FinancialReport>): Boolean {
        if (a.size != b.size) return false
        for ((reportIndex, _) in a.withIndex()) {
            if (a[reportIndex].endOfReportingPeriod != b[reportIndex].endOfReportingPeriod) return false
            if (a[reportIndex].entityIdentifier != b[reportIndex].entityIdentifier) return false
            if (a[reportIndex].entityIdentifierScheme != b[reportIndex].entityIdentifierScheme) return false

            for (fact in a[reportIndex].factList) {
                if (!b[reportIndex].factList.contains(fact)) return false
            }

        }
        return true
    }


}