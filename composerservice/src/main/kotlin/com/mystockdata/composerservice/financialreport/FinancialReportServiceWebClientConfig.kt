package com.mystockdata.composerservice.financialreport

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class FinancialReportServiceWebClientConfig(
    @Value("\${financialreportservice.host}") val url: String,
    @Value("\${financialreportservice.port}") val port: Int
) {
    @Bean
    fun financialReportServiceWebClient(): WebClient = WebClient.builder()
        .baseUrl("http://$url:$port/")
        .build()
}