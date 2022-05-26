package com.mystockdata.schedulingservice.financialreportevent

data class FinancialReportEvent(val correlationId: String, val fuelStationEventType: FuelStationEventType)
enum class FuelStationEventType{
    REFRESH_DATA, LOAD_TAXONOMY
}