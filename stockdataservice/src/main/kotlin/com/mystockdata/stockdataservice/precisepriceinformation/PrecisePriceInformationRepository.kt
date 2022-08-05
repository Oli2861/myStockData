package com.mystockdata.stockdataservice.precisepriceinformation

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.mystockdata.stockdataservice.utility.chunks
import com.mystockdata.stockdataservice.utility.createFilter
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

    /**
     * Writes a flow of precise price information to the database.
     * @param precisePriceInformation Flow of PrecisePriceInformation to be written.
     */
    suspend fun writePrecisePriceInformation(precisePriceInformation: Flow<PrecisePriceInformation>) {
        influxDBClientKotlin().use { client ->
            val writeApi = client.getWriteKotlinApi()

            precisePriceInformation.chunks()
                .collect { precisePriceInformation ->
                    logger.trace("Writing $precisePriceInformation")
                    writeApi.writeMeasurements(precisePriceInformation, WritePrecision.NS)
                }

        }
    }

    /**
     * Retrieves precise price information from Influxdb.
     * @param symbols Symbols of interest.
     * @param start Start of the time window.
     * @param stop End of the time window.
     * @param withDayVolume Whether the dayVolume should be retrieved.
     * @return List of the retrieved PrecisePriceInformationResponse.
     */
    suspend fun readPrecisePriceInformation(
        symbols: List<String>,
        start: Instant,
        stop: Instant = Instant.now(),
        withDayVolume: Boolean = false
    ): List<PrecisePriceInformationResponse> {
        if (symbols.isEmpty()) return listOf()

        val fluxQuery = "from(bucket: \"$bucket\") " +
                "|> range(start: $start, stop: $stop) " +
                createFilter("_measurement", listOf("PrecisePriceInformation")) +
                createFilter("symbol", symbols) +
                createFilter("_field", listOf("price")) +
                // Last value in every 5 min step, creates empty entries if there is no value
                "|> aggregateWindow(every: 5m, fn: last, createEmpty: true)" +
                "|> yield()"
        logger.trace("fluxQuery: $fluxQuery")

        influxDBClientKotlin().use { client ->
            val queryApi = client.getQueryKotlinApi()
            val result =
                queryApi.query(fluxQuery, InfluxPrecisePriceInformationResponse::class.java).consumeAsFlow().toList()
            return result.mapNotNull { influxPriceInformationResponse -> influxPriceInformationResponse.toPrecisePriceInformationOrNull() }
        }

    }

    /**
     * Only used to map into PriceInformationResponse.
     */
    data class InfluxPrecisePriceInformationResponse(
        val time: Instant? = null,
        val symbol: String? = null,
        val exchange: String? = null,
        val marketHours: String? = null,
        val field: String? = null,
        val value: BigDecimal? = null
    ) {
        fun toPrecisePriceInformationOrNull(): PrecisePriceInformationResponse? {
            return if (time != null && symbol != null && exchange != null && marketHours != null && field != null && value != null && field == "price") {
                PrecisePriceInformationResponse(time, symbol, exchange, marketHours, value)
            } else {
                null
            }
        }
    }
}