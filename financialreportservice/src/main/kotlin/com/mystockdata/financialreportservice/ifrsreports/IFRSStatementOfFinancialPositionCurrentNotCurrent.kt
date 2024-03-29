package com.mystockdata.financialreportservice.ifrsreports

import com.mystockdata.financialreportservice.financialreportdatasource.Item
import java.math.BigDecimal

/**
 *  IFRS 2022 Statement of financial position (non-) current
 */
data class IFRSStatementOfFinancialPositionCurrentNotCurrent(
    var ifrs_full_PropertyPlantAndEquipment: BigDecimal? = null,
    var ifrs_full_InvestmentProperty: BigDecimal? = null,
    var ifrs_full_Goodwill: BigDecimal? = null,
    var ifrs_full_IntangibleAssetsOtherThanGoodwill: BigDecimal? = null,
    var ifrs_full_InvestmentsAccountedForUsingEquityMethodMember: BigDecimal? = null,
    var ifrs_full_InvestmentsInSubsidiariesJointVenturesAndAssociates: BigDecimal? = null,
    var ifrs_full_NoncurrentBiologicalAssets: BigDecimal? = null,
    var ifrs_full_NoncurrentReceivables: BigDecimal? = null,
    var ifrs_full_NoncurrentInventories: BigDecimal? = null,
    var ifrs_full_DeferredTaxAssets: BigDecimal? = null,
    var ifrs_full_CurrentTaxAssetsNoncurrent: BigDecimal? = null,
    var ifrs_full_OtherNoncurrentFinancialAssets: BigDecimal? = null,
    var ifrs_full_OtherNoncurrentNonfinancialAssets: BigDecimal? = null,
    var ifrs_full_NoncurrentNoncashAssetsPledgedAsCollateralForWhichTransfereeHasRightByContractOrCustomToSellOrRepledgeCollateral: BigDecimal? = null,
    var ifrs_full_NoncurrentAssets: BigDecimal? = null,
    var ifrs_full_Inventories: BigDecimal? = null,
    var ifrs_full_TradeAndOtherCurrentReceivables: BigDecimal? = null,
    var ifrs_full_CurrentTaxAssetsCurrent: BigDecimal? = null,
    var ifrs_full_CurrentBiologicalAssets: BigDecimal? = null,
    var ifrs_full_OtherCurrentFinancialAssets: BigDecimal? = null,
    var ifrs_full_OtherCurrentNonfinancialAssets: BigDecimal? = null,
    var ifrs_full_CashAndCashEquivalents: BigDecimal? = null,
    var ifrs_full_CurrentNoncashAssetsPledgedAsCollateralForWhichTransfereeHasRightByContractOrCustomToSellOrRepledgeCollateral: BigDecimal? = null,
    var ifrs_full_CurrentAssetsOtherThanAssetsOrDisposalGroupsClassifiedAsHeldForSaleOrAsHeldForDistributionToOwners: BigDecimal? = null,
    var ifrs_full_NoncurrentAssetsOrDisposalGroupsClassifiedAsHeldForSaleOrAsHeldForDistributionToOwners: BigDecimal? = null,
    var ifrs_full_CurrentAssets: BigDecimal? = null,
    var ifrs_full_Assets: BigDecimal? = null,
    var ifrs_full_IssuedCapital: BigDecimal? = null,
    var ifrs_full_RetainedEarnings: BigDecimal? = null,
    var ifrs_full_SharePremium: BigDecimal? = null,
    var ifrs_full_TreasuryShares: BigDecimal? = null,
    var ifrs_full_OtherEquityInterest: BigDecimal? = null,
    var ifrs_full_OtherReserves: BigDecimal? = null,
    var ifrs_full_EquityAttributableToOwnersOfParent: BigDecimal? = null,
    var ifrs_full_NoncontrollingInterests: BigDecimal? = null,
    var ifrs_full_Equity: BigDecimal? = null,
    var ifrs_full_NoncurrentProvisionsForEmployeeBenefits: BigDecimal? = null,
    var ifrs_full_OtherLongtermProvisions: BigDecimal? = null,
    var ifrs_full_NoncurrentProvisions: BigDecimal? = null,
    var ifrs_full_NoncurrentPayables: BigDecimal? = null,
    var ifrs_full_DeferredTaxLiabilities: BigDecimal? = null,
    var ifrs_full_CurrentTaxLiabilitiesNoncurrent: BigDecimal? = null,
    var ifrs_full_OtherNoncurrentFinancialLiabilities: BigDecimal? = null,
    var ifrs_full_OtherNoncurrentNonfinancialLiabilities: BigDecimal? = null,
    var ifrs_full_NoncurrentLiabilities: BigDecimal? = null,
    var ifrs_full_CurrentProvisionsForEmployeeBenefits: BigDecimal? = null,
    var ifrs_full_OtherShorttermProvisions: BigDecimal? = null,
    var ifrs_full_CurrentProvisions: BigDecimal? = null,
    var ifrs_full_TradeAndOtherCurrentPayables: BigDecimal? = null,
    var ifrs_full_CurrentTaxLiabilitiesCurrent: BigDecimal? = null,
    var ifrs_full_OtherCurrentFinancialLiabilities: BigDecimal? = null,
    var ifrs_full_OtherCurrentNonfinancialLiabilities: BigDecimal? = null,
    var ifrs_full_CurrentLiabilitiesOtherThanLiabilitiesIncludedInDisposalGroupsClassifiedAsHeldForSale: BigDecimal? = null,
    var ifrs_full_LiabilitiesIncludedInDisposalGroupsClassifiedAsHeldForSale: BigDecimal? = null,
    var ifrs_full_CurrentLiabilities: BigDecimal? = null,
    var ifrs_full_Liabilities: BigDecimal? = null,
    var ifrs_full_EquityAndLiabilities: BigDecimal? = null
) {
    fun setValue(item: Item) = keyLambdaMap[item.name]?.invoke(this, item)
}

