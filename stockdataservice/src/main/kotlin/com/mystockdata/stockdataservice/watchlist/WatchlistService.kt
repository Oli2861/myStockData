package com.mystockdata.stockdataservice.watchlist

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WatchlistService(
    @Autowired private val watchlistRepository: WatchlistRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(WatchlistService::class.java)
    }

    suspend fun getWatchlist(): Set<String>? {
        return watchlistRepository.findById(WatchlistConstants.WATCHLIST_ID)?.lei?.toSet()
    }

    suspend fun removeFromWatchList(symbols: Set<String>): Set<String>? {
        val watchlist = watchlistRepository.findById(WatchlistConstants.WATCHLIST_ID)
        if (watchlist != null) {
            val removed = symbols.filter { watchlist.lei.contains(it) }
            watchlist.lei.removeAll(symbols)
            val saved = watchlistRepository.save(watchlist)
            return removed.toSet()
        }
        return null
    }

    suspend fun addToWatchList(lei: Set<String>): List<String> {
        var watchlist: Watchlist? = watchlistRepository.findById(WatchlistConstants.WATCHLIST_ID)
        if (watchlist == null) {
            logger.debug("No existing watchlist found, creating a new one.")
            watchlist = Watchlist(WatchlistConstants.WATCHLIST_ID, mutableSetOf())
        }
        watchlist.lei.addAll(lei)
        return watchlistRepository.save(watchlist).lei.filter { lei.contains(it) }
    }
}