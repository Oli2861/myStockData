package com.mystockdata.schedulingservice.watchlist

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CompanyService(
    @Autowired val companyRepository: CompanyRepository,
    @Autowired val watchlistRepository: WatchlistRepository
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

    suspend fun getWatchlist(): MutableSet<String>? {
        return watchlistRepository.findById(WatchlistConstants.watchlistID)?.leis
    }

    suspend fun removeFromWatchList(lei: String): String? {
        val watchlist = watchlistRepository.findById(WatchlistConstants.watchlistID)
        if (watchlist != null) {
            watchlist.leis.remove(lei)
            watchlistRepository.save(watchlist)
            return lei
        }
        return null
    }

    suspend fun addToWatchList(lei: List<String>): List<String> {
        var watchlist: Watchlist? = watchlistRepository.findById(WatchlistConstants.watchlistID)
        if(watchlist == null){
            logger.debug("No existing watchlist found, creating a new one.")
            watchlist = Watchlist(WatchlistConstants.watchlistID, mutableSetOf())
        }
        watchlist.leis.addAll(lei)
        return watchlistRepository.save(watchlist).leis.filter { lei.contains(it) }
    }

}