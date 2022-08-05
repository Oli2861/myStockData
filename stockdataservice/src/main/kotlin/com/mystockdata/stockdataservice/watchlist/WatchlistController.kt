package com.mystockdata.stockdataservice.watchlist

import com.mystockdata.stockdataservice.StockDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("v1/watchlist")
class WatchlistController(
    @Autowired val stockDataService: StockDataService
) {
    @GetMapping
    suspend fun getWatchlist() = stockDataService.getWatchlist()

    @PutMapping
    suspend fun addToWatchlist(
        @RequestParam symbol: List<String>
    ) = stockDataService.addToWatchList(symbol)

    @DeleteMapping
    suspend fun removeFromWatchlist(
        @RequestParam symbol: List<String>
    ) = stockDataService.removeFromWatchList(symbol.toSet())
}