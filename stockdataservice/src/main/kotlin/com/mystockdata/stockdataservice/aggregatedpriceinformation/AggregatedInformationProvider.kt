package com.mystockdata.stockdataservice.aggregatedpriceinformation

import java.time.LocalDate

abstract class AggregatedInformationProvider {

    /**
     * Retrieves aggregated stock information concerning the past 30 days for a given symbol.
     * @param symbols Symbols of the securities.
     * @return List containing lists of the retrieved information
     */
    suspend fun retrieveAggregatedInformationForPastMonth(symbols: List<String>): List<List<StockDataOHLCV>> =
        symbols.mapNotNull { retrieveHistoricalStockData(it, LocalDate.now().minusMonths(1), LocalDate.now()) }

    /**
     * Retrieves latest aggregated stock information for a given symbol.
     * @param symbols Symbols of the securities.
     * @return List containing one aggregated stock information object for each symbol.
     */
    suspend fun retrieveAggregatedInformationForYesterday(symbols: List<String>): List<StockDataOHLCV> =
        symbols.mapNotNull { retrieveHistoricalStockData(it, LocalDate.now().minusDays(1), LocalDate.now())?.firstOrNull() }

    /**
     * Retrieves aggregated price information for a given time window for the given symbol.
     *  @param stockSymbol Symbol of the security.
     *  @param startDate Start date.
     *  @param endDate End date.
     *  @return List of the retrieved stock data.
     */
    abstract suspend fun retrieveHistoricalStockData(stockSymbol: String, startDate: LocalDate, endDate: LocalDate): List<StockDataOHLCV>?

}