package com.mystockdata.financialreportservice.arelle

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.xml.Jaxb2XmlDecoder
import org.springframework.http.codec.xml.Jaxb2XmlEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ArelleWebClientConfig(
    @Value("\${arelle.url}") val url: String,
    @Value("\${arelle.port}") val port: Int,
) {
    @Bean
    fun arelleWebClient(): WebClient {
        // Byte * 1024 --> KB * 1024 --> MB
        val bufferSize = 16 * 1024 * 1024
        return WebClient
            .builder()
            .exchangeStrategies(ExchangeStrategies.builder().codecs { configurer: ClientCodecConfigurer ->
                configurer.defaultCodecs().jaxb2Encoder(Jaxb2XmlEncoder())
                configurer.defaultCodecs().jaxb2Decoder(Jaxb2XmlDecoder())
                configurer.defaultCodecs().maxInMemorySize(bufferSize)
            }.build())
            .baseUrl("http://$url:$port/")
            .build()
    }
}