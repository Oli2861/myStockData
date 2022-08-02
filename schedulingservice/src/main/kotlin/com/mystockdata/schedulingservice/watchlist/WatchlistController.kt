package com.mystockdata.schedulingservice.watchlist

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("v1/watchlist")
class WatchlistController(
    @Autowired val companyService: CompanyService
) {
    @GetMapping
    suspend fun getWatchlist() = companyService.getWatchlist()

    @PutMapping
    suspend fun addToWatchlist(
        @RequestParam lei: List<String>
    ) = companyService.addToWatchList(lei)

    @DeleteMapping
    suspend fun removeFromWatchlist(
        @RequestParam lei: String
    ) = companyService.removeFromWatchList(lei)
}