package com.mystockdata.composerservice.company

import com.mystockdata.composerservice.financialreport.FinancialReportServiceAdapter
import com.mystockdata.composerservice.stockdata.StockDataServiceAdapter
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner::class)
@DataMongoTest
class CompanyServiceTest(
    @Autowired val companyRepository: CompanyRepository
) {

    private val mockStockDataServiceAdapter: StockDataServiceAdapter = Mockito.mock(StockDataServiceAdapter::class.java)
    private val mockFinancialReportServiceAdapter: FinancialReportServiceAdapter =
        Mockito.mock(FinancialReportServiceAdapter::class.java)

    private val subject =
        CompanyService(companyRepository, mockStockDataServiceAdapter, mockFinancialReportServiceAdapter)
    private val companies = listOf(
        Company("lei", setOf(Security("isin", setOf(Symbol("symbol", "exchange"))))),
        Company("lei1", setOf(Security("isin1", setOf(Symbol("symbol1", "exchange")))))
    )

    @BeforeEach
    fun clearDB() = runBlocking {
        companyRepository.deleteAll()
    }

    @Test
    fun addCompaniesTest() = runBlocking {
        val response = subject.addCompanies(companies.toSet())
        val savedData = companyRepository.findAll().toList()
        companies.forEach {
            Assertions.assertTrue(response.contains(it))
            Assertions.assertTrue(savedData.contains(it))
        }
    }

    @Test
    fun addCompaniesAlsoToWatchlistTest(): Unit = runBlocking {
        val symbols = companies.map { it.getSymbolNames() }.flatten().toSet()
        Mockito.`when`(mockStockDataServiceAdapter.addToWatchlist(symbols)).thenReturn(symbols.toList().asFlow())

        val response = subject.addCompaniesAndSaveToWatchList(companies.toSet())
        val savedData = companyRepository.findAll().toList()

        companies.forEach {
            Assertions.assertNotNull(response)
            Assertions.assertTrue(response.companies.contains(it))
            Assertions.assertTrue(savedData.contains(it))
        }

        Mockito.verify(mockStockDataServiceAdapter, times(1)).addToWatchlist(symbols)
    }

    @Test
    fun addExistingCompanyTest() = runBlocking {
        subject.addCompanies(companies.toSet())
        val response = subject.addCompanies(companies.toSet())
        val savedData = companyRepository.findAll().toList()
        companies.forEach {
            Assertions.assertTrue(savedData.contains(it))
        }
        Assertions.assertEquals(companies.size, savedData.size)
        Assertions.assertEquals(0, response.size)
    }

    @Test
    fun getCompaniesTest() = runBlocking {
        subject.addCompanies(companies.toSet())
        val actual = subject.getCompany(companies[0].lei)
        Assertions.assertEquals(1, actual.size)
        Assertions.assertEquals(companies[0].lei, actual.first().lei)
    }

}