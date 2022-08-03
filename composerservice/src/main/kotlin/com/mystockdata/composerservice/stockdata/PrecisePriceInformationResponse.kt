package com.mystockdata.composerservice.stockdata

import com.mystockdata.composerservice.csv.CsvEntry
import java.math.BigDecimal
import java.time.Instant

data class PrecisePriceInformationResponse(
    val time: Instant,
    val symbol: String,
    val exchange: String,
    val marketHours: String,
    val price: BigDecimal?
){
    fun toCSVEntry(): CsvEntry {
        return CsvEntry(time, symbol, price)
    }
}

/**
 * Produces a List<CsvEntry> for a provided list of PriceInformationResponse.
 * @param data List of PrecisePriceInformation. Should be down-sampled in order to produce a useful CSV Body.
 * @return list of the produced csv entries.
 */
fun List<PrecisePriceInformationResponse>.toCSVEntryList(): List<CsvEntry> = this.map { CsvEntry(it.time, it.symbol, it.price) }