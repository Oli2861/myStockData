package com.mystockdata.composerservice.company

import kotlinx.coroutines.flow.Flow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompanyService(
    @Autowired val companyRepository: CompanyRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CompanyService::class.java)
    }

    suspend fun addCompanies(companies: List<Company>): MutableSet<Company> {
        val savedCompanies = mutableSetOf<Company>()
        companies.forEach { company ->
            if (getCompany(company.lei) == null) {
                val saved = companyRepository.save(company)
                savedCompanies.add(saved)
            }
        }
        return savedCompanies
    }

    suspend fun getCompany(lei: String): Company? {
        return companyRepository.findById(lei)
    }

    suspend fun getCompanies(leis: List<String>): Flow<Company>{
        return companyRepository.findAllById(leis)
    }

}