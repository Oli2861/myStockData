package com.mystockdata.financialreportservice.financialreportinformation

import kotlinx.coroutines.flow.Flow

interface ReportInfoDataSource {
    /**
     * Retrieve a list of all available financial reports from the data source.
     * @return Flow emitting the retrieved information about the financial reports.
     */
    fun getAvailableFinancialReports(): Flow<RetrievedReportInfo>
}