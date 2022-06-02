package com.mystockdata.financialreportservice.financialreportevent

data class FinancialReportEvent(val correlationId: String, val financialReportEventType: FinancialReportEventType)
enum class FinancialReportEventType{
    REFRESH_DATA, LOAD_TAXONOMY
}