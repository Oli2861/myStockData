package com.mystockdata.financialreportservice.financialreports

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.math.BigDecimal
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes(
    value = [
        JsonSubTypes.Type(value = MonetaryFact::class),
        JsonSubTypes.Type(value = NumericFact::class),
        JsonSubTypes.Type(value = TextualFact::class)
    ]
)
abstract class Fact {
    abstract val ifrsTag: String
    abstract val start: Date?
    abstract val end: Date?
    abstract val value: Any
}

data class NumericFact(
    override val ifrsTag: String,
    override val start: Date?,
    override val end: Date?,
    override val value: BigDecimal
): Fact()

data class MonetaryFact(
    override val ifrsTag: String,
    override val start: Date?,
    override val end: Date?,
    override val value: BigDecimal,
    val currency: String?,
    val balance: String?
) : Fact()

data class TextualFact(
    override val ifrsTag: String,
    override val start: Date?,
    override val end: Date?,
    override val value: String
) : Fact()