package com.mystockdata.stockdataservice.controller

import com.mystockdata.stockdataservice.StockDataService
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformation
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.temporal.ChronoUnit

@RestController
@RequestMapping("v1/aggregatedPriceInformation/")
class AggregatedPriceInformationController(
    @Autowired private val stockDataService: StockDataService
) {
    @GetMapping("retrieve")
    suspend fun retrieve(
        @RequestParam(required = false) days: Long?,
        @RequestParam(required = false) months: Long?
    ): Flow<AggregatedPriceInformation> {
        return if (days != null) {
            stockDataService.retrieveAggregatedInformationForDays(days)
        } else if (months != null) {
            stockDataService.retrieveAggregatedInformationForMonths(months)
        } else {
            // If no time frame is provided, retrieve for 1 month
            stockDataService.retrieveAggregatedInformationForMonths(1)
        }
    }

    @GetMapping("csv")
    suspend fun getAggregatedPriceInformationCSV(
        @RequestParam symbols: List<String>,
        @RequestParam(required = false) start: Instant?,
        @RequestParam(required = false) end: Instant?
    ): ResponseEntity<InputStreamResource> {
        val startInstant: Instant = start ?: Instant.now().minus(30, ChronoUnit.DAYS)
        val endInstant: Instant = end ?: Instant.now()
        val stream = stockDataService.getAggregatedPriceInformationCSV(symbols, startInstant, endInstant)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=aggregatedPriceInformation.csv")
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv")

        return ResponseEntity(stream, headers, HttpStatus.OK)
    }

}