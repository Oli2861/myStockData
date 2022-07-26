package com.mystockdata.stockdataservice.persistence

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlin
import com.mystockdata.stockdataservice.precisepriceinformation.PrecisePriceInformation
import com.mystockdata.stockdataservice.utility.chunks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class PrecisePriceInformationRepository(
    @Autowired val influxDBClientKotlin: InfluxDBClientKotlin,
    @Value("\${influx.bucket}") private val bucket: String
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PrecisePriceInformationRepository::class.java)
    }

    suspend fun writePrecisePriceInformation(precisePriceInformation: PrecisePriceInformation){
        logger.debug("Methode aufgerufen")
        influxDBClientKotlin.use { client ->
            val writeApi = client.getWriteKotlinApi()
            logger.debug("Writing $precisePriceInformation")
            writeApi.writeMeasurement(precisePriceInformation, WritePrecision.NS)
        }
    }

    suspend fun writePrecisePriceInformation(precisePriceInformation: Flow<PrecisePriceInformation>) {

        influxDBClientKotlin.use { client ->
            val writeApi = client.getWriteKotlinApi()

            precisePriceInformation.collect { precisePriceInformation ->
                logger.debug("Writing $precisePriceInformation")
                writeApi.writeMeasurement(precisePriceInformation, WritePrecision.NS)
            }

        }
    }

    suspend fun readPrecisePriceInformation(symbol: String, start: Instant, stop: Instant = Instant.now()): Flow<PrecisePriceInformation> {
        influxDBClientKotlin.use { client ->
            val queryApi = client.getQueryKotlinApi()

            return queryApi.query("from(bucket: $bucket) |> range(start: $start, stop: $stop)", PrecisePriceInformation::class.java)
                .consumeAsFlow()
        }
    }

}