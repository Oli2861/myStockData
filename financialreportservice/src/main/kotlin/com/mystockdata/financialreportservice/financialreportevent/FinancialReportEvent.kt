package com.mystockdata.financialreportservice.financialreportevent

data class FinancialReportEvent(val correlationId: String, val fuelStationEventType: FuelStationEventType)
enum class FuelStationEventType{
    REFRESH_DATA, LOAD_TAXONOMY
}