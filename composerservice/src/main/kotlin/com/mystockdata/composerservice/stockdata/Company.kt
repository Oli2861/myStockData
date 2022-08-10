package com.mystockdata.composerservice.stockdata

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * A company is identified by a Legal Entity Identifier and can be associated with multiple securities.
 * @param lei Unique Legal Entity Identifier.
 * @param securities A Set of all associated securities.
 */
data class Company(
    val lei: String,
    val securities: Set<Security>,
) {

    @JsonIgnore
    fun getAssociatedSymbols(): Set<Symbol>{
        return securities.map { security ->
            security.symbols
        }.flatten().toSet()
    }

    @JsonIgnore
    fun getAssociatedSymbolNames(): Set<String> {
        return getAssociatedSymbols().map { symbol ->
            symbol.symbol
        }.toSet()
    }

    @JsonIgnore
    fun isAssociatedWithSymbol(symbol: String): Boolean {
        return getAssociatedSymbolNames().contains(symbol)
    }
}

/**
 * Since securities can be listed on multiple exchanges they can be identified through different symbols.
 * @param isin International Security Identifier; identifies a symbol uniquely.
 * @param symbols List of all symbols associated with the security.
 */
data class Security(
    val isin: String,
    val symbols: Set<Symbol>
)

/**
 * @param symbol String containing the actual symbol.
 * @param system Identifies the system the symbol is valid on i.e. Yahoo Finance or Bloomberg.
 * @param exchange Name of the exchange the security ist listed on.
 */
data class Symbol(
    val symbol: String,
    val system: String,
    val exchange: String
)

fun Set<Company>.findCompanyBySymbol(symbol: String): Company? {
    return this.find { it.isAssociatedWithSymbol(symbol) }
}

fun Set<Company>.getAssociatedSymbols(): Set<String> = map { it.getAssociatedSymbolNames() }.flatten().toSet()