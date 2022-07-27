package com.mystockdata.stockdataservice.persistence

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformation
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationFieldNames
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformationResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.toList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant

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

    /**
     * @param symbols Symbols of interest
     * @param start Start of the time window
     * @param stop End of the time window
     * @param openColumn Whether to retrieve opening prices
     * @param highColumn Whether to retrieve high prices
     * @param lowColumn Whether to retrieve low prices
     * @param closeColumn Whether to retrieve closing prices
     * @param adjCloseColumn Whether to retrieve adjusted close prices
     * @param volumeColumn Whether to retrieve volumes
     * @param aggregateWindow Size of the down sampling window
     * @return List of the retrieved information of type InfluxAggregatedPriceInformationResponse
     */
    suspend fun readAggregatedPriceInformation(
        symbols: List<String>,
        start: Instant,
        stop: Instant = Instant.now(),
        openColumn: Boolean = true,
        highColumn: Boolean = true,
        lowColumn: Boolean = true,
        closeColumn: Boolean = true,
        adjCloseColumn: Boolean = true,
        volumeColumn: Boolean = true,
        aggregateWindow: Int = 24
    ): List<AggregatedPriceInformationResponse> {
        if (symbols.isEmpty()) return listOf()

        // Prepare query
        val fields = mutableListOf<String>()
        if (openColumn) fields.add(AggregatedPriceInformationFieldNames.OPEN.fieldName)
        if (highColumn) fields.add(AggregatedPriceInformationFieldNames.HIGH.fieldName)
        if (lowColumn) fields.add(AggregatedPriceInformationFieldNames.LOW.fieldName)
        if (closeColumn) fields.add(AggregatedPriceInformationFieldNames.CLOSE.fieldName)
        if (adjCloseColumn) fields.add(AggregatedPriceInformationFieldNames.ADJ_CLOSE.fieldName)
        if (volumeColumn) fields.add(AggregatedPriceInformationFieldNames.VOLUME.fieldName)

        val fluxQuery = "from(bucket: \"$bucket\") " +
                "|> range(start: $start, stop: $stop) " +
                createFilter("_measurement", listOf("AggregatedPriceInformation")) +
                createFilter("_field", fields) +
                createFilter("symbol", symbols) +
                // Last value in every 5 min step, creates empty entries if there is no value
                "|> aggregateWindow(every: ${aggregateWindow}h, fn: last, createEmpty: true)" +
                "|> yield()"
        logger.debug("fluxQuery: $fluxQuery")
        // Execute query
        influxDBClientKotlin().use { client ->
            val queryApi = client.getQueryKotlinApi()
            val result =
                queryApi.query(fluxQuery, InfluxAggregatedPriceInformationResponse::class.java).consumeAsFlow().toList()
            return mapInfluxAggregatedPriceInformationResponseToAggregatedPriceInformationResponse(result)
        }

    }

    private fun mapInfluxAggregatedPriceInformationResponseToAggregatedPriceInformationResponse(list: List<InfluxAggregatedPriceInformationResponse>): List<AggregatedPriceInformationResponse> {
        val entries = list.mapNotNull { if(it.time != null && it.symbol != null)AggregatedPriceInformationResponse(it.time, it.symbol) else null}.distinct()
        entries.forEach { aggregatedPriceInformationResponse ->
            list.filter { (it.time == aggregatedPriceInformationResponse.time) && (it.symbol == aggregatedPriceInformationResponse.symbol) }.forEach {
                when (it.field) {
                    AggregatedPriceInformationFieldNames.OPEN.fieldName -> aggregatedPriceInformationResponse.open = it.value
                    AggregatedPriceInformationFieldNames.HIGH.fieldName -> aggregatedPriceInformationResponse.high = it.value
                    AggregatedPriceInformationFieldNames.LOW.fieldName -> aggregatedPriceInformationResponse.low = it.value
                    AggregatedPriceInformationFieldNames.CLOSE.fieldName -> aggregatedPriceInformationResponse.close = it.value
                    AggregatedPriceInformationFieldNames.ADJ_CLOSE.fieldName -> aggregatedPriceInformationResponse.adjClose = it.value
                    AggregatedPriceInformationFieldNames.VOLUME.fieldName -> aggregatedPriceInformationResponse.volume = it.value?.toInt()
                }
            }
        }
        return entries
    }

    data class InfluxAggregatedPriceInformationResponse(
        val time: Instant? = null,
        val symbol: String? = null,
        val field: String? = null,
        val value: BigDecimal? = null
    )
}