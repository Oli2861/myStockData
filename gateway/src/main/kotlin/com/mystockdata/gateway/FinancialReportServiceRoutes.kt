package com.mystockdata.gateway

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RestController

@RestController
@Configuration
class FinancialReportServiceRoutes(
    @Value("\${financialreportservice.host}") val financialReportServiceHost: String,
    @Value("\${financialreportservice.port}") val financialReportServicePort: Int
) {
    private val financialReportServiceUri = "http://$financialReportServiceHost:$financialReportServicePort/"

    @Bean
    fun financialReportRoutes(routeLocatorBuilder: RouteLocatorBuilder): RouteLocator {
        return routeLocatorBuilder.routes()
            .route("financialReportService_financialReport_retrieve") { predicateSpec ->
                predicateSpec.path("/v1/financialreport/retrieveReports")
                    .uri(financialReportServiceUri)
            }
            .route("financialReportService_financialReport_get"){
                    predicateSpec ->
                predicateSpec.path("/v1/financialreport")
                    .uri(financialReportServiceUri)
            }
            .build()
    }
}