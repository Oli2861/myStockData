package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedInformationProvider
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformation
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationRepository
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationRepository
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformation
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationProvider
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationResponse
import com.mystockdata.stockdataservice.stockdataevent.StockDataEvent
import com.mystockdata.stockdataservice.stockdataevent.StockDataEventType
import com.mystockdata.stockdataservice.watchlist.Watchlist
import com.mystockdata.stockdataservice.watchlist.WatchlistConstants
import com.mystockdata.stockdataservice.watchlist.WatchlistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Service
class StockDataService(
    @Autowired private val aggregatedInformationProvider: AggregatedInformationProvider,
    @Autowired private val precisePriceInformationProvider: PrecisePriceInformationProvider,
    @Autowired private val precisePriceInformationRepository: PrecisePriceInformationRepository,
    @Autowired private val aggregatedPriceInformationRepository: AggregatedPriceInformationRepository,
    @Autowired private val watchlistRepository: WatchlistRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StockDataService::class.java)
    }

    /**
     * Handles incoming events from scheduling service.
     * @param stockDataEvent Received event.
     */
    suspend fun handleEvent(stockDataEvent: StockDataEvent) {

        when (stockDataEvent.stockDataEventType) {
            StockDataEventType.RETRIEVE_AGGREGATED -> getWatchlist().let {
                if (it != null) {
                    if (stockDataEvent.start != null && stockDataEvent.end != null) {
                        retrieveAggregatedPriceInformation(it, stockDataEvent.start, stockDataEvent.end)
                    } else {
                        retrieveAggregatedPriceInformation(it)
                    }
                } else {
                    logger.debug("Cannot retrieve aggregated price information since watchlist is empty.")
                }
            }
            StockDataEventType.START_RETRIEVING_PRECISE -> getWatchlist().let {
                if (it != null) startRetrievingPrecisePriceInformation(it)
                else logger.debug("Cannot retrieve precise price information since watchlist is empty.")
            }
            StockDataEventType.STOP_RETRIEVING_PRECISE -> stopRetrievingPrecisePriceInformation()
        }
    }

    suspend fun getWatchlist(): Set<String>? {
        return watchlistRepository.findById(WatchlistConstants.watchlistID)?.symbols
    }

    suspend fun removeFromWatchList(lei: Set<String>): Set<String>? {
        val watchlist = watchlistRepository.findById(WatchlistConstants.watchlistID)
        if (watchlist != null) {
            watchlist.symbols.removeAll(lei)
            watchlistRepository.save(watchlist)
            return lei
        }
        return null
    }

    suspend fun addToWatchList(lei: List<String>): List<String> {
        var watchlist: Watchlist? = watchlistRepository.findById(WatchlistConstants.watchlistID)
        if (watchlist == null) {
            logger.debug("No existing watchlist found, creating a new one.")
            watchlist = Watchlist(WatchlistConstants.watchlistID, mutableSetOf())
        }
        watchlist.symbols.addAll(lei)
        return watchlistRepository.save(watchlist).symbols.filter { lei.contains(it) }
    }

    suspend fun retrieveAggregatedInformationForDays(symbols: Set<String>?, days: Long) =
        retrieveAggregatedPriceInformation(symbols, Instant.now().minus(days, ChronoUnit.DAYS), Instant.now())

    suspend fun retrieveAggregatedInformationForMonths(symbols: Set<String>?, months: Long) =
        retrieveAggregatedPriceInformation(symbols, ZonedDateTime.now().minusMonths(months).toInstant(), Instant.now())

    /**
     * Retrieves aggregated Price Information.
     * @param symbols The symbols to retrieve stock data for. If left empty stocks from the watchlist are retrieved.
     * @param start Start of the time window.
     * @param end End of the time window.
     * @return Flow containing the retrieved aggregated price information.
     */
    suspend fun retrieveAggregatedPriceInformation(
        symbols: Set<String>?,
        start: Instant = Instant.now().minus(1, ChronoUnit.DAYS),
        end: Instant = Instant.now()
    ): Flow<AggregatedPriceInformation> {
        val usedSymbols: Set<String> = if (!symbols.isNullOrEmpty()) symbols else getWatchlist() ?: return flowOf()
        val priceInformationFlow: Flow<AggregatedPriceInformation> =
            aggregatedInformationProvider.retrieveHistoricalStockData(usedSymbols.toList(), start, end).flatten()
                .asFlow()
        aggregatedPriceInformationRepository.writeAggregatedPriceInformation(priceInformationFlow)
        return priceInformationFlow
    }

    /**
     * Get a List containing aggregated Stock Price Information of given symbols and a given time window.
     * @param symbols Stock symbols of interest.
     * @param start Start of the time window.
     * @param end End of the time window.
     * @return List containing the aggregated price information.
     */
    suspend fun getAggregatedPriceInformation(
        symbols: Set<String>, start: Instant, end: Instant
    ) = aggregatedPriceInformationRepository.readAggregatedPriceInformation(symbols, start, end)


    /**
     * Start retrieving PrecisePriceInformation from a Precise Price Information Provider.
     */
    suspend fun startRetrievingPrecisePriceInformation(symbols: Set<String>?): Flow<PrecisePriceInformation> {
        val usedSymbols = (if (symbols.isNullOrEmpty()) getWatchlist() else symbols) ?: return flowOf()
        scope.launch {
            precisePriceInformationProvider.establishConnection(usedSymbols)
            precisePriceInformationRepository.writePrecisePriceInformation(precisePriceInformationProvider.flow)
        }
        return precisePriceInformationProvider.flow
    }

    /**
     * Start retrieving PrecisePriceInformation from a Precise Price Information Provider.
     */
    suspend fun stopRetrievingPrecisePriceInformation() {
        precisePriceInformationProvider.close()
    }

    /**
     * Get a List of aggregated Stock Price Information of given symbols and a given time window.
     * @param symbols Stock symbols of interest.
     * @param start Start of the time window.
     * @param end End of the time window.
     * @return List containing the desired price information.
     */
    suspend fun getPrecisePriceInformation(
        symbols: List<String>, start: Instant, end: Instant
    ): List<PrecisePriceInformationResponse> =
        precisePriceInformationRepository.readPrecisePriceInformation(symbols, start, end)

}