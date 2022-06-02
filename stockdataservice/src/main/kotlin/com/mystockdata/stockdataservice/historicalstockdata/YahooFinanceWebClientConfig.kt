package com.mystockdata.stockdataservice.historicalstockdata

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class YahooFinanceWebClientConfig {
    val bufferSize = 16 * 1024 * 1024

    @Bean
    fun yahooFinanceWebClient(): WebClient = WebClient.builder()
        .clientConnector(
            ReactorClientHttpConnector(
                HttpClient.create()
                    .followRedirect(true)
            )
        )
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
        .baseUrl("https://query1.finance.yahoo.com")
        .build()
}