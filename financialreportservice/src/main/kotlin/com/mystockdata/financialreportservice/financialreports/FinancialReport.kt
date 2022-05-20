package com.mystockdata.financialreportservice.financialreports

import java.util.Date

data class FinancialReport(
    val IFRSGeneralInformation: IFRSGeneralInformation,
    val IFRSStatementOfFinancialPositionCurrentNotCurrent: IFRSStatementOfFinancialPositionCurrentNotCurrent,
    val IFRSStatementOfFinancialPositionOrderOfLiquidity: IFRSStatementOfFinancialPositionOrderOfLiquidity,
    val date: Date,
    val currency: String,
    val entityIdentifier: String,
    val entityIdentifierScheme: String
)

// TODO: data class StatementOfComprehensiveIncomeByFunctionOfExpense()
// TODO: data class StatementOfComprehensiveIncomeByNatureOfExpense()
