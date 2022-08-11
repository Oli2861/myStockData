package com.mystockdata.composerservice

import com.mystockdata.composerservice.csv.ClosestEntryStrategy
import com.mystockdata.composerservice.csv.IgnoreStrategy
import com.mystockdata.composerservice.csv.LastValueStrategy
import com.mystockdata.composerservice.csv.MissingValueHandlingStrategy
import com.mystockdata.composerservice.indicator.IndicatorName
import com.mystockdata.composerservice.indicator.IndicatorType
import com.mystockdata.composerservice.indicator.RequestedIndicator
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
class ComposerController(
    @Autowired val composerservice: Composerservice
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ComposerController::class.java)
    }

    @GetMapping("precisePriceInformation/csv")
    suspend fun getPrecisePriceCSV(
        @RequestParam symbols: List<String>,
        @RequestParam(required = false) start: Instant?,
        @RequestParam(required = false) end: Instant?,
        @RequestParam(required = false) indicatorNames: List<String>?,
        @RequestParam(required = false) missingValueStrategy: String?
    ): ResponseEntity<InputStreamResource> {
        if (start == null || end == null) logger.debug("No time range specified / detected while trying to getPrecisePriceCSV, retrieving for 14 days.")

        val stream = composerservice.getPrecisePriceCSV(
            symbols = symbols,
            start = start ?: Instant.now().minus(14, ChronoUnit.DAYS),
            end = end ?: Instant.now(),
            indicators = parseIndicatorNames(indicatorNames),
            missingValueHandlingStrategy = parseMissingValueStrategy(missingValueStrategy)
        )

        val headers = HttpHeaders()
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stockData.csv")
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv")

        return ResponseEntity(stream, headers, HttpStatus.OK)

    }

    @GetMapping("aggregatedPriceInformation/csv")
    suspend fun getAggregatedPriceInfoCSV(
        @RequestParam lei: List<String>,
        @RequestParam(required = false) start: Instant?,
        @RequestParam(required = false) end: Instant?,
        @RequestParam(required = false) indicatorNames: List<String>?,
        @RequestParam(required = false) missingValueStrategy: String?,
        @RequestParam(required = false) ifrsFact: List<String>?
    ): ResponseEntity<InputStreamResource> {
        if (start == null || end == null) logger.debug("No time range specified / detected while trying to getAggregatedPriceInfoCSV, retrieving for 365 days.")

        val stream = composerservice.getAggregatedPriceCSV(
            leis = lei,
            start = start ?: Instant.now().minus(365, ChronoUnit.DAYS),
            end = end ?: Instant.now(),
            indicators = parseIndicatorNames(indicatorNames),
            missingValueHandlingStrategy = parseMissingValueStrategy(missingValueStrategy),
            factTags = ifrsFact
        )

        val headers = HttpHeaders()
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stockData.csv")
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv")

        return ResponseEntity(stream, headers, HttpStatus.OK)
    }

    private fun parseIndicatorNames(indicatorNames: List<String>?): List<RequestedIndicator> = indicatorNames?.mapNotNull { str -> indicatorMap[str] } ?: listOf()
    private fun parseMissingValueStrategy(missingValueStrategy: String?): MissingValueHandlingStrategy =  missingValueStrategyMap[missingValueStrategy] ?: LastValueStrategy

}

private val missingValueStrategyMap = mapOf(
    "LAST_VALUE" to LastValueStrategy,
    "IGNORE" to IgnoreStrategy,
    "NEXT_MATCHING" to ClosestEntryStrategy
)

private val indicatorMap = mapOf(
    IndicatorName.SMA.indicatorName to RequestedIndicator(IndicatorName.SMA, IndicatorType.TECHNICAL_INDICATOR),
    IndicatorName.PE_RATIO.indicatorName to RequestedIndicator(IndicatorName.PE_RATIO, IndicatorType.FUNDAMENTAL_INDICATOR)
)