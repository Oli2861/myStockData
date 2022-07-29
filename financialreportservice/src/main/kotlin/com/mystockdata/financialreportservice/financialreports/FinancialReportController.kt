package com.mystockdata.financialreportservice.financialreports

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/")
class FinancialReportController(
    @Autowired val financialReportService: FinancialReportService
) {
    @GetMapping("loadReports")
    suspend fun loadReports(
        @RequestParam(required = false) lei: List<String>?
    ) = financialReportService.retrieveAvailableFinancialReports(lei ?: listOf())

}