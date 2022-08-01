package com.mystockdata.composerservice

import com.mystockdata.composerservice.csv.MissingValueHandlingStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
@RequestMapping("v1/")
class Controller(
    @Autowired val composerservice: Composerservice
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Controller::class.java)
    }

    @GetMapping("test")
    suspend fun test() = composerservice.test()

    @GetMapping("csv")
    suspend fun csv(
        @RequestParam symbols: List<String>,
        @RequestParam(required = false) start: Instant?,
        @RequestParam(required = false) end: Instant?,
        @RequestParam(required = false) indicators: List<String>?,
        @RequestParam(required = false) missingValueStrategy: String?
    ): ResponseEntity<InputStreamResource> {
        if(start == null || end == null) logger.debug("No time range specified, retrieving for 365 days")

        val missingValueHandlingStrategy = when(missingValueStrategy){
            "LAST_VALUE" -> MissingValueHandlingStrategy.LAST_VALUE
            "IGNORE" -> MissingValueHandlingStrategy.IGNORE
            "NEXT_MATCHING" -> MissingValueHandlingStrategy.NEXT_MATCHING
            else -> MissingValueHandlingStrategy.LAST_VALUE
        }

        val stream = composerservice.getStockData(
            symbols = symbols.ifEmpty { listOf("VOW3.DE", "SOW.DE", "SAP.DE", "AMC", "TSLA") },
            start = start ?: Instant.now().minus(365, ChronoUnit.DAYS),
            end = end ?: Instant.now(),
            indicatorName = listOf(IndicatorName.SMA),
            missingValueHandlingStrategy = missingValueHandlingStrategy ?: MissingValueHandlingStrategy.LAST_VALUE
        )

        val headers = HttpHeaders()
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stockData.csv")
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv")

        return ResponseEntity(stream, headers, HttpStatus.OK)
    }

}