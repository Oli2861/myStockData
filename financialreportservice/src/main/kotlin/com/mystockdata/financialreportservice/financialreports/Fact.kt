package com.mystockdata.financialreportservice.financialreports

import java.math.BigDecimal
import java.util.*

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