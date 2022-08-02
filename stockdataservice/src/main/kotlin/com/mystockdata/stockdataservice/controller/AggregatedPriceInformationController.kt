package com.mystockdata.stockdataservice.controller

import com.mystockdata.stockdataservice.StockDataService
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformation
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationResponse
import kotlinx.coroutines.flow.Flow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.log

@RestController
@RequestMapping("v1/aggregatedPriceInformation")
class AggregatedPriceInformationController(
    @Autowired private val stockDataService: StockDataService
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AggregatedPriceInformationController::class.java)
    }

    @GetMapping("/retrieve")
    suspend fun retrieve(
        @RequestParam symbols: Set<String>,
        @RequestParam(required = false) days: Long?,
        @RequestParam(required = false) months: Long?
    ): Flow<AggregatedPriceInformation> {
        return if (days != null) {
            stockDataService.retrieveAggregatedInformationForDays(symbols, days)
        } else if (months != null) {
            stockDataService.retrieveAggregatedInformationForMonths(symbols, months)
        } else {
            // If no time frame is provided, retrieve for 1 month
            logger.debug("No time range specified, retrieving for 1 month")
            stockDataService.retrieveAggregatedInformationForMonths(symbols, 1)
        }
    }

    @GetMapping
    suspend fun get(
        @RequestParam symbols: Set<String>,
        @RequestParam(required = false) start: Instant?,
        @RequestParam(required = false) end: Instant?
    ): ResponseEntity<List<AggregatedPriceInformationResponse>> {
        if(start == null || end == null) logger.debug("No time range specified, retrieving for 30 days")
        val data = stockDataService.getAggregatedPriceInformation(
            symbols = symbols,
            start = start ?: Instant.now().minus(30, ChronoUnit.DAYS),
            end = end ?: Instant.now()
        )

        return ResponseEntity.ok(data)

    }
}