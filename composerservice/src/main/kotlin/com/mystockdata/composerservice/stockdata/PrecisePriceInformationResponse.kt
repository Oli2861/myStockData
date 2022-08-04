package com.mystockdata.composerservice.stockdata

import com.mystockdata.composerservice.csv.PriceEntry
import java.math.BigDecimal
import java.time.Instant

data class PrecisePriceInformationResponse(
    val time: Instant,
    val symbol: String,
    val exchange: String,
    val marketHours: String,
    val price: BigDecimal?
){
    fun toCSVEntry(): PriceEntry {
        return PriceEntry(time, symbol, price, symbol)
    }
}

/**
 * Produces a List<CsvEntry> for a provided list of PriceInformationResponse.
 * @return list of the produced csv entries.
 */
fun List<PrecisePriceInformationResponse>.toCSVEntryList(): List<PriceEntry> = this.map { it.toCSVEntry() }