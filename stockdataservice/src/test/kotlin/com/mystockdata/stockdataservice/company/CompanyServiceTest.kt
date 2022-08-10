package com.mystockdata.stockdataservice.company

import com.mystockdata.stockdataservice.watchlist.WatchlistService
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
    private val watchlistServiceMock = Mockito.mock(WatchlistService::class.java)

    private val subject =
        CompanyService(companyRepository, watchlistServiceMock)

    private val companies = listOf(
        Company("lei", setOf(Security("isin", setOf(Symbol("symbol", "system", "exchange"))))),
        Company("lei1", setOf(Security("isin1", setOf(Symbol("symbol1", "system", "exchange")))))
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
        val leis = companies.map { it.lei }.toSet()
        Mockito.`when`(watchlistServiceMock.addToWatchList(leis)).thenReturn(leis.toList())

        val response = subject.addCompaniesAndSaveToWatchList(companies.toSet())
        val savedData = companyRepository.findAll().toList()

        companies.forEach {
            Assertions.assertNotNull(response)
            Assertions.assertTrue(response.companies.contains(it))
            Assertions.assertTrue(savedData.contains(it))
        }

        Mockito.verify(watchlistServiceMock, times(1)).addToWatchList(leis)
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