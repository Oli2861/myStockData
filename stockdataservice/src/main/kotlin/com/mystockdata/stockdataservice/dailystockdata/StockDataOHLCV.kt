package com.mystockdata.stockdataservice.dailystockdata

import java.math.BigDecimal

data class StockDataOHLCV(
    val isin: String,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val closePreviousDay: BigDecimal,
    val volume: Int
)

/**
 * @return StockDataOHLCV if none of the params is null; otherwise will return null.
 */
fun toStockDataOHLCV(
    isin: String,
    open: BigDecimal?,
    high: BigDecimal?,
    low: BigDecimal?,
    closePreviousDay: BigDecimal?,
    volume: Int?
): StockDataOHLCV? {
    return if (open != null && high != null && low != null && closePreviousDay != null && volume != null) {
        StockDataOHLCV(isin, open, high, low, closePreviousDay, volume)
    } else {
        null
    }
}

