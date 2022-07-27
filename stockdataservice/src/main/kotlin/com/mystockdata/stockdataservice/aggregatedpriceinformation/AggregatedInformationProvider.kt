package com.mystockdata.stockdataservice.aggregatedpriceinformation

import java.time.Instant
import java.time.LocalDate

abstract class AggregatedInformationProvider {
    /**
     * Retrieves aggregated price information for a given time window for the given symbol.
     *  @param symbols Symbols of the securities.
     *  @param startDate Start date.
     *  @param endDate End date.
     *  @return List of the retrieved stock data.
     */
    suspend fun retrieveHistoricalStockData(symbols: List<String>, startDate: Instant, endDate: Instant) = symbols.mapNotNull { retrieveHistoricalStockData(it, startDate, endDate) }

    /**
     * Retrieves aggregated price information for a given time window for the given symbol.
     *  @param stockSymbol Symbol of the security.
     *  @param startDate Start date.
     *  @param endDate End date.
     *  @return List of the retrieved stock data.
     */
    abstract suspend fun retrieveHistoricalStockData(stockSymbol: String, startDate: Instant, endDate: Instant): List<AggregatedPriceInformation>?

}