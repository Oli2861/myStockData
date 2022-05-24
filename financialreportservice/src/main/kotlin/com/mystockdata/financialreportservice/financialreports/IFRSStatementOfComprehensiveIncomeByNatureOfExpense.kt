package com.mystockdata.financialreportservice.financialreports

import com.mystockdata.financialreportservice.arelle.Item
import java.math.BigDecimal

data class IFRSStatementOfComprehensiveIncomeByNatureOfExpense(
    var ifrs_full_Revenue: BigDecimal? = null,
    var ifrs_full_InterestRevenueCalculatedUsingEffectiveInterestMethod: BigDecimal? = null,
    var ifrs_full_OtherIncome: BigDecimal? = null,
    var ifrs_full_ChangesInInventoriesOfFinishedGoodsAndWorkInProgress: BigDecimal? = null,
    var ifrs_full_OtherWorkPerformedByEntityAndCapitalised: BigDecimal? = null,
    var ifrs_full_RawMaterialsAndConsumablesUsed: BigDecimal? = null,
    var ifrs_full_EmployeeBenefitsExpense: BigDecimal? = null,
    var ifrs_full_DepreciationAndAmortisationExpense: BigDecimal? = null,
    var ifrs_full_ImpairmentLossReversalOfImpairmentLossRecognisedInProfitOrLoss: BigDecimal? = null,
    var ifrs_full_OtherExpenseByNature: BigDecimal? = null,
    var ifrs_full_OtherGainsLosses: BigDecimal? = null,
    var ifrs_full_ProfitLossFromOperatingActivities: BigDecimal? = null,
    var ifrs_full_DifferenceBetweenCarryingAmountOfDividendsPayableAndCarryingAmountOfNoncashAssetsDistributed: BigDecimal? = null,
    var ifrs_full_GainsLossesOnNetMonetaryPosition: BigDecimal? = null,
    var ifrs_full_GainLossArisingFromDerecognitionOfFinancialAssetsMeasuredAtAmortisedCost: BigDecimal? = null,
    var ifrs_full_FinanceIncome: BigDecimal? = null,
    var ifrs_full_FinanceCosts: BigDecimal? = null,
    var ifrs_full_ImpairmentLossImpairmentGainAndReversalOfImpairmentLossDeterminedInAccordanceWithIFRS9: BigDecimal? = null,
    var ifrs_full_ShareOfProfitLossOfAssociatesAndJointVenturesAccountedForUsingEquityMethod: BigDecimal? = null,
    var ifrs_full_OtherIncomeExpenseFromSubsidiariesJointlyControlledEntitiesAndAssociates: BigDecimal? = null,
    var ifrs_full_GainsLossesArisingFromDifferenceBetweenPreviousCarryingAmountAndFairValueOfFinancialAssetsReclassifiedAsMeasuredAtFairValue: BigDecimal? = null,
    var ifrs_full_CumulativeGainLossPreviouslyRecognisedInOtherComprehensiveIncomeArisingFromReclassificationOfFinancialAssetsOutOfFairValueThroughOtherComprehensiveIncomeIntoFairValueThroughProfitOrLossMeasurementCategory: BigDecimal? = null,
    var ifrs_full_HedgingGainsLossesForHedgeOfGroupOfItemsWithOffsettingRiskPositions: BigDecimal? = null,
    var ifrs_full_ProfitLossBeforeTax: BigDecimal? = null,
    var ifrs_full_IncomeTaxExpenseContinuingOperations: BigDecimal? = null,
    var ifrs_full_ProfitLossFromContinuingOperations: BigDecimal? = null,
    var ifrs_full_ProfitLossFromDiscontinuedOperations: BigDecimal? = null,
    var ifrs_full_ProfitLoss: BigDecimal? = null,
    var ifrs_full_ProfitLossAttributableToOwnersOfParent: BigDecimal? = null,
    var ifrs_full_ProfitLossAttributableToNoncontrollingInterests: BigDecimal? = null,
    var ifrs_full_EarningsPerShareExplanatory: String? = null,
    var ifrs_full_BasicEarningsLossPerShareFromContinuingOperations: BigDecimal? = null,
    var ifrs_full_BasicEarningsLossPerShareFromDiscontinuedOperations: BigDecimal? = null,
    var ifrs_full_BasicEarningsLossPerShare: BigDecimal? = null,
    var ifrs_full_DilutedEarningsLossPerShareFromContinuingOperations: BigDecimal? = null,
    var ifrs_full_DilutedEarningsLossPerShareFromDiscontinuedOperations: BigDecimal? = null,
    var ifrs_full_DilutedEarningsLossPerShare: BigDecimal? = null
) {
    fun setValue(item: Item) {
        if (item.name.isNullOrEmpty()) return
        when (item.name) {
            "ifrs-full:Revenue" -> ifrs_full_Revenue = item.valueNumeric
            "ifrs-full:InterestRevenueCalculatedUsingEffectiveInterestMethod" -> ifrs_full_InterestRevenueCalculatedUsingEffectiveInterestMethod = item.valueNumeric
            "ifrs-full:OtherIncome" -> ifrs_full_OtherIncome = item.valueNumeric
            "ifrs-full:ChangesInInventoriesOfFinishedGoodsAndWorkInProgress" -> ifrs_full_ChangesInInventoriesOfFinishedGoodsAndWorkInProgress = item.valueNumeric
            "ifrs-full:OtherWorkPerformedByEntityAndCapitalised" -> ifrs_full_OtherWorkPerformedByEntityAndCapitalised = item.valueNumeric
            "ifrs-full:RawMaterialsAndConsumablesUsed" -> ifrs_full_RawMaterialsAndConsumablesUsed = item.valueNumeric
            "ifrs-full:EmployeeBenefitsExpense" -> ifrs_full_EmployeeBenefitsExpense = item.valueNumeric
            "ifrs-full:DepreciationAndAmortisationExpense" -> ifrs_full_DepreciationAndAmortisationExpense = item.valueNumeric
            "ifrs-full:ImpairmentLossReversalOfImpairmentLossRecognisedInProfitOrLoss" -> ifrs_full_ImpairmentLossReversalOfImpairmentLossRecognisedInProfitOrLoss = item.valueNumeric
            "ifrs-full:OtherExpenseByNature" -> ifrs_full_OtherExpenseByNature = item.valueNumeric
            "ifrs-full:OtherGainsLosses" -> ifrs_full_OtherGainsLosses = item.valueNumeric
            "ifrs-full:ProfitLossFromOperatingActivities" -> ifrs_full_ProfitLossFromOperatingActivities = item.valueNumeric
            "ifrs-full:DifferenceBetweenCarryingAmountOfDividendsPayableAndCarryingAmountOfNoncashAssetsDistributed" -> ifrs_full_DifferenceBetweenCarryingAmountOfDividendsPayableAndCarryingAmountOfNoncashAssetsDistributed = item.valueNumeric
            "ifrs-full:GainsLossesOnNetMonetaryPosition" -> ifrs_full_GainsLossesOnNetMonetaryPosition = item.valueNumeric
            "ifrs-full:GainLossArisingFromDerecognitionOfFinancialAssetsMeasuredAtAmortisedCost" -> ifrs_full_GainLossArisingFromDerecognitionOfFinancialAssetsMeasuredAtAmortisedCost = item.valueNumeric
            "ifrs-full:FinanceIncome" -> ifrs_full_FinanceIncome = item.valueNumeric
            "ifrs-full:FinanceCosts" -> ifrs_full_FinanceCosts = item.valueNumeric
            "ifrs-full:ImpairmentLossImpairmentGainAndReversalOfImpairmentLossDeterminedInAccordanceWithIFRS9" -> ifrs_full_ImpairmentLossImpairmentGainAndReversalOfImpairmentLossDeterminedInAccordanceWithIFRS9 = item.valueNumeric
            "ifrs-full:ShareOfProfitLossOfAssociatesAndJointVenturesAccountedForUsingEquityMethod" -> ifrs_full_ShareOfProfitLossOfAssociatesAndJointVenturesAccountedForUsingEquityMethod = item.valueNumeric
            "ifrs-full:OtherIncomeExpenseFromSubsidiariesJointlyControlledEntitiesAndAssociates" -> ifrs_full_OtherIncomeExpenseFromSubsidiariesJointlyControlledEntitiesAndAssociates = item.valueNumeric
            "ifrs-full:GainsLossesArisingFromDifferenceBetweenPreviousCarryingAmountAndFairValueOfFinancialAssetsReclassifiedAsMeasuredAtFairValue" -> ifrs_full_GainsLossesArisingFromDifferenceBetweenPreviousCarryingAmountAndFairValueOfFinancialAssetsReclassifiedAsMeasuredAtFairValue = item.valueNumeric
            "ifrs-full:CumulativeGainLossPreviouslyRecognisedInOtherComprehensiveIncomeArisingFromReclassificationOfFinancialAssetsOutOfFairValueThroughOtherComprehensiveIncomeIntoFairValueThroughProfitOrLossMeasurementCategory" -> ifrs_full_CumulativeGainLossPreviouslyRecognisedInOtherComprehensiveIncomeArisingFromReclassificationOfFinancialAssetsOutOfFairValueThroughOtherComprehensiveIncomeIntoFairValueThroughProfitOrLossMeasurementCategory = item.valueNumeric
            "ifrs-full:HedgingGainsLossesForHedgeOfGroupOfItemsWithOffsettingRiskPositions" -> ifrs_full_HedgingGainsLossesForHedgeOfGroupOfItemsWithOffsettingRiskPositions = item.valueNumeric
            "ifrs-full:ProfitLossBeforeTax" -> ifrs_full_ProfitLossBeforeTax = item.valueNumeric
            "ifrs-full:IncomeTaxExpenseContinuingOperations" -> ifrs_full_IncomeTaxExpenseContinuingOperations = item.valueNumeric
            "ifrs-full:ProfitLossFromContinuingOperations" -> ifrs_full_ProfitLossFromContinuingOperations = item.valueNumeric
            "ifrs-full:ProfitLossFromDiscontinuedOperations" -> ifrs_full_ProfitLossFromDiscontinuedOperations = item.valueNumeric
            "ifrs-full:ProfitLoss" -> ifrs_full_ProfitLoss = item.valueNumeric
            "ifrs-full:ProfitLossAttributableToOwnersOfParent" -> ifrs_full_ProfitLossAttributableToOwnersOfParent = item.valueNumeric
            "ifrs-full:ProfitLossAttributableToNoncontrollingInterests" -> ifrs_full_ProfitLossAttributableToNoncontrollingInterests = item.valueNumeric
            "ifrs-full:EarningsPerShareExplanatory" -> ifrs_full_EarningsPerShareExplanatory = item.value
            "ifrs-full:BasicEarningsLossPerShareFromContinuingOperations" -> ifrs_full_BasicEarningsLossPerShareFromContinuingOperations = item.valueNumeric
            "ifrs-full:BasicEarningsLossPerShareFromDiscontinuedOperations" -> ifrs_full_BasicEarningsLossPerShareFromDiscontinuedOperations = item.valueNumeric
            "ifrs-full:BasicEarningsLossPerShare" -> ifrs_full_BasicEarningsLossPerShare = item.valueNumeric
            "ifrs-full:DilutedEarningsLossPerShareFromContinuingOperations" -> ifrs_full_DilutedEarningsLossPerShareFromContinuingOperations = item.valueNumeric
            "ifrs-full:DilutedEarningsLossPerShareFromDiscontinuedOperations" -> ifrs_full_DilutedEarningsLossPerShareFromDiscontinuedOperations = item.valueNumeric
            "ifrs-full:DilutedEarningsLossPerShare" -> ifrs_full_DilutedEarningsLossPerShare = item.valueNumeric
        }
    }
}
