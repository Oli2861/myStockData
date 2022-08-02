package com.mystockdata.stockdataservice.watchlist

import com.mystockdata.stockdataservice.StockDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("v1/watchlist")
class WatchlistController(
    @Autowired val stockDataService: StockDataService
) {
    @GetMapping
    suspend fun getWatchlist() = stockDataService.getWatchlist()

    @PutMapping
    suspend fun addToWatchlist(
        @RequestParam lei: List<String>
    ) = stockDataService.addToWatchList(lei)

    @DeleteMapping
    suspend fun removeFromWatchlist(
        @RequestParam lei: String
    ) = stockDataService.removeFromWatchList(lei)
}