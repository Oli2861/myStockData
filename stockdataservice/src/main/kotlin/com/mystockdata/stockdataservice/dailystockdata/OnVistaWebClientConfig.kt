package com.mystockdata.stockdataservice.dailystockdata

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient


@Configuration
class OnVistaWebClientConfig {
    val bufferSize = 16 * 1024 * 1024

    @Bean
    fun onVistaWebClient(): WebClient = WebClient.builder()
        .exchangeStrategies(
            ExchangeStrategies.builder()
                .codecs { codecs -> codecs.defaultCodecs().maxInMemorySize(bufferSize) }
                .build()
        )
        .clientConnector(
            ReactorClientHttpConnector(
                HttpClient.create()
                    .followRedirect(true)
            )
        )
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XHTML_XML_VALUE)
        .baseUrl("https://www.onvista.de/aktien")
        .build()

}