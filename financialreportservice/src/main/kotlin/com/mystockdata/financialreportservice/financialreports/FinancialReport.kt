package com.mystockdata.financialreportservice.financialreports

import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document(collection = "FinancialReport")
data class FinancialReport(
    val endOfReportingPeriod: Date,
    val entityIdentifier: String,
    val entityIdentifierScheme: String,
    val factList: List<Fact>
)