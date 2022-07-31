package com.mystockdata.financialreportservice.financialreports

import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.Date

@RestController
@RequestMapping("v1/financialreports")
class FinancialReportController(
    @Autowired val financialReportService: FinancialReportService
) {
    @GetMapping("loadReports")
    suspend fun loadReports(
        @RequestParam(required = false) lei: List<String>?
    ): Flow<FinancialReport> = financialReportService.retrieveAvailableFinancialReports(lei ?: listOf())

    @GetMapping
    suspend fun getReports(
        @RequestParam(required = false) lei: List<String>?,
        @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") start: Date?,
        @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") end: Date?
    ): Flow<FinancialReport> = financialReportService.getReports(lei ?: listOf(), start, end)

}