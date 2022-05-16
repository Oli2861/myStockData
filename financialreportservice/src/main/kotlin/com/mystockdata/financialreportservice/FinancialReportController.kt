package com.mystockdata.financialreportservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/")
class FinancialReportController(
    @Autowired val financialReportService: FinancialReportService
) {
    @GetMapping("loadReports")
    suspend fun loadReports() = financialReportService.loadFinancialReports()

    @GetMapping("loadTaxonomy")
    suspend fun loadTaxonomy() = financialReportService.loadEsefTaxonomy()

}