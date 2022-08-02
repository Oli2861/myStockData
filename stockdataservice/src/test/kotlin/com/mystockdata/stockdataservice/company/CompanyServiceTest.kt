package com.mystockdata.stockdataservice.company

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

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner::class)
@DataMongoTest
class CompanyServiceTest(
    @Autowired val companyRepository: CompanyRepository
) {
    private val subject = CompanyService(companyRepository)
    val companies = listOf(
        Company("lei", listOf(Security("isin", "companyId", listOf(Symbol("symbol", "exchange", "isin"))))),
        Company("lei1", listOf(Security("isin1", "companyId1", listOf(Symbol("symbol1", "exchange", "isin1")))))
    )

    @BeforeEach
    fun clearDB() = runBlocking {
        companyRepository.deleteAll()
    }

    @Test
    fun addCompaniesTest() = runBlocking {
        val response = subject.addCompanies(companies)
        val savedData = companyRepository.findAll().toList()
        companies.forEach {
            Assertions.assertTrue(response.contains(it))
            Assertions.assertTrue(savedData.contains(it))
        }
    }

    @Test
    fun addExistingCompanyTest() = runBlocking {
        subject.addCompanies(companies)
        val response = subject.addCompanies(companies)
        val savedData = companyRepository.findAll().toList()
        companies.forEach {
            Assertions.assertTrue(savedData.contains(it))
        }
        Assertions.assertEquals(companies.size, savedData.size)
        Assertions.assertEquals(0, response.size)
    }

    @Test
    fun getCompaniesTest() = runBlocking {
        subject.addCompanies(companies)
        val actual = subject.getCompany(companies[0].lei)
        Assertions.assertEquals(companies[0], actual)
    }



}