package com.mystockdata.schedulingservice.financialreportevent

data class FinancialReportEvent(
    val correlationId: String,
    val financialReportEventType: FinancialReportEventType
)

enum class FinancialReportEventType {
    REFRESH_DATA
}