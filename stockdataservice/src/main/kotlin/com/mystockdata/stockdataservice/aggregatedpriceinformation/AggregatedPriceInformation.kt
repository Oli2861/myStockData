package com.mystockdata.stockdataservice.aggregatedpriceinformation

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import java.math.BigDecimal
import java.time.Instant

@Measurement(name = "AggregatedPriceInformation")
data class AggregatedPriceInformation(
    @Column(timestamp = true) val time: Instant,
    @Column(tag = true) val symbol: String,
    @Column val open: BigDecimal,
    @Column val high: BigDecimal,
    @Column val low: BigDecimal,
    @Column val close: BigDecimal,
    @Column val adjClose: BigDecimal,
    @Column val volume: Int
)

enum class AggregatedPriceInformationFieldNames(val fieldName: String){
    OPEN("open"),
    HIGH("high"),
    LOW("low"),
    CLOSE("close"),
    ADJ_CLOSE("adjClose"),
    VOLUME("volume")
}

data class AggregatedPriceInformationResponse(
    val time: Instant,
    val symbol: String,
    var open: BigDecimal? = null,
    var high: BigDecimal? = null,
    var low: BigDecimal? = null,
    var close: BigDecimal? = null,
    var adjClose: BigDecimal? = null,
    var volume: Int? = null
)