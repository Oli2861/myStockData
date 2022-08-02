package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedInformationProvider
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationRepository
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationRepository
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationProvider
import com.mystockdata.stockdataservice.watchlist.Watchlist
import com.mystockdata.stockdataservice.watchlist.WatchlistConstants
import com.mystockdata.stockdataservice.watchlist.WatchlistRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner::class)
@DataMongoTest
class StockDataServiceTest(
    @Autowired val watchlistRepository: WatchlistRepository
    ) {

    private val aggregatedPriceInformationMock = Mockito.mock(AggregatedInformationProvider::class.java)
    private val precisePriceInformationProviderMock = Mockito.mock(PrecisePriceInformationProvider::class.java)
    private val precisePriceInformationRepositoryMock = Mockito.mock(PrecisePriceInformationRepository::class.java)
    private val aggregatedPriceInformationRepositoryMock = Mockito.mock(AggregatedPriceInformationRepository::class.java)

    private val subject = StockDataService(
        aggregatedPriceInformationMock,
        precisePriceInformationProviderMock,
        precisePriceInformationRepositoryMock,
        aggregatedPriceInformationRepositoryMock,
        watchlistRepository
    )

    val watchlist = listOf("lei", "lei1")

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
        subject.addToWatchList(watchlist)
        val response = subject.removeFromWatchList(watchlist[0])
        val actual = subject.getWatchlist()
        Assertions.assertEquals(1, actual!!.size)
        Assertions.assertEquals(watchlist[1], actual.first())
        Assertions.assertEquals(watchlist[0], response)
    }

    @Test
    fun addToWatchListNotExistingTest() = runBlocking {
        val response = subject.addToWatchList(watchlist)
        val actual = watchlistRepository.findAll().first()
        Assertions.assertEquals(watchlist, response)
        Assertions.assertEquals(Watchlist(WatchlistConstants.watchlistID, watchlist.toMutableSet()), actual)
    }

    @Test
    fun addToWatchListTest() = runBlocking {
        subject.addToWatchList(watchlist)
        val response = subject.addToWatchList(listOf("lei3"))
        val actual = watchlistRepository.findAll().first()
        Assertions.assertEquals("lei3", response.first())
        val expectedWatchList = watchlist.toMutableSet()
        expectedWatchList.add("lei3")
        Assertions.assertEquals(Watchlist(WatchlistConstants.watchlistID, expectedWatchList), actual)
    }

}