package com.mystockdata.schedulingservice.watchlist

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Company")
data class Company(
     @Id val lei: String,
    val securities: List<Security>,
)

data class Security(
    val isin: String,
    val companyId: String,
    val symbols: List<Symbol>
)

data class Symbol(
    val symbol: String,
    val exchange: String,
    val isin: String
)