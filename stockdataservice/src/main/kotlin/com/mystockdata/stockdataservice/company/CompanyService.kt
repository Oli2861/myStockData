package com.mystockdata.stockdataservice.company

import com.mystockdata.stockdataservice.watchlist.WatchlistService
import kotlinx.coroutines.flow.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompanyService(
    @Autowired val companyRepository: CompanyRepository,
    @Autowired val watchlistService: WatchlistService
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CompanyService::class.java)
    }

    suspend fun addCompanies(companies: Set<Company>): Set<Company> {
        val savedCompanies = mutableSetOf<Company>()
        companies.forEach { company ->
            if (getCompany(company.lei).isEmpty()) {
                val saved = companyRepository.save(company)
                savedCompanies.add(saved)
            }
        }
        return savedCompanies
    }

    suspend fun getCompany(lei: String): List<Company> {
        return companyRepository.findByLeiIs(lei).toList()
    }

    suspend fun getCompanies(leis: Set<String>): Flow<Company> {
        return companyRepository.findAllById(leis)
    }

    suspend fun addCompaniesToWatchlist(lei: Set<String>): List<String>? {
        val companies = getCompanies(lei).toList()
        return if (companies.isNotEmpty()) {
            watchlistService.addToWatchList(lei)
        } else {
            null
        }
    }

    data class SavedCompaniesWithWatchlistResponse(val companies: Set<Company>, val addedWatchListEntries: Set<String>?)

    suspend fun addCompaniesAndSaveToWatchList(companies: Set<Company>): SavedCompaniesWithWatchlistResponse {
        val savedCompanies = addCompanies(companies)
        val toWatchListAddedSymbols = addCompaniesToWatchlist(companies.map { it.lei }.toSet())?.toSet()
        return SavedCompaniesWithWatchlistResponse(savedCompanies, toWatchListAddedSymbols)
    }

}
