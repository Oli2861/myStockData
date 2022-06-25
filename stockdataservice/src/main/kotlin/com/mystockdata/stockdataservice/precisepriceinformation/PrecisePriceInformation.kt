package com.mystockdata.stockdataservice.precisepriceinformation

import java.math.BigDecimal

data class PrecisePriceInformation(val symbol: String, val price: BigDecimal, val time: Long)