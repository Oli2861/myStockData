package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedInformationProvider
import com.mystockdata.stockdataservice.persistence.AggregatedPriceInformationRepository
import com.mystockdata.stockdataservice.persistence.PrecisePriceInformationRepository
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationProvider
import org.mockito.Mockito

class StockDataServiceTest {

    private val aggregatedPriceInformationMock = Mockito.mock(AggregatedInformationProvider::class.java)
    private val precisePriceInformationProviderMock = Mockito.mock(PrecisePriceInformationProvider::class.java)
    private val precisePriceInformationRepositoryMock = Mockito.mock(PrecisePriceInformationRepository::class.java)
    private val aggregatedPriceInformationRepositoryMock = Mockito.mock(AggregatedPriceInformationRepository::class.java)

    private val subject = StockDataService(
        aggregatedPriceInformationMock,
        precisePriceInformationProviderMock,
        precisePriceInformationRepositoryMock,
        aggregatedPriceInformationRepositoryMock
    )


}