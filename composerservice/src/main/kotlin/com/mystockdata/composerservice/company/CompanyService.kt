package com.mystockdata.composerservice.company

import com.mystockdata.composerservice.financialreport.FinancialReport
import com.mystockdata.composerservice.financialreport.FinancialReportServiceAdapter
import com.mystockdata.composerservice.stockdata.AggregatedPriceInformationResponse
import com.mystockdata.composerservice.stockdata.StockDataServiceAdapter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class CompanyService(
    @Autowired val companyRepository: CompanyRepository,
    @Autowired val stockDataServiceAdapter: StockDataServiceAdapter,
    @Autowired val financialReportServiceAdapter: FinancialReportServiceAdapter
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

    suspend fun getCompanies(leis: List<String>): Flow<Company> {
        return companyRepository.findAllById(leis)
    }

    suspend fun addCompaniesToWatchlist(lei: List<String>): Flow<String> {
        val companies = getCompanies(lei).toList()
        return if (companies.isNotEmpty()) {
            stockDataServiceAdapter.addToWatchlist(lei.toSet())
        } else {
            flowOf()
        }
    }

    data class SavedCompaniesWithWatchlistResponse(val companies: Set<Company>, val addedWatchListEntries: Set<String>?)

    suspend fun addCompaniesAndSaveToWatchList(companies: Set<Company>): SavedCompaniesWithWatchlistResponse =
        coroutineScope {
            val savedCompanies = async { addCompanies(companies) }
            val symbols: Set<String> = companies.map { it.getSymbolNames() }.flatten().toSet()
            val toWatchListAddedSymbols = async { stockDataServiceAdapter.addToWatchlist(symbols).toSet() }
            return@coroutineScope SavedCompaniesWithWatchlistResponse(
                savedCompanies.await(),
                toWatchListAddedSymbols.await()
            )
        }

    suspend fun retrieveDataForCompanies(
        leis: List<String>,
        aggregatedPriceInfoStart: Instant,
        aggregatedPriceInfoEnd: Instant
    ): Pair<List<FinancialReport>, List<AggregatedPriceInformationResponse>> = coroutineScope {

        val reportDeferred = async(Dispatchers.IO) {
            financialReportServiceAdapter.retrieveReports(leis).toList()
        }

        val priceInfoDeferred = async(Dispatchers.IO) {
            val symbols = getCompanies(leis).toSet().getSymbols()
            stockDataServiceAdapter.retrieveAggregatedPriceInformation(
                symbols,
                aggregatedPriceInfoStart,
                aggregatedPriceInfoEnd
            ).toList()
        }

        return@coroutineScope Pair(reportDeferred.await().toList(), priceInfoDeferred.await().toList())
    }

}