private val keyLambdaMap = mapOf<String, (IFRSStatementOfFinancialPositionCurrentNotCurrent, Item) -> Unit>(
    "ifrs-full:PropertyPlantAndEquipment" to {statement, item -> statement.ifrs_full_PropertyPlantAndEquipment = item.valueNumeric},
    "ifrs-full:InvestmentProperty" to {statement, item -> statement.ifrs_full_InvestmentProperty = item.valueNumeric},
    "ifrs-full:Goodwill" to {statement, item -> statement.ifrs_full_Goodwill = item.valueNumeric},
    "ifrs-full:IntangibleAssetsOtherThanGoodwill" to {statement, item -> statement.ifrs_full_IntangibleAssetsOtherThanGoodwill = item.valueNumeric},
    "ifrs-full:InvestmentsAccountedForUsingEquityMethodMember" to {statement, item -> statement.ifrs_full_InvestmentsAccountedForUsingEquityMethodMember = item.valueNumeric},
    "ifrs-full:InvestmentsInSubsidiariesJointVenturesAndAssociates" to {statement, item -> statement.ifrs_full_InvestmentsInSubsidiariesJointVenturesAndAssociates = item.valueNumeric},
    "ifrs-full:NoncurrentBiologicalAssets" to {statement, item -> statement.ifrs_full_NoncurrentBiologicalAssets = item.valueNumeric},
    "ifrs-full:NoncurrentReceivables" to {statement, item -> statement.ifrs_full_NoncurrentReceivables = item.valueNumeric},
    "ifrs-full:NoncurrentInventories" to {statement, item -> statement.ifrs_full_NoncurrentInventories = item.valueNumeric},
    "ifrs-full:DeferredTaxAssets" to {statement, item -> statement.ifrs_full_DeferredTaxAssets = item.valueNumeric},
    "ifrs-full:CurrentTaxAssetsNoncurrent" to {statement, item -> statement.ifrs_full_CurrentTaxAssetsNoncurrent = item.valueNumeric},
    "ifrs-full:OtherNoncurrentFinancialAssets" to {statement, item -> statement.ifrs_full_OtherNoncurrentFinancialAssets = item.valueNumeric},
    "ifrs-full:OtherNoncurrentNonfinancialAssets" to {statement, item -> statement.ifrs_full_OtherNoncurrentNonfinancialAssets = item.valueNumeric},
    "ifrs-full:NoncurrentNoncashAssetsPledgedAsCollateralForWhichTransfereeHasRightByContractOrCustomToSellOrRepledgeCollateral" to {statement, item -> statement.ifrs_full_NoncurrentNoncashAssetsPledgedAsCollateralForWhichTransfereeHasRightByContractOrCustomToSellOrRepledgeCollateral = item.valueNumeric},
    "ifrs-full:NoncurrentAssets" to {statement, item -> statement.ifrs_full_NoncurrentAssets = item.valueNumeric},
    "ifrs-full:Inventories" to {statement, item -> statement.ifrs_full_Inventories = item.valueNumeric},
    "ifrs-full:TradeAndOtherCurrentReceivables" to {statement, item -> statement.ifrs_full_TradeAndOtherCurrentReceivables = item.valueNumeric},
    "ifrs-full:CurrentTaxAssetsCurrent" to {statement, item -> statement.ifrs_full_CurrentTaxAssetsCurrent = item.valueNumeric},
    "ifrs-full:CurrentBiologicalAssets" to {statement, item -> statement.ifrs_full_CurrentBiologicalAssets = item.valueNumeric},
    "ifrs-full:OtherCurrentFinancialAssets" to {statement, item -> statement.ifrs_full_OtherCurrentFinancialAssets = item.valueNumeric},
    "ifrs-full:OtherCurrentNonfinancialAssets" to {statement, item -> statement.ifrs_full_OtherCurrentNonfinancialAssets = item.valueNumeric},
    "ifrs-full:CashAndCashEquivalents" to {statement, item -> statement.ifrs_full_CashAndCashEquivalents = item.valueNumeric},
    "ifrs-full:CurrentNoncashAssetsPledgedAsCollateralForWhichTransfereeHasRightByContractOrCustomToSellOrRepledgeCollateral" to {statement, item -> statement.ifrs_full_CurrentNoncashAssetsPledgedAsCollateralForWhichTransfereeHasRightByContractOrCustomToSellOrRepledgeCollateral = item.valueNumeric},
    "ifrs-full:CurrentAssetsOtherThanAssetsOrDisposalGroupsClassifiedAsHeldForSaleOrAsHeldForDistributionToOwners" to {statement, item -> statement.ifrs_full_CurrentAssetsOtherThanAssetsOrDisposalGroupsClassifiedAsHeldForSaleOrAsHeldForDistributionToOwners = item.valueNumeric},
    "ifrs-full:NoncurrentAssetsOrDisposalGroupsClassifiedAsHeldForSaleOrAsHeldForDistributionToOwners" to {statement, item -> statement.ifrs_full_NoncurrentAssetsOrDisposalGroupsClassifiedAsHeldForSaleOrAsHeldForDistributionToOwners = item.valueNumeric},
    "ifrs-full:CurrentAssets" to {statement, item -> statement.ifrs_full_CurrentAssets = item.valueNumeric},
    "ifrs-full:Assets" to {statement, item -> statement.ifrs_full_Assets = item.valueNumeric},
    "ifrs-full:IssuedCapital" to {statement, item -> statement.ifrs_full_IssuedCapital = item.valueNumeric},
    "ifrs-full:RetainedEarnings" to {statement, item -> statement.ifrs_full_RetainedEarnings = item.valueNumeric},
    "ifrs-full:SharePremium" to {statement, item -> statement.ifrs_full_SharePremium = item.valueNumeric},
    "ifrs-full:TreasuryShares" to {statement, item -> statement.ifrs_full_TreasuryShares = item.valueNumeric},
    "ifrs-full:OtherEquityInterest" to {statement, item -> statement.ifrs_full_OtherEquityInterest = item.valueNumeric},
    "ifrs-full:OtherReserves" to {statement, item -> statement.ifrs_full_OtherReserves = item.valueNumeric},
    "ifrs-full:EquityAttributableToOwnersOfParent" to {statement, item -> statement.ifrs_full_EquityAttributableToOwnersOfParent = item.valueNumeric},
    "ifrs-full:NoncontrollingInterests" to {statement, item -> statement.ifrs_full_NoncontrollingInterests = item.valueNumeric},
    "ifrs-full:Equity" to {statement, item -> statement.ifrs_full_Equity = item.valueNumeric},
    "ifrs-full:NoncurrentProvisionsForEmployeeBenefits" to {statement, item -> statement.ifrs_full_NoncurrentProvisionsForEmployeeBenefits = item.valueNumeric},
    "ifrs-full:OtherLongtermProvisions" to {statement, item -> statement.ifrs_full_OtherLongtermProvisions = item.valueNumeric},
    "ifrs-full:NoncurrentProvisions" to {statement, item -> statement.ifrs_full_NoncurrentProvisions = item.valueNumeric},
    "ifrs-full:NoncurrentPayables" to {statement, item -> statement.ifrs_full_NoncurrentPayables = item.valueNumeric},
    "ifrs-full:DeferredTaxLiabilities" to {statement, item -> statement.ifrs_full_DeferredTaxLiabilities = item.valueNumeric},
    "ifrs-full:CurrentTaxLiabilitiesNoncurrent" to {statement, item -> statement.ifrs_full_CurrentTaxLiabilitiesNoncurrent = item.valueNumeric},
    "ifrs-full:OtherNoncurrentFinancialLiabilities" to {statement, item -> statement.ifrs_full_OtherNoncurrentFinancialLiabilities = item.valueNumeric},
    "ifrs-full:OtherNoncurrentNonfinancialLiabilities" to {statement, item -> statement.ifrs_full_OtherNoncurrentNonfinancialLiabilities = item.valueNumeric},
    "ifrs-full:NoncurrentLiabilities" to {statement, item -> statement.ifrs_full_NoncurrentLiabilities = item.valueNumeric},
    "ifrs-full:CurrentProvisionsForEmployeeBenefits" to {statement, item -> statement.ifrs_full_CurrentProvisionsForEmployeeBenefits = item.valueNumeric},
    "ifrs-full:OtherShorttermProvisions" to {statement, item -> statement.ifrs_full_OtherShorttermProvisions = item.valueNumeric},
    "ifrs-full:CurrentProvisions" to {statement, item -> statement.ifrs_full_CurrentProvisions = item.valueNumeric},
    "ifrs-full:TradeAndOtherCurrentPayables" to {statement, item -> statement.ifrs_full_TradeAndOtherCurrentPayables = item.valueNumeric},
    "ifrs-full:CurrentTaxLiabilitiesCurrent" to {statement, item -> statement.ifrs_full_CurrentTaxLiabilitiesCurrent = item.valueNumeric},
    "ifrs-full:OtherCurrentFinancialLiabilities" to {statement, item -> statement.ifrs_full_OtherCurrentFinancialLiabilities = item.valueNumeric},
    "ifrs-full:OtherCurrentNonfinancialLiabilities" to {statement, item -> statement.ifrs_full_OtherCurrentNonfinancialLiabilities = item.valueNumeric},
    "ifrs-full:CurrentLiabilitiesOtherThanLiabilitiesIncludedInDisposalGroupsClassifiedAsHeldForSale" to {statement, item -> statement.ifrs_full_CurrentLiabilitiesOtherThanLiabilitiesIncludedInDisposalGroupsClassifiedAsHeldForSale = item.valueNumeric},
    "ifrs-full:LiabilitiesIncludedInDisposalGroupsClassifiedAsHeldForSale" to {statement, item -> statement.ifrs_full_LiabilitiesIncludedInDisposalGroupsClassifiedAsHeldForSale = item.valueNumeric},
    "ifrs-full:CurrentLiabilities" to {statement, item -> statement.ifrs_full_CurrentLiabilities = item.valueNumeric},
    "ifrs-full:Liabilities" to {statement, item -> statement.ifrs_full_Liabilities = item.valueNumeric},
    "ifrs-full:EquityAndLiabilities" to {statement, item -> statement.ifrs_full_EquityAndLiabilities = item.valueNumeric},
)