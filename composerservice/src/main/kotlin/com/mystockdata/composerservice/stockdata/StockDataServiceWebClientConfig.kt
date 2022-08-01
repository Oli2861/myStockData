package com.mystockdata.composerservice.stockdata

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class StockDataServiceWebClientConfig(
    @Value("\${stockdataservice.host}") val url: String,
    @Value("\${stockdataservice.port}") val port: Int
) {
    @Bean
    fun stockDataServiceWebClient(): WebClient = WebClient.builder()
        .baseUrl("http://$url:$port/")
        .build()
}