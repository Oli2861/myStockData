package com.mystockdata.composerservice.stockdata

import com.mystockdata.composerservice.csv.CSVEntryConstants
import com.mystockdata.composerservice.csv.PriceEntry
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
 * @return list of the produced csv entries.
 */
fun List<AggregatedPriceInformationResponse>.toCSVEntryList(): List<PriceEntry> {
    val entries: MutableList<PriceEntry> = mutableListOf()
    forEach {
        if (it.open != null) entries.add(PriceEntry(it.time, "${CSVEntryConstants.OPEN_COLUMN_NAME_PREFIX}${it.symbol}", it.open, it.symbol))
        if (it.high != null) entries.add(PriceEntry(it.time, "${CSVEntryConstants.HIGH_COLUMN_NAME_PREFIX}${it.symbol}", it.high, it.symbol))
        if (it.low != null) entries.add(PriceEntry(it.time, "${CSVEntryConstants.LOW_COLUMN_NAME_PREFIX}${it.symbol}", it.low, it.symbol))
        if (it.close != null) entries.add(PriceEntry(it.time, "${CSVEntryConstants.CLOSE_COLUMN_NAME_PREFIX}${it.symbol}", it.close, it.symbol))
        if (it.adjClose != null) entries.add(PriceEntry(it.time, "${CSVEntryConstants.ADJ_CLOSE_COLUMN_NAME_PREFIX}${it.symbol}", it.adjClose, it.symbol))
        if (it.volume != null) entries.add(PriceEntry(it.time, "${CSVEntryConstants.VOLUME_COLUMN_NAME_PREFIX}${it.symbol}", it.volume?.toBigDecimal(), it.symbol))
    }
    return entries
}


/**
 * Map aggregatedPriceInformationResponses to a corresponding company.
 * @return companies and aggregatedPriceInformationResponses of the associated symbols.
 */
fun List<AggregatedPriceInformationResponse>.matchToCompany(
    companies: Set<Company>
): Map<Company, List<AggregatedPriceInformationResponse>> {
    val map = mutableMapOf<Company, MutableList<AggregatedPriceInformationResponse>>()
    forEach { priceInfo ->
        companies.forEach { company ->
            if (company.isAssociatedWithSymbol(priceInfo.symbol)) {
                if (map[company].isNullOrEmpty()) {
                    map[company] = mutableListOf()
                    map[company]?.add(priceInfo)
                } else {
                    map[company]?.add(priceInfo)
                }
            }
        }
    }
    return map
}
