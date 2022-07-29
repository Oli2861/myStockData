package com.mystockdata.financialreportservice.financialreportinformation

import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow

@Component
class XBRLFilingsAdapter: ReportInfoDataSource {
    private val webClient: WebClient = WebClient.create("https://filings.xbrl.org/")

    /**
     * Retrieve a list of all available financial reports from https://filings.xbrl.org/ (includes all entries of all pages of the table)
     * @return Flow emitting the retrieved information about the financial reports.
     */
    override fun getAvailableFinancialReports(): Flow<RetrievedReportInfo> {
        return webClient.get()
            .uri("table-index.json")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType(MediaType.APPLICATION_JSON))
            .retrieve()
            .bodyToFlow<RetrievedReportInfo>()
    }
}