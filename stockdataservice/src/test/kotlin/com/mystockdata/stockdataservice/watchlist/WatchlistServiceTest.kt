package com.mystockdata.stockdataservice.watchlist;

import com.mystockdata.stockdataservice.company.Symbol
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner::class)
@DataMongoTest
class WatchlistServiceTest(
    @Autowired val watchlistRepository: WatchlistRepository
) {
    private val subject = WatchlistService(watchlistRepository)

    private val watchlist = listOf("lei", "lei1")

    @BeforeEach
    fun clearDB() = runBlocking {
        watchlistRepository.deleteAll()
    }

    @Test
    fun getWatchListNotExistingTest() = runBlocking {
        val actual = subject.getWatchlist()
        Assertions.assertEquals(null, actual)
    }

    @Test
    fun removeFromWatchListTest() = runBlocking {
        subject.addToWatchList(watchlist.toSet())
        val response = subject.removeFromWatchList(setOf(watchlist[0]))
        val actual = subject.getWatchlist()
        Assertions.assertEquals(1, actual!!.size)
        Assertions.assertTrue(actual.contains(watchlist[1]))
        Assertions.assertEquals(setOf(watchlist[0]) , response)
    }

    @Test
    fun addToWatchListNotExistingTest() = runBlocking {
        val response = subject.addToWatchList(watchlist.toSet())
        val actual = watchlistRepository.findAll().first()
        Assertions.assertEquals(watchlist, response.toList())
        Assertions.assertEquals(Watchlist(WatchlistConstants.WATCHLIST_ID, watchlist.toMutableSet()), actual)
    }

    @Test
    fun addToWatchListTest() = runBlocking {
        subject.addToWatchList(watchlist.toSet())
        val lei = "lei2"
        val response = subject.addToWatchList(setOf(lei))
        val actual = watchlistRepository.findAll().first()
        Assertions.assertEquals(lei, response.first())
        val expectedWatchList = watchlist.toMutableSet()
        expectedWatchList.add(lei)
        Assertions.assertEquals(Watchlist(WatchlistConstants.WATCHLIST_ID, expectedWatchList), actual)
    }
}
