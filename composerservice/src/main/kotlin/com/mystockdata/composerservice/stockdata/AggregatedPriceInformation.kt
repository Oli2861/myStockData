package com.mystockdata.composerservice.stockdata

import java.math.BigDecimal
import java.time.Instant

data class AggregatedPriceInformation(
    val time: Instant,
    val symbol: String,
    var open: BigDecimal? = null,
    var high: BigDecimal? = null,
    var low: BigDecimal? = null,
    var close: BigDecimal? = null,
    var adjClose: BigDecimal? = null,
    var volume: Int? = null
)