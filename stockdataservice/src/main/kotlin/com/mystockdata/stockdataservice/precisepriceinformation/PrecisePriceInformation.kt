package com.mystockdata.stockdataservice.precisepriceinformation

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import java.math.BigDecimal
import java.time.Instant

@Measurement(name = "PrecisePriceInformation")
data class PrecisePriceInformation(
    @Column(tag = true) val symbol: String,
    @Column val price: BigDecimal,
    @Column(timestamp = true) val time: Instant
)