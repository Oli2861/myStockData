package com.mystockdata.composerservice.stockdata

import com.mystockdata.composerservice.company.Company
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
 * Map aggregatedPriceInformationResponses to a corresponding company.
 * @return companies and aggregatedPriceInformationResponses of the associated symbols.
 */
fun List<AggregatedPriceInformationResponse>.matchToCompany(
    companies: Set<Company>
): Map<Company, List<AggregatedPriceInformationResponse>> {
    val map = mutableMapOf<Company, MutableList<AggregatedPriceInformationResponse>>()
    forEach { priceInfo ->
        companies.forEach { company ->
            if (company.containsSymbol(priceInfo.symbol)) {
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
