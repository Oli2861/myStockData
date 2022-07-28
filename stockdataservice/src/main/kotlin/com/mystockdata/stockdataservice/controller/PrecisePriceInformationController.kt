package com.mystockdata.stockdataservice.controller

import com.mystockdata.stockdataservice.StockDataService
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformationResponse
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
@RequestMapping("v1/precisePriceInformation")
class PrecisePriceInformationController(
    @Autowired private val stockDataService: StockDataService
) {
    @GetMapping("/start")
    suspend fun start() = stockDataService.startRetrievingPrecisePriceInformation()

    @GetMapping
    suspend fun get(
        @RequestParam symbols: List<String>,
        @RequestParam(required = false) start: Instant?,
        @RequestParam(required = false) end: Instant?
    ): ResponseEntity<List<PrecisePriceInformationResponse>> {

        val data = stockDataService.getPrecisePriceInformation(
            symbols = symbols,
            start = start ?: Instant.now().minus(1, ChronoUnit.DAYS),
            end = end ?: Instant.now()
        )

        return ResponseEntity.ok(data)

    }

    @GetMapping(value = ["/csv"], produces = ["text/csv"])
    suspend fun getPreciseInformationCSV(
        @RequestParam symbols: List<String>,
        @RequestParam(required = false) start: Instant?,
        @RequestParam(required = false) end: Instant?
    ): ResponseEntity<InputStreamResource> {

        val stream = stockDataService.getPrecisePriceInformationCSV(
            symbols = symbols,
            start = start ?: Instant.now().minus(1, ChronoUnit.DAYS),
            end = end ?: Instant.now()
        )

        val headers = HttpHeaders()
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=precisePriceInformation.csv")
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv")

        return ResponseEntity(stream, headers, HttpStatus.OK)

    }

}