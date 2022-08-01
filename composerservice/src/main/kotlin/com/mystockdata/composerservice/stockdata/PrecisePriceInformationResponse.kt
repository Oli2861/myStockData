package com.mystockdata.composerservice.stockdata

import java.math.BigDecimal
import java.time.Instant

data class PrecisePriceInformationResponse(
    val time: Instant,
    val symbol: String,
    val exchange: String,
    val marketHours: String,
    val price: BigDecimal?
)