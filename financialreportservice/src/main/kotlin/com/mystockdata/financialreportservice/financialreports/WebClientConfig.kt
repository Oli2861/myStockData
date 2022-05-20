package com.mystockdata.financialreportservice.financialreports

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.xml.Jaxb2XmlDecoder
import org.springframework.http.codec.xml.Jaxb2XmlEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    fun webClient(): WebClient? {
        // Byte * 1024 --> KB * 10245 --> MB
        val bufferSize = 16 * 1024 * 1024
        return WebClient
            .builder()
            .exchangeStrategies(ExchangeStrategies.builder().codecs { configurer: ClientCodecConfigurer ->
                configurer.defaultCodecs().jaxb2Encoder(Jaxb2XmlEncoder())
                configurer.defaultCodecs().jaxb2Decoder(Jaxb2XmlDecoder())
                configurer.defaultCodecs().maxInMemorySize(bufferSize)
            }.build())
            .build()
    }
}