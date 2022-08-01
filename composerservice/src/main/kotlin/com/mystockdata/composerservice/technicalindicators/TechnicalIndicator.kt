package com.mystockdata.composerservice.technicalindicators

import com.mystockdata.composerservice.Indicator
import com.mystockdata.composerservice.IndicatorName
import java.math.BigDecimal
import java.time.Instant

data class TechnicalIndicator(
    override val time: Instant,
    override val symbol: String,
    override val indicatorName: IndicatorName,
    override val value: BigDecimal
) : Indicator()

