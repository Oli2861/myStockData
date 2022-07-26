package com.mystockdata.stockdataservice.persistence

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.toList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Spring properties externalized in docker-compose environment properties:
 * - organisation: InfluxDB Organisation name
 * - bucket: InfluxDB main bucket name
 * - host: Url of the local InfluxDB instance
 * - token: Authentication token
 */
@Component
class AggregatedPriceInformationRepository(
    @Value("\${influx2.org}") private val org: String,
    @Value("\${influx2.bucket}") private val bucket: String,
    @Value("\${influx2.host}") private val host: String,
    @Value("\${influx2.token}") private val token: String
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AggregatedPriceInformationRepository::class.java)
    }

    fun influxDBClientKotlin(): InfluxDBClientKotlin =
        InfluxDBClientKotlinFactory.create(host, token.toCharArray(), org, bucket)

    suspend fun writeAggregatedPriceInformation(aggregatedPriceInformation: Flow<AggregatedPriceInformation>) {
        influxDBClientKotlin().use { client ->
            val writeApi = client.getWriteKotlinApi()

            aggregatedPriceInformation.collect { stockDataOHLCV ->
                logger.debug("Writing $stockDataOHLCV")
                writeApi.writeMeasurement(stockDataOHLCV, WritePrecision.S)
            }

        }
    }

    suspend fun readAggregatedPriceInformation(symbols: List<String>): List<AggregatedPriceInformation> {
        influxDBClientKotlin().use { client ->
            val queryApi = client.getQueryKotlinApi()
            return queryApi.query("from(bucket: \"stockdata\")\n" +
                    "  |> filter(fn: (r) => r[\"_measurement\"] == \"AggregatedPriceInformation\")\n" +
                    "  |> yield(name: \"mean\")", AggregatedPriceInformation::class.java)
                .consumeAsFlow().toList()
        }
    }

}