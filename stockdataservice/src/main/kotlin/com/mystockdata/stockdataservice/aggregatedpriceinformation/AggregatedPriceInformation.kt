package com.mystockdata.stockdataservice.aggregatedpriceinformation

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import java.math.BigDecimal
import java.time.Instant

@Measurement(name = "AggregatedPriceInformation")
data class AggregatedPriceInformation(
    @Column(timestamp = true) val date: Instant,
    @Column(tag = true) val symbol: String,
    @Column val open: BigDecimal,
    @Column val high: BigDecimal,
    @Column val low: BigDecimal,
    @Column val close: BigDecimal,
    @Column val adjClose: BigDecimal,
    @Column val volume: Int
)
