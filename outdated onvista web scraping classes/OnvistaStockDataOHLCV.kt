package com.mystockdata.stockdataservice.dailystockdata

import java.math.BigDecimal
import java.util.Date

data class OnvistaStockDataOHLCV(
    val isin: String,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val closePreviousDay: BigDecimal,
    val volume: Int,
    val date: Date = Date()
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
): OnvistaStockDataOHLCV? {
    return if (open != null && high != null && low != null && closePreviousDay != null && volume != null) {
        OnvistaStockDataOHLCV(isin, open, high, low, closePreviousDay, volume)
    } else {
        null
    }
}
