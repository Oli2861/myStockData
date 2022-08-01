package com.mystockdata.composerservice.financialreport

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class FinancialReportServiceAdapter(
    @Qualifier("financialReportServiceWebClient") @Autowired private val financialReportServiceWebClient: WebClient
) {

}