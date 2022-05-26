package com.mystockdata.financialreportservice.financialreports

import com.mystockdata.financialreportservice.arelle.Item
import java.util.*

/**
 *  IFRS 2022 General information
 */
data class IFRSGeneralInformation(
    var ifrs_full_NameOfReportingEntityOrOtherMeansOfIdentification: String? = null,
    var ifrs_full_ExplanationOfChangeInNameOfReportingEntityOrOtherMeansOfIdentificationFromEndOfPrecedingReportingPeriod: String? = null,
    var ifrs_full_DescriptionOfNatureOfFinancialStatements: String? = null,
    var ifrs_full_DateOfEndOfReportingPeriod2013: Date? = null,
    var ifrs_full_PeriodCoveredByFinancialStatements: String? = null,
    var ifrs_full_DescriptionOfPresentationCurrency: String? = null,
    var ifrs_full_LevelOfRoundingUsedInFinancialStatements: String? = null
){
    fun setValue(item: Item)= keyLambdaMap[item.name]?.invoke(this, item)
}
private val keyLambdaMap = mapOf<String, (IFRSGeneralInformation, Item) -> Unit>(
    "ifrs-full:NameOfReportingEntityOrOtherMeansOfIdentification" to {statement, item -> statement.ifrs_full_NameOfReportingEntityOrOtherMeansOfIdentification = item.value},
    "ifrs-full:ExplanationOfChangeInNameOfReportingEntityOrOtherMeansOfIdentificationFromEndOfPrecedingReportingPeriod" to {statement, item -> statement.ifrs_full_ExplanationOfChangeInNameOfReportingEntityOrOtherMeansOfIdentificationFromEndOfPrecedingReportingPeriod = item.value},
    "ifrs-full:DescriptionOfNatureOfFinancialStatements" to {statement, item -> statement.ifrs_full_DescriptionOfNatureOfFinancialStatements = item.value},
    "ifrs-full:DateOfEndOfReportingPeriod2013" to {statement, item -> statement.ifrs_full_DateOfEndOfReportingPeriod2013 = item.valueDate},
    "ifrs-full:PeriodCoveredByFinancialStatements" to {statement, item -> statement.ifrs_full_PeriodCoveredByFinancialStatements = item.value},
    "ifrs-full:DescriptionOfPresentationCurrency" to {statement, item -> statement.ifrs_full_DescriptionOfPresentationCurrency = item.value},
    "ifrs-full:LevelOfRoundingUsedInFinancialStatements" to {statement, item -> statement.ifrs_full_LevelOfRoundingUsedInFinancialStatements = item.value},
)