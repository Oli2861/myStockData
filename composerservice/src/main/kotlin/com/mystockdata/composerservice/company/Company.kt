package com.mystockdata.composerservice.company

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Company")
data class Company(
    @Id val lei: String,
    val securities: Set<Security>,
) {
    fun getSymbolNames(): Set<String> {
        return securities.map { security ->
            security.symbols.map { symbol ->
                symbol.symbol
            }
        }.flatten().toSet()
    }

    fun containsSymbol(symbol: String): Boolean {
        return getSymbolNames().contains(symbol)
    }
}

data class Security(
    val isin: String,
    val symbols: Set<Symbol>
)

data class Symbol(
    val symbol: String,
    val exchange: String,
)

fun Set<Company>.findCompanyBySymbol(symbol: String): Company? {
    return this.find { it.containsSymbol(symbol) }
}

fun Set<Company>.getSymbols(): Set<String> = map { it.getSymbolNames() }.flatten().toSet()