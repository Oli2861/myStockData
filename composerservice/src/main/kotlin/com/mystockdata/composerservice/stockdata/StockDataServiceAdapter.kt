package com.mystockdata.composerservice.stockdata

import com.mystockdata.composerservice.stockdata.StockDataServiceAdapterConstants.END_REQUEST_PARAM
import com.mystockdata.composerservice.stockdata.StockDataServiceAdapterConstants.START_REQUEST_PARAM
import com.mystockdata.composerservice.stockdata.StockDataServiceAdapterConstants.SYMBOLS_REQUEST_PARAM
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import java.time.Instant
import java.time.temporal.ChronoUnit

object StockDataServiceAdapterConstants {
    const val SYMBOLS_REQUEST_PARAM: String = "symbol"
    const val START_REQUEST_PARAM: String = "start"
    const val END_REQUEST_PARAM: String = "end"
}

@Component
class StockDataServiceAdapter(
    @Qualifier("stockDataServiceWebClient") @Autowired private val stockDataServiceWebClient: WebClient
) {

    /**
     * Retrieves aggregated price information from the stock data service.
     * @param symbols symbols of the desired stocks.
     * @param start start of the time window.
     * @param end end of the time window.
     * @return flow emitting the retrieved aggregated price information.
     */
    suspend fun getAggregatedPriceInformation(
        symbols: Set<String>,
        start: Instant? = Instant.now().minus(30, ChronoUnit.DAYS),
        end: Instant? = Instant.now()
    ): Flow<AggregatedPriceInformationResponse> {
        return stockDataServiceWebClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/v1/aggregatedPriceInformation")
                    .queryParam(SYMBOLS_REQUEST_PARAM, symbols)
                    .queryParam(START_REQUEST_PARAM, start)
                    .queryParam(END_REQUEST_PARAM, end)
                    .build()
            }.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType(MediaType.APPLICATION_JSON))
            .retrieve()
            .bodyToFlow()
    }

    /**
     * Tell the stockdataservice to retrieve aggregated price information from the remote data source.
     * @param symbols symbols of the desired stocks.
     * @param start start of the time window.
     * @param end end of the time window.
     * @return flow emitting the retrieved aggregated price information.
     */
    suspend fun retrieveAggregatedPriceInformation(
        symbols: Set<String>,
        start: Instant? = Instant.now().minus(30, ChronoUnit.DAYS),
        end: Instant? = Instant.now()
    ): Flow<AggregatedPriceInformationResponse> {
        return stockDataServiceWebClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/v1/aggregatedPriceInformation/retrieve")
                    .queryParam(SYMBOLS_REQUEST_PARAM, symbols)
                    .queryParam(START_REQUEST_PARAM, start)
                    .queryParam(END_REQUEST_PARAM, end)
                    .build()
            }.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType(MediaType.APPLICATION_JSON))
            .retrieve()
            .bodyToFlow()
    }

    /**
     * Retrieves precise price information from the stock data service.
     * @param symbols symbols of the desired stocks.
     * @param start start of the time window.
     * @param end end of the time window.
     * @return flow emitting the retrieved precise price information.
     */
    suspend fun getPrecisePriceInformation(
        symbols: List<String>,
        start: Instant = Instant.now().minus(1, ChronoUnit.DAYS),
        end: Instant = Instant.now()
    ): Flow<PrecisePriceInformationResponse> {
        return stockDataServiceWebClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/v1/precisePriceInformation")
                    .queryParam(SYMBOLS_REQUEST_PARAM, symbols)
                    .queryParam(START_REQUEST_PARAM, start)
                    .queryParam(END_REQUEST_PARAM, end)
                    .build()
            }.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType(MediaType.APPLICATION_JSON))
            .retrieve()
            .bodyToFlow()
    }

    /**
     * Adds a set of symbols to the watchlist of the stockdataservice.
     * @param symbols symbols to be added to the watchlist contained in the stockdataservice.
     * @return Received response.
     */
    suspend fun addToWatchlist(symbols: Set<String>): Flow<String>{
        return stockDataServiceWebClient.put()
            .uri{ uriBuilder ->
                uriBuilder.path("v1/watchlist")
                    .queryParam(SYMBOLS_REQUEST_PARAM, symbols)
                    .build()
            }.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType(MediaType.APPLICATION_JSON))
            .retrieve()
            .bodyToFlow()
    }
}