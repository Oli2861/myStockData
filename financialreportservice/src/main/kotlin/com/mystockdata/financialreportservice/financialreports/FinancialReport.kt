package com.mystockdata.financialreportservice.financialreports

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "FinancialReport")
data class FinancialReport(
    @Id val id: String? = null,
    val endOfReportingPeriod: Date,
    val entityIdentifier: String,
    val entityIdentifierScheme: String,
    val factList: List<Fact>
)