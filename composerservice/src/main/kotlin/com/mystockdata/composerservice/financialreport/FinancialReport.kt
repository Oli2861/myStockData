package com.mystockdata.composerservice.financialreport

import com.mystockdata.financialreportservice.financialreports.Fact
import java.util.*

data class FinancialReport(
    val id: String? = null,
    val endOfReportingPeriod: Date,
    val entityIdentifier: String,
    val entityIdentifierScheme: String,
    val factList: List<Fact>
)