package com.mystockdata.stockdataservice.watchlist

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WatchlistRepository: CoroutineCrudRepository<Watchlist, String> {

}