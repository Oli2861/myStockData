package com.mystockdata.composerservice.financialreport

import java.util.*

data class FinancialReport(
    val id: String? = null,
    val endOfReportingPeriod: Date,
    val entityIdentifier: String,
    val entityIdentifierScheme: String,
    val factList: List<Fact>
)
