package com.mystockdata.stockdataservice.controller

import com.mystockdata.stockdataservice.StockDataService
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
@RequestMapping("v1/precisePriceInformation/")
class PrecisePriceInformationController(
    @Autowired private val stockDataService: StockDataService
) {
    @GetMapping("start")
    suspend fun start() = stockDataService.startRetrievingPrecisePriceInformation()

    @GetMapping(value = ["csv"], produces = ["text/csv"])
    suspend fun getPreciseInformationCSV(
        @RequestParam symbols: List<String>,
        @RequestParam(required = false) start: Instant?,
        @RequestParam(required = false) end: Instant?
    ): ResponseEntity<InputStreamResource> {

        val startInstant: Instant = start ?: Instant.now().minus(1, ChronoUnit.DAYS)
        val endInstant: Instant = end?: Instant.now()

        val stream = stockDataService.getPrecisePriceInformationCSV(symbols, startInstant, endInstant)

        val headers = HttpHeaders()
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=precisePriceInformation.csv")
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv")

        return ResponseEntity(stream, headers, HttpStatus.OK)
    }

}