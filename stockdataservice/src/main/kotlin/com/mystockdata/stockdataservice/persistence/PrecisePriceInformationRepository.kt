package com.mystockdata.stockdataservice.persistence

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.mystockdata.stockdataservice.precisepriceinformation.*
import kotlinx.coroutines.flow.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant

@Component
class PrecisePriceInformationRepository(
    @Value("\${influx2.org}") private val org: String,
    @Value("\${influx2.bucket}") private val bucket: String,
    @Value("\${influx2.host}") private val host: String,
    @Value("\${influx2.token}") private val token: String
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PrecisePriceInformationRepository::class.java)
    }

    fun influxDBClientKotlin(): InfluxDBClientKotlin =
        InfluxDBClientKotlinFactory.create(host, token.toCharArray(), org, bucket)

    suspend fun writePrecisePriceInformation(precisePriceInformation: PrecisePriceInformation) {
        influxDBClientKotlin().use { client ->
            val writeApi = client.getWriteKotlinApi()
            logger.debug("Writing $precisePriceInformation")
            writeApi.writeMeasurement(precisePriceInformation, WritePrecision.NS)
        }
    }

    suspend fun writePrecisePriceInformation(precisePriceInformation: Flow<PrecisePriceInformation>) {
        influxDBClientKotlin().use { client ->
            val writeApi = client.getWriteKotlinApi()

            precisePriceInformation.collect { precisePriceInformation ->
                logger.debug("Writing $precisePriceInformation")
                writeApi.writeMeasurement(precisePriceInformation, WritePrecision.NS)
            }

        }
    }

    suspend fun readPrecisePriceInformation(
        symbol: String,
        start: Instant,
        stop: Instant = Instant.now()
    ): Flow<PrecisePriceInformation> {
        influxDBClientKotlin().use { client ->
            val queryApi = client.getQueryKotlinApi()

            return queryApi.query(
                "from(bucket: $bucket) |> range(start: $start, stop: $stop) |> filter(fn: (r) => r[\"_measurement\"] == \"PrecisePriceInformation\") |> filter(fn: (r) => r[\"symbol\"] == \"$symbol\") |> yield()",
                PrecisePriceInformation::class.java
            )
                .consumeAsFlow()
        }
    }

    /**
     *
     */
    suspend fun readPrecisePriceInformation(
        symbols: List<String>,
        start: Instant,
        stop: Instant = Instant.now(),
        withDayVolume: Boolean = false
    ): List<PriceInformationResponse> {
        if (symbols.isEmpty()) return listOf()

        val stringBuilder = StringBuilder()
        for ((index, symbol) in symbols.withIndex()) {
            stringBuilder.append("${if (index > 0) " or " else ""}r[\"symbol\"] == \"$symbol\"")
        }

        val symbolCondition = stringBuilder.toString()

        val fluxQuery = "from(bucket: \"$bucket\") " +
                "|> range(start: $start, stop: $stop) " +
                "|> filter(fn: (r) => r[\"_measurement\"] == \"PrecisePriceInformation\") " +
                "|> filter(fn: (r) => $symbolCondition) " +
                "|> filter(fn: (r) => r[\"_field\"] == \"price\")" +
                // Last value in every 5 min step, creates empty entries if there is no value
                "|> aggregateWindow(every: 5m, fn: last, createEmpty: true)" +
                "|> yield()"
        logger.debug("fluxQuery: $fluxQuery")

        influxDBClientKotlin().use { client ->
            val queryApi = client.getQueryKotlinApi()
            val result = queryApi.query(fluxQuery, InfluxPriceInformationResponse::class.java).consumeAsFlow().toList()
            return result.mapNotNull { influxPriceInformationResponse -> influxPriceInformationResponse.toPrecisePriceInformationOrNull() }
        }

    }

    /**
     * Only used to map into PriceInformationResponse.
     */
    data class InfluxPriceInformationResponse(
        val time: Instant? = null,
        val symbol: String? = null,
        val exchange: String? = null,
        val marketHours: String? = null,
        val field: String? = null,
        val value: BigDecimal? = null
    ) {
        fun toPrecisePriceInformationOrNull(): PriceInformationResponse? {
            return if (time != null && symbol != null && exchange != null && marketHours != null && field != null && value != null && field == "price") {
                PriceInformationResponse(time, symbol, exchange, marketHours, value)
            } else {
                null
            }
        }
    }
}

/*
 PrecisePriceInformation(
                    time = it.time,
                    symbol = it.values["symbol"],
                    exchange = it.values["exchange"],
                    marketHours = it.values["marketHours"],

                    dayVolume = it.field[]
                )
 */