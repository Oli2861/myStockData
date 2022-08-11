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
class StockDataServiceRoutes(
    @Value("\${stockdataservice.host}") val stockDataServiceHost: String,
    @Value("\${stockdataservice.port}") val stockDataServicePort: Int
) {
    private val stockdataServiceUri = "http://$stockDataServiceHost:$stockDataServicePort/"

    @Bean
    fun stockDataRoutes(routeLocatorBuilder: RouteLocatorBuilder): RouteLocator {
        return routeLocatorBuilder.routes()
            .route("stockDataService_aggregatedPriceInformation_retrieve") { predicateSpec ->
                predicateSpec.path("/v1/stockdata/aggregatedPriceInformation/retrieve")
                    .filters { rewriteStockDataServiceRoute(it) }
                    .uri(stockdataServiceUri)
            }
            .route("stockDataService_aggregatedPriceInformation_get"){
                    predicateSpec ->
                predicateSpec.path("/v1/stockdata/aggregatedPriceInformation")
                    .filters { rewriteStockDataServiceRoute(it) }
                    .uri(stockdataServiceUri)
            }
            .route("stockDataService_precisePriceInformation_start"){ predicateSpec ->
                predicateSpec.path("/v1/stockdata/precisePriceInformation/start")
                    .filters { rewriteStockDataServiceRoute(it) }
                    .uri(stockdataServiceUri)
            }
            .route("stockDataService_precisePriceInformation_stop"){ predicateSpec ->
                predicateSpec.path("/v1/stockdata/precisePriceInformation/stop")
                    .filters { rewriteStockDataServiceRoute(it) }
                    .uri(stockdataServiceUri)
            }
            .route("stockDataService_precisePriceInformation_get"){ predicateSpec ->
                predicateSpec.path("/v1/stockdata/precisePriceInformation")
                    .filters { rewriteStockDataServiceRoute(it) }
                    .uri(stockdataServiceUri)
            }
            .route("stockDataService_company_get"){ predicateSpec ->
                predicateSpec.path("/v1/stockdata/company")
                    .filters { rewriteStockDataServiceRoute(it) }
                    .uri(stockdataServiceUri)
            }
            .route("stockDataService_company_get"){ predicateSpec ->
                predicateSpec.path("/v1/stockdata/watchlist")
                    .filters { rewriteStockDataServiceRoute(it) }
                    .uri(stockdataServiceUri)
            }
            .build()
    }

    private fun rewriteStockDataServiceRoute(gatewayFilterSpec: GatewayFilterSpec): GatewayFilterSpec {
        return  gatewayFilterSpec.filter{ exchange, chain ->
            val request = exchange.request
            val path = request.uri.path
            val newPath = path.replace("stockdata/", "")
            val newRequest = request.mutate().path(newPath).build()
            chain.filter(exchange.mutate().request(newRequest).build())
        }
    }
}