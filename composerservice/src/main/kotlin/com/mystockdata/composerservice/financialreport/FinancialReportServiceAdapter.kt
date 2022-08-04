package com.mystockdata.composerservice.financialreport

import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@Component
class FinancialReportServiceAdapter(
    @Qualifier("financialReportServiceWebClient") @Autowired private val financialReportServiceWebClient: WebClient
) {


    /**
     * Retrieve financial reports from financial report service.
     * @param leis legal entity identifiers.
     * @param startDate Considering only reports older than this date.
     * @param endDate Considering only reports younger than this date.
     * @return flow of retrieved reports.
     */
    suspend fun getFinancialReports(leis: List<String>, startDate: Instant, endDate: Instant): Flow<FinancialReport> {
        return getFinancialReports(leis, Date.from(startDate), Date.from(endDate))
    }
    /**
     * Retrieve financial reports from financial report service.
     * @param leis legal entity identifiers.
     * @param startDate Considering only reports older than this date.
     * @param endDate Considering only reports younger than this date.
     * @return flow of retrieved reports.
     */
    private suspend fun getFinancialReports(leis: List<String>, startDate: Date, endDate: Date): Flow<FinancialReport> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return financialReportServiceWebClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("v1/financialreports")
                    .queryParam("lei", leis)
                    .queryParam("start", dateFormat.format(startDate))
                    .queryParam("end", dateFormat.format(endDate))
                    .build()
            }.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType(MediaType.APPLICATION_JSON))
            .retrieve()
            .bodyToFlow()
    }

    suspend fun loadReports(leis: List<String>): Flow<FinancialReport> {
        return financialReportServiceWebClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("v1/financialreports/loadReports")
                    .build()
            }.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType(MediaType.APPLICATION_JSON))
            .retrieve()
            .bodyToFlow()
    }

}