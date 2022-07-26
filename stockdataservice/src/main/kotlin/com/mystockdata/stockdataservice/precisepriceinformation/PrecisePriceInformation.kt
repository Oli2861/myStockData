package com.mystockdata.stockdataservice.precisepriceinformation

import com.influxdb.annotations.Column
import com.influxdb.annotations.Measurement
import java.math.BigDecimal
import java.time.Instant

@Measurement(name = "PrecisePriceInformation")
data class PrecisePriceInformation(
    @Column(timestamp = true) val time: Instant,
    @Column(name="symbol", tag = true) val symbol: String,
    @Column(name="exchange", tag = true) val exchange: String,
    @Column val price: BigDecimal
)