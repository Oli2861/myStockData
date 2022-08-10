package com.mystockdata.stockdataservice.aggregatedpriceinformation

import com.mystockdata.stockdataservice.company.Symbol
import java.time.Instant

abstract class AggregatedInformationProvider {
    /**
     * Retrieves aggregated price information for a given time window for the given symbol.
     *  @param symbols Symbols of the securities.
     *  @param startDate Start date.
     *  @param endDate End date.
     *  @return List of the retrieved stock data.
     */
    suspend fun retrieveHistoricalStockData(symbols: Set<Symbol>, startDate: Instant, endDate: Instant) = symbols.mapNotNull { retrieveHistoricalStockData(it, startDate, endDate) }

    /**
     * Retrieves aggregated price information for a given time window for the given symbol.
     *  @param stockSymbol Symbol of the security.
     *  @param startDate Start date.
     *  @param endDate End date.
     *  @return List of the retrieved stock data.
     */
    abstract suspend fun retrieveHistoricalStockData(stockSymbol: Symbol, startDate: Instant, endDate: Instant): List<AggregatedPriceInformation>?

}