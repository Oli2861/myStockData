package com.mystockdata.stockdataservice.watchlist

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("v1/watchlist")
class WatchlistController(
    @Autowired val watchlistService: WatchlistService
) {
    @GetMapping
    suspend fun getWatchlist() = watchlistService.getWatchlist()

    @PutMapping
    suspend fun addToWatchlist(
        @RequestParam lei: List<String>
    ) = watchlistService.addToWatchList(lei.toSet())

    @DeleteMapping
    suspend fun removeFromWatchlist(
        @RequestParam lei: List<String>
    ) = watchlistService.removeFromWatchList(lei.toSet())
}