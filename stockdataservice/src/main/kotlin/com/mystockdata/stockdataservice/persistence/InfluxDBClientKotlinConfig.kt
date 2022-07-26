package com.mystockdata.stockdataservice.persistence

import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InfluxDBClientKotlinConfig(
    @Value("\${influx.org}") private val org: String,
    @Value("\${influx.bucket}") private val bucket: String,
    @Value("\${influx.host}") private val host: String,
    @Value("\${influx.token}") private val token: String,
) {

    @Bean
    fun influxDBClientKotlin(): InfluxDBClientKotlin =
        InfluxDBClientKotlinFactory.create(host, token.toCharArray(), org, bucket)

}