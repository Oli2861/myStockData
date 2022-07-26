package com.mystockdata.stockdataservice.persistence

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.mystockdata.stockdataservice.aggregatedpriceinformation.AggregatedPriceInformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @property influxDBClientKotlin Client to communicate with the local InfluxDB Instance.
 * Spring properties externalized in docker-compose environment properties:
 * - organisation: InfluxDB Organisation name
 * - bucket: InfluxDB main bucket name
 * - host: Url of the local InfluxDB instance
 * - token: Authentication token
 */
@Component
class AggregatedPriceInformationRepository(
    @Autowired val influxDBClientKotlin: InfluxDBClientKotlin
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AggregatedPriceInformationRepository::class.java)
    }

    suspend fun writeAggregatedPriceInformation(aggregatedPriceInformation: Flow<AggregatedPriceInformation>) {

        influxDBClientKotlin.use { client ->
            val writeApi = client.getWriteKotlinApi()

            aggregatedPriceInformation.collect { stockDataOHLCV ->
                logger.debug("Writing $stockDataOHLCV")
                writeApi.writeMeasurement(stockDataOHLCV, WritePrecision.S)
            }

        }
    }

    suspend fun readAggregatedPriceInformation(): Flow<AggregatedPriceInformation> {
        influxDBClientKotlin.use { client ->
            val queryApi = client.getQueryKotlinApi()

            return queryApi.query("", AggregatedPriceInformation::class.java)
                .consumeAsFlow()
        }
    }

}