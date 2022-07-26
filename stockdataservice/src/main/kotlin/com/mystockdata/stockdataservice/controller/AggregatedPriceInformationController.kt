package com.mystockdata.stockdataservice.controller

import com.mystockdata.stockdataservice.StockDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("v1/aggregatedPriceInformation/")
class AggregatedPriceInformationController(
    @Autowired private val stockDataService: StockDataService
) {
    @GetMapping("retrieve")
    suspend fun retrieve(@RequestParam(required = false) days: Int = 1) =
        stockDataService.retrieveAggregatedInformation(days)

    @GetMapping("csv")
    suspend fun getAggregatedPriceInformationCSV(
        @RequestParam symbols: List<String>,
        @RequestParam(required = false) start: Instant?,
        @RequestParam(required = false) end: Instant?
    ) =
        stockDataService.getAggregatedPriceInformationCSV(symbols)

}