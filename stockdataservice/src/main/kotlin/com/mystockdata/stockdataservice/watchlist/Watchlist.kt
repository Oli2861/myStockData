package com.mystockdata.stockdataservice.watchlist

import com.mystockdata.stockdataservice.watchlist.WatchlistConstants.watchlistID
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

object WatchlistConstants{
    const val watchlistID = "watchlist"
}

@Document(collection = "Watchlist")
data class Watchlist (
    @Id val id: String = watchlistID,
    val leis: MutableSet<String>
)