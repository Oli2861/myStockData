package com.mystockdata.gateway

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RestController

@RestController
@Configuration
class DataComposerServiceRoutes(
    @Value("\${composerservice.host}") val composerServiceHost: String,
    @Value("\${composerservice.port}") val composerServicePort: Int
) {
    private val dataComposerServiceUri = "http://$composerServiceHost:$composerServicePort/"

    @Bean
    fun composedDataRoutes(routeLocatorBuilder: RouteLocatorBuilder): RouteLocator {
        return routeLocatorBuilder.routes()
            .route("stockDataService_aggregatedPriceInformation_retrieve") { predicateSpec ->
                predicateSpec.path("/v1/composedData/aggregatedPriceInformation/csv")
                    .filters { rewriteDataComposerServiceRoute(it) }
                    .uri(dataComposerServiceUri)
            }
            .route("stockDataService_aggregatedPriceInformation_get"){
                    predicateSpec ->
                predicateSpec.path("/v1/composedData/precisePriceInformation/csv")
                    .filters { rewriteDataComposerServiceRoute(it) }
                    .uri(dataComposerServiceUri)
            }
            .build()
    }

    private fun rewriteDataComposerServiceRoute(gatewayFilterSpec: GatewayFilterSpec): GatewayFilterSpec {
        return  gatewayFilterSpec.filter{ exchange, chain ->
            val request = exchange.request
            val path = request.uri.path
            val newPath = path.replace("composedData/", "")
            val newRequest = request.mutate().path(newPath).build()
            chain.filter(exchange.mutate().request(newRequest).build())
        }
    }
}