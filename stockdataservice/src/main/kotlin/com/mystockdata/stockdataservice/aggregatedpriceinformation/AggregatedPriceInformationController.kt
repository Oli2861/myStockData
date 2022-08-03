package com.mystockdata.stockdataservice.aggregatedpriceinformation

import com.mystockdata.stockdataservice.StockDataService
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
        @RequestParam symbols: Set<String>?,
        @RequestParam(required = false) start: Instant?,
        @RequestParam(required = false) end: Instant?
    ): Flow<AggregatedPriceInformation> {
        return if(start == null || end == null) {
            logger.debug("No time range specified, retrieving for 1 month")
            stockDataService.retrieveAggregatedInformationForMonths(symbols, 1)
        }else{
            stockDataService.retrieveAggregatedPriceInformation(symbols, start, end)
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