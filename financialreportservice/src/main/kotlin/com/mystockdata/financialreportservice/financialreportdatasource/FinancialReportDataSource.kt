package com.mystockdata.financialreportservice.financialreportdatasource

interface FinancialReportDataSource {
    /**
     * Parses a financial report into a list of fact-items.
     * @param path Path to the financial report to be read. (Local path or from Web (URL))
     * @return List containing the retrieved items.
     */
    suspend fun retrieveFacts(path: String): List<Item>?

}