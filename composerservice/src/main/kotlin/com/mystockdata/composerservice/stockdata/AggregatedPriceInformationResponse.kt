package com.mystockdata.composerservice.stockdata

import com.mystockdata.composerservice.csv.CsvEntry
import java.math.BigDecimal
import java.time.Instant

data class AggregatedPriceInformationResponse(
    val time: Instant,
    val symbol: String,
    var open: BigDecimal? = null,
    var high: BigDecimal? = null,
    var low: BigDecimal? = null,
    var close: BigDecimal? = null,
    var adjClose: BigDecimal? = null,
    var volume: Int? = null
)
/**
 * Produces a List<CsvEntry> for a provided list of AggregatedPriceInformation.
 * @param data List of AggregatedPriceInformation. Should be down sampled in order to produce a useful CSV Body.
 * @return list of the produced csv entries.
 */
fun List<AggregatedPriceInformationResponse>.toCSVEntryList(): List<CsvEntry> {
    val entries: MutableList<CsvEntry> = mutableListOf()
    this.forEach {
        if (it.open != null) entries.add(CsvEntry(it.time, "open_${it.symbol}", it.open))
        if (it.high != null) entries.add(CsvEntry(it.time, "high_${it.symbol}", it.high))
        if (it.low != null) entries.add(CsvEntry(it.time, "low_${it.symbol}", it.low))
        if (it.close != null) entries.add(CsvEntry(it.time, "close_${it.symbol}", it.close))
        if (it.adjClose != null) entries.add(CsvEntry(it.time, "adjClose_${it.symbol}", it.adjClose))
        if (it.volume != null) entries.add(CsvEntry(it.time, "volume_${it.symbol}", it.volume?.toBigDecimal()))
    }
    return entries
}
