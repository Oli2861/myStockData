package com.mystockdata.stockdataservice.precisepriceinformation

import com.mystockdata.stockdataservice.StockDataService
import org.springframework.beans.factory.annotation.Autowired
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
    suspend fun start(
        @RequestParam lei: Set<String>?
    ) = stockDataService.startRetrievingPrecisePriceInformation(lei)

    @GetMapping("/stop")
    suspend fun stop() = stockDataService.stopRetrievingPrecisePriceInformation()

    @GetMapping
    suspend fun get(
        @RequestParam lei: List<String>,
        @RequestParam(required = false) start: Instant?,
        @RequestParam(required = false) end: Instant?
    ): ResponseEntity<List<PrecisePriceInformationResponse>> {

        val data = stockDataService.getPrecisePriceInformation(
            leis = lei.toSet(),
            start = start ?: Instant.now().minus(1, ChronoUnit.DAYS),
            end = end ?: Instant.now()
        )
        return ResponseEntity.ok(data)
    }
}