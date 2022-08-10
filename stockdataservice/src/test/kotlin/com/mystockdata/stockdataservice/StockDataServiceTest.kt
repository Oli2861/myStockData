package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedInformationProvider
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationRepository
import com.mystockdata.stockdataservice.company.CompanyService
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationProvider
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationRepository
import com.mystockdata.stockdataservice.company.Symbol
import com.mystockdata.stockdataservice.watchlist.Watchlist
import com.mystockdata.stockdataservice.watchlist.WatchlistConstants
import com.mystockdata.stockdataservice.watchlist.WatchlistRepository
import com.mystockdata.stockdataservice.watchlist.WatchlistService
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


class StockDataServiceTest{

    private val aggregatedPriceInformationMock = Mockito.mock(AggregatedInformationProvider::class.java)
    private val precisePriceInformationProviderMock = Mockito.mock(PrecisePriceInformationProvider::class.java)
    private val precisePriceInformationRepositoryMock = Mockito.mock(PrecisePriceInformationRepository::class.java)
    private val aggregatedPriceInformationRepositoryMock = Mockito.mock(AggregatedPriceInformationRepository::class.java)
    private val watchlistServiceMock = Mockito.mock(WatchlistService::class.java)
    private val companyServiceMock = Mockito.mock(CompanyService::class.java)

    private val subject = StockDataService(
        aggregatedPriceInformationMock,
        precisePriceInformationProviderMock,
        precisePriceInformationRepositoryMock,
        aggregatedPriceInformationRepositoryMock,
        watchlistServiceMock,
        companyServiceMock
    )



}