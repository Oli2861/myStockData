package com.mystockdata.stockdataservice

import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedInformationProvider
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformation
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationRepository
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationResponse
import com.mystockdata.stockdataservice.company.CompanyService
import com.mystockdata.stockdataservice.company.Symbol
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformation
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationProvider
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationRepository
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationResponse
import com.mystockdata.stockdataservice.stockdataevent.StockDataEvent
import com.mystockdata.stockdataservice.stockdataevent.StockDataEventType
import com.mystockdata.stockdataservice.watchlist.WatchlistService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
    @Autowired private val watchlistService: WatchlistService,
    @Autowired private val companyService: CompanyService
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
            StockDataEventType.RETRIEVE_AGGREGATED -> if (stockDataEvent.start != null && stockDataEvent.end != null) {
                retrieveAggregatedPriceInformation(null, stockDataEvent.start, stockDataEvent.end)
            } else {
                retrieveAggregatedPriceInformation(null)
            }
            StockDataEventType.START_RETRIEVING_PRECISE -> startRetrievingPrecisePriceInformation(null)
            StockDataEventType.STOP_RETRIEVING_PRECISE -> stopRetrievingPrecisePriceInformation()
        }
    }

    suspend fun retrieveAggregatedInformationForDays(symbols: Set<String>?, days: Long) =
        retrieveAggregatedPriceInformation(symbols, Instant.now().minus(days, ChronoUnit.DAYS), Instant.now())

    suspend fun retrieveAggregatedInformationForMonths(symbols: Set<String>?, months: Long) =
        retrieveAggregatedPriceInformation(symbols, ZonedDateTime.now().minusMonths(months).toInstant(), Instant.now())


    /**
     * Retrieves aggregated Price Information for a set of symbols.
     * @param leis The legal entity identifiers to retrieve stock data for. If left empty stocks from the watchlist are retrieved.
     * @param start Start of the time window.
     * @param end End of the time window.
     * @return Flow containing the retrieved aggregated price information.
     */
    suspend fun retrieveAggregatedPriceInformation(
        leis: Set<String>?,
        start: Instant = Instant.now().minus(1, ChronoUnit.DAYS),
        end: Instant = Instant.now()
    ): Flow<AggregatedPriceInformation> {
        val symbols = symbolsForProvidedLeisOrWatchList(leis, "aggregated price information") ?: return flowOf()

        val priceInformationFlow: Flow<AggregatedPriceInformation> =
            aggregatedInformationProvider.retrieveHistoricalStockData(symbols, start, end).flatten().asFlow()

        aggregatedPriceInformationRepository.writeAggregatedPriceInformation(priceInformationFlow)

        return priceInformationFlow
    }

    /**
     * Get a List containing aggregated Stock Price Information of given symbols and a given time window.
     * @param leis Legal entity identifiers of desired companies.
     * @param start Start of the time window.
     * @param end End of the time window.
     * @return List containing the aggregated price information.
     */
    suspend fun getAggregatedPriceInformation(
        leis: Set<String>, start: Instant, end: Instant
    ): List<AggregatedPriceInformationResponse> {
        val symbols = getSymbolsForCompanies(leis)
        return aggregatedPriceInformationRepository.readAggregatedPriceInformation(
            symbols.map { it.symbol }.toSet(), start, end
        )
    }


    /**
     * Start retrieving PrecisePriceInformation from a Precise Price Information Provider.
     */
    suspend fun startRetrievingPrecisePriceInformation(leis: Set<String>?): Flow<PrecisePriceInformation> {
        val symbols = symbolsForProvidedLeisOrWatchList(leis, "precise price information") ?: return flowOf()

        scope.launch {
            precisePriceInformationProvider.establishConnection(symbols)
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
     * @param leis Legal entity identifiers of desired companies.
     * @param start Start of the time window.
     * @param end End of the time window.
     * @return List containing the desired price information.
     */
    suspend fun getPrecisePriceInformation(
        leis: Set<String>, start: Instant, end: Instant
    ): List<PrecisePriceInformationResponse> {
        val symbols = getSymbolsForCompanies(leis)
        return precisePriceInformationRepository.readPrecisePriceInformation(symbols.map { it.symbol }, start, end)
    }

    /**
     * Retrieves as symbols for the companies on the watchlist.
     * @return Set with all symbols of the watched companies.
     */
    private suspend fun getSymbolsFromWatchlist(): Set<Symbol>? {
        val watchlist = watchlistService.getWatchlist()
        return if (watchlist != null) {
            getSymbolsForCompanies(watchlist)
        } else {
            null
        }
    }

    /**
     * Retrieves all Symbols for the provided companies.
     * @param leis Legal Entity Identifiers of the companies.
     * @return Set containing all symbols.
     */
    private suspend fun getSymbolsForCompanies(leis: Set<String>): Set<Symbol> {
        return companyService.getCompanies(leis)
            .toSet()
            .map { it.getAssociatedSymbols() }
            .flatten()
            .toSet()
    }

    /**
     * Retrieves all symbols for either all specified legal entity identifiers or for all legal entity identifiers stored in the watchlist.
     * @param leis List of legal entity identifiers or null if those in the watchlist are desired.
     * @param typeOfPriceInformation Which type of price information is to be retrieved. Only used for logging purposes.
     * @return all symbols of the specified leis or the ones on the watchlist if none were specified. If the watchlist is empty null is returned.
     */
    private suspend fun symbolsForProvidedLeisOrWatchList(
        leis: Set<String>?,
        typeOfPriceInformation: String
    ): Set<Symbol>? {
        return if (!leis.isNullOrEmpty()) {
            getSymbolsForCompanies(leis)
        } else {
            logger.debug("No leis specified, retrieving $typeOfPriceInformation for watchlist.")
            val symbolsFromWatchList = getSymbolsFromWatchlist()

            if (symbolsFromWatchList == null) {
                logger.debug("Cannot retrieve $typeOfPriceInformation since watchlist is empty.")
                return null
            } else {
                symbolsFromWatchList
            }
        }
    }

}