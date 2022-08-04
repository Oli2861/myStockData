package com.mystockdata.composerservice.financialreport

import com.mystockdata.financialreportservice.financialreports.Fact
import java.util.*

data class FinancialReport(
    val id: String? = null,
    val endOfReportingPeriod: Date,
    val entityIdentifier: String,
    val entityIdentifierScheme: String,
    val factList: List<Fact>
) {
    fun findFacts(ifrsTag: String): List<Fact> =
        factList.filter { fact -> fact.ifrsTag == ifrsTag }
}

/**
 * Maps desired facts (which match with the provided ifrs-tag) to entity identifiers of the companies they belong to.
 * @param ifrsTag the tag the facts have to match.
 * @return map containing legal entity identifiers and a list of all associated facts matching the provided ifrs-tag.
 */
fun List<FinancialReport>.toFactMap(ifrsTag: String): Map<String, Set<Fact>>{
    val identifierFactMap = mutableMapOf<String, MutableSet<Fact>>()
    forEach { financialReport ->
        val facts = financialReport.findFacts(ifrsTag)
        val lei = financialReport.entityIdentifier
        if(identifierFactMap[lei] == null){
            identifierFactMap[lei] = mutableSetOf()
        }
        identifierFactMap[lei]?.addAll(facts)
    }
    return identifierFactMap
}