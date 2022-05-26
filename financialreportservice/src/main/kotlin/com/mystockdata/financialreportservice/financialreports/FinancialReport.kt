package com.mystockdata.financialreportservice.financialreports

import java.util.Date

data class FinancialReport(
    val entityIdentifier: String,
    val entityIdentifierScheme: String,
    val year: Int,
    val IFRSGeneralInformation: IFRSGeneralInformation,
    val IFRSStatementOfFinancialPositionCurrentNotCurrent: IFRSStatementOfFinancialPositionCurrentNotCurrent,
    val IFRSStatementOfFinancialPositionOrderOfLiquidity: IFRSStatementOfFinancialPositionOrderOfLiquidity,
    val IFRSStatementOfComprehensiveIncomeByFunctionOfExpense: IFRSStatementOfComprehensiveIncomeByFunctionOfExpense,
    val IFRSStatementOfComprehensiveIncomeByNatureOfExpense: IFRSStatementOfComprehensiveIncomeByNatureOfExpense,
    val currency: String
)