package com.mystockdata.composerservice.financialreport

import com.mystockdata.composerservice.financialreport.FinancialReportServiceConstants.LEI_QUERY_PARAM
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

object FinancialReportServiceConstants {
    const val LEI_QUERY_PARAM = "lei"
}

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
                uriBuilder.path("v1/financialreport")
                    .queryParam("lei", leis)
                    .queryParam("start", dateFormat.format(startDate))
                    .queryParam("end", dateFormat.format(endDate))
                    .build()
            }.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType(MediaType.APPLICATION_JSON))
            .retrieve()
            .bodyToFlow()
    }

    /**
     * Tells the financialreportservice to retrieve financial reports from a remote data source.
     * @param leis List of the leis the financial reports have to match in order to get retrieved. If the list is empty all available financial reports will be retrieved.
     * @return flow emitting the retrieved reports.
     */
    suspend fun retrieveReports(leis: List<String> = listOf()): Flow<FinancialReport> {
        return financialReportServiceWebClient.get()
            .uri { uriBuilder ->
                if (leis.isEmpty()) {
                    uriBuilder.path("v1/financialreport/retrieveReports")
                        .build()
                } else {
                    uriBuilder.path("v1/financialreport/retrieveReports")
                        .queryParam(LEI_QUERY_PARAM, leis)
                        .build()
                }
            }.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType(MediaType.APPLICATION_JSON))
            .retrieve()
            .bodyToFlow()
    }

}