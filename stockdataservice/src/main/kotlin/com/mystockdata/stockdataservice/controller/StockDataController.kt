package com.mystockdata.stockdataservice.controller

import com.mystockdata.stockdataservice.StockDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController("v1/")
class StockDataController(
    @Autowired private val stockDataService: StockDataService
) {
    @GetMapping("precisePriceInformation/start")
    suspend fun start() = stockDataService.startRetrievingPrecisePriceInformation()

    @GetMapping("aggregatedPriceInformation/retrieve")
    suspend fun retrieve(@RequestParam(required = false) days: Int = 1) = stockDataService.retrieveAggregatedInformation(days)

}