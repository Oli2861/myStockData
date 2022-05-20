package com.mystockdata.financialreportservice.financialreports

import com.mystockdata.financialreportservice.arelle.Item
import java.math.BigDecimal

data class IFRSStatementOfFinancialPositionOrderOfLiquidity (
    var ifrs_full_PropertyPlantAndEquipment: BigDecimal? = null,
    var ifrs_full_InvestmentProperty: BigDecimal? = null,
    var ifrs_full_Goodwill: BigDecimal? = null,
    var ifrs_full_IntangibleAssetsOtherThanGoodwill: BigDecimal? = null,
    var ifrs_full_OtherFinancialAssets: BigDecimal? = null,
    var ifrs_full_OtherNonfinancialAssets: BigDecimal? = null,
    var ifrs_full_InvestmentAccountedForUsingEquityMethod: BigDecimal? = null,
    var ifrs_full_InvestmentsInSubsidiariesJointVenturesAndAssociates: BigDecimal? = null,
    var ifrs_full_BiologicalAssets: BigDecimal? = null,
    var ifrs_full_NoncurrentAssetsOrDisposalGroupsClassifiedAsHeldForSaleOrAsHeldForDistributionToOwners: BigDecimal? = null,
    var ifrs_full_InventoriesTotal: BigDecimal? = null,
    var ifrs_full_CurrentTaxAssets: BigDecimal? = null,
    var ifrs_full_DeferredTaxAssets: BigDecimal? = null,
    var ifrs_full_TradeAndOtherReceivables: BigDecimal? = null,
    var ifrs_full_CashAndCashEquivalents: BigDecimal? = null,
    var ifrs_full_NoncashAssetsPledgedAsCollateralForWhichTransfereeHasRightByContractOrCustomToSellOrRepledgeCollateral: BigDecimal? = null,
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
    var ifrs_full_TradeAndOtherPayables: BigDecimal? = null,
    var ifrs_full_ProvisionsForEmployeeBenefits: BigDecimal? = null,
    var ifrs_full_OtherProvisions: BigDecimal? = null,
    var ifrs_full_Provisions: BigDecimal? = null,
    var ifrs_full_OtherFinancialLiabilities: BigDecimal? = null,
    var ifrs_full_OtherNonfinancialLiabilities: BigDecimal? = null,
    var ifrs_full_CurrentTaxLiabilities: BigDecimal? = null,
    var ifrs_full_DeferredTaxLiabilities: BigDecimal? = null,
    var ifrs_full_LiabilitiesIncludedInDisposalGroupsClassifiedAsHeldForSale: BigDecimal? = null,
    var ifrs_full_Liabilities: BigDecimal? = null,
    var ifrs_full_EquityAndLiabilities: BigDecimal? = null
){
    fun setValue(item: Item) {
        if (item.name.isNullOrEmpty()) return
        when (item.name) {
            "ifrs-full:PropertyPlantAndEquipment" -> ifrs_full_PropertyPlantAndEquipment = item.valueNumeric
            "ifrs-full:InvestmentProperty" -> ifrs_full_InvestmentProperty = item.valueNumeric
            "ifrs-full:Goodwill" -> ifrs_full_Goodwill = item.valueNumeric
            "ifrs-full:IntangibleAssetsOtherThanGoodwill" -> ifrs_full_IntangibleAssetsOtherThanGoodwill = item.valueNumeric
            "ifrs-full:OtherFinancialAssets" -> ifrs_full_OtherFinancialAssets = item.valueNumeric
            "ifrs-full:OtherNonfinancialAssets" -> ifrs_full_OtherNonfinancialAssets = item.valueNumeric
            "ifrs-full:InvestmentAccountedForUsingEquityMethod" -> ifrs_full_InvestmentAccountedForUsingEquityMethod = item.valueNumeric
            "ifrs-full:InvestmentsInSubsidiariesJointVenturesAndAssociates" -> ifrs_full_InvestmentsInSubsidiariesJointVenturesAndAssociates = item.valueNumeric
            "ifrs-full:BiologicalAssets" -> ifrs_full_BiologicalAssets = item.valueNumeric
            "ifrs-full:NoncurrentAssetsOrDisposalGroupsClassifiedAsHeldForSaleOrAsHeldForDistributionToOwners" -> ifrs_full_NoncurrentAssetsOrDisposalGroupsClassifiedAsHeldForSaleOrAsHeldForDistributionToOwners = item.valueNumeric
            "ifrs-full:InventoriesTotal" -> ifrs_full_InventoriesTotal = item.valueNumeric
            "ifrs-full:CurrentTaxAssets" -> ifrs_full_CurrentTaxAssets = item.valueNumeric
            "ifrs-full:DeferredTaxAssets" -> ifrs_full_DeferredTaxAssets = item.valueNumeric
            "ifrs-full:TradeAndOtherReceivables" -> ifrs_full_TradeAndOtherReceivables = item.valueNumeric
            "ifrs-full:CashAndCashEquivalents" -> ifrs_full_CashAndCashEquivalents = item.valueNumeric
            "ifrs-full:NoncashAssetsPledgedAsCollateralForWhichTransfereeHasRightByContractOrCustomToSellOrRepledgeCollateral" -> ifrs_full_NoncashAssetsPledgedAsCollateralForWhichTransfereeHasRightByContractOrCustomToSellOrRepledgeCollateral = item.valueNumeric
            "ifrs-full:Assets" -> ifrs_full_Assets = item.valueNumeric
            "ifrs-full:IssuedCapital" -> ifrs_full_IssuedCapital = item.valueNumeric
            "ifrs-full:RetainedEarnings" -> ifrs_full_RetainedEarnings = item.valueNumeric
            "ifrs-full:SharePremium" -> ifrs_full_SharePremium = item.valueNumeric
            "ifrs-full:TreasuryShares" -> ifrs_full_TreasuryShares = item.valueNumeric
            "ifrs-full:OtherEquityInterest" -> ifrs_full_OtherEquityInterest = item.valueNumeric
            "ifrs-full:OtherReserves" -> ifrs_full_OtherReserves = item.valueNumeric
            "ifrs-full:EquityAttributableToOwnersOfParent" -> ifrs_full_EquityAttributableToOwnersOfParent = item.valueNumeric
            "ifrs-full:NoncontrollingInterests" -> ifrs_full_NoncontrollingInterests = item.valueNumeric
            "ifrs-full:Equity" -> ifrs_full_Equity = item.valueNumeric
            "ifrs-full:TradeAndOtherPayables" -> ifrs_full_TradeAndOtherPayables = item.valueNumeric
            "ifrs-full:ProvisionsForEmployeeBenefits" -> ifrs_full_ProvisionsForEmployeeBenefits = item.valueNumeric
            "ifrs-full:OtherProvisions" -> ifrs_full_OtherProvisions = item.valueNumeric
            "ifrs-full:Provisions" -> ifrs_full_Provisions = item.valueNumeric
            "ifrs-full:OtherFinancialLiabilities" -> ifrs_full_OtherFinancialLiabilities = item.valueNumeric
            "ifrs-full:OtherNonfinancialLiabilities" -> ifrs_full_OtherNonfinancialLiabilities = item.valueNumeric
            "ifrs-full:CurrentTaxLiabilities" -> ifrs_full_CurrentTaxLiabilities = item.valueNumeric
            "ifrs-full:DeferredTaxLiabilities" -> ifrs_full_DeferredTaxLiabilities = item.valueNumeric
            "ifrs-full:LiabilitiesIncludedInDisposalGroupsClassifiedAsHeldForSale" -> ifrs_full_LiabilitiesIncludedInDisposalGroupsClassifiedAsHeldForSale = item.valueNumeric
            "ifrs-full:Liabilities" -> ifrs_full_Liabilities = item.valueNumeric
            "ifrs-full:EquityAndLiabilities" -> ifrs_full_EquityAndLiabilities = item.valueNumeric
        }
    }
}