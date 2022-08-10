package com.mystockdata.stockdataservice.watchlist

import com.mystockdata.stockdataservice.watchlist.WatchlistConstants.WATCHLIST_ID
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

object WatchlistConstants{
    const val WATCHLIST_ID = "watchlist"
}

@Document(collection = "Watchlist")
data class Watchlist (
    @Id val id: String = WATCHLIST_ID,
    val lei: MutableSet<String>
)

