@file:Suppress("SpringJavaInjectionPointsAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection")

package com.mystockdata.financialreportservice.financialreports

import com.mystockdata.financialreportservice.arelle.ArelleAdapter
import com.mystockdata.financialreportservice.arelle.Item
import com.mystockdata.financialreportservice.arelle.TYPE
import com.mystockdata.financialreportservice.financialreports.FinancialReportServiceConstants.DELAY_TIME
import com.mystockdata.financialreportservice.xbrlfilings.RetrievedReportInfo
import com.mystockdata.financialreportservice.xbrlfilings.XBRLFilingsAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.transform
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

object FinancialReportServiceConstants {
    const val DELAY_TIME: Long = 120_000L
}

@Service
class FinancialReportService(
    @Autowired val arelleAdapter: ArelleAdapter, @Autowired val xbrlFilingsAdapter: XBRLFilingsAdapter
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(FinancialReportService::class.java)
    }

    /**
     * Tells arelle to load a taxonomy.
     * @param fileName Name of the taxonomy file to be loaded. File has to be placed within the taxonomies (docker) volume. By default, loads the esef taxonomy.
     * @return Boolean indicating success (true) or failure (false) of the operation.
     */
    suspend fun loadEsefTaxonomy(fileName: String = "esef_taxonomy_2021.zip"): Boolean{
        val result = arelleAdapter.loadTaxonomy(fileName)
        logger.debug("Taxonomy $fileName was ${if(result) "successfully" else "not"} loaded.")
        return result
    }

    suspend fun getFinancialReports(): Flow<List<FinancialReport>> {
        val retrievedReportInfoFlow: Flow<RetrievedReportInfo> = xbrlFilingsAdapter.getAvailableFinancialReports()

        val financialReportFlow = retrievedReportInfoFlow.filter { retrievedReportInfo ->
            // Only save reports that have no errors and are not saved yet
            retrievedReportInfo.hasErrors != null && retrievedReportInfo.lei != null && retrievedReportInfo.date != null && !retrievedReportInfo.hasErrors!! && !checkReportAlreadyExists(
                retrievedReportInfo.lei!!,
                retrievedReportInfo.date!!
            )
        }.transform { retrievedReportInfo ->
            logger.debug("Delayed for ${DELAY_TIME / 1000} seconds, afterwards retrieving report for ${retrievedReportInfo.name}")
            kotlinx.coroutines.delay(DELAY_TIME)
            val factList = arelleAdapter.retrieveFacts(retrievedReportInfo.url)

            if (factList != null) {
                // Generates financial reports for each year for which data is found in the financial report (often includes data from the previous year to compare the current year against)
                val reports = getFinancialReports(factList)
                val mainReport = reports.find { it.date == retrievedReportInfo.date }

                // Log if the lei and date in the report does not match the lei and date from the location the report was retrieved from ("https://filings.xbrl.org/)
                if (mainReport != null && mainReport.entityIdentifier == retrievedReportInfo.lei && mainReport.date == retrievedReportInfo.date) {
                    logger.debug("Successfully retrieved financial report for ${mainReport.date} of company ${mainReport.entityIdentifier}")
                } else {
                    // No report was generated whose year matches the year specified in the data source
                    logger.debug("Mismatch: Entity identifier of the generated report: ${mainReport?.entityIdentifier}\tEntity identifier provided by data source: ${retrievedReportInfo.lei}\nDate of the generated report ${mainReport?.date}\tDate provided by data source ${retrievedReportInfo.date}")
                }

                emit(reports)
            }
        }
        return financialReportFlow
    }

    suspend fun checkReportAlreadyExists(lei: String, date: Date): Boolean {
        // TODO: Implement function
        return false
    }

    /**
     * Generate financial reports from items / facts.
     * @param factList List containing items / facts.
     * @return Financial reports and for each year.
     */
    suspend fun getFinancialReports(factList: List<Item>): List<FinancialReport> {
        return splitByDate(factList).mapValues { (date, list) ->
            createFinancialReportFromList(date, list)
        }.values.toList()
    }

    /**
     * Map end instants on lists of items in order to differentiate between different report items (balance sheet 2019, 2020,..)
     * @param list List containing all items.
     * @return Map mapping Dates to Lists of items.
     */
    fun splitByDate(list: List<Item>): Map<Date, List<Item>> {
        val map = HashMap<Date, MutableList<Item>>()

        list.forEach { item ->
            item.endInstant?.let { endDate ->
                if (map[endDate].isNullOrEmpty()) {
                    map[endDate] = mutableListOf(item)
                } else {
                    map[endDate]?.add(item)
                }
            }
        }

        return map
    }

    /**
     * Create a financial reports from a list of items.
     * @param list List of items used to create a financial reports.
     * @return Financial report containing the information of the provided items.
     */
    fun createFinancialReportFromList(date: Date, list: List<Item>): FinancialReport {
        val ifrsGeneralInformation = IFRSGeneralInformation()
        val ifrsStatementOfFinancialPositionCurrentNotCurrent = IFRSStatementOfFinancialPositionCurrentNotCurrent()
        val ifrsStatementOfFinancialPositionOrderOfLiquidity = IFRSStatementOfFinancialPositionOrderOfLiquidity()
        val ifrsStatementOfComprehensiveIncomeByFunctionOfExpense = IFRSStatementOfComprehensiveIncomeByFunctionOfExpense()
        val ifrsStatementOfComprehensiveIncomeByNatureOfExpense = IFRSStatementOfComprehensiveIncomeByNatureOfExpense()
        val currency: String = list.firstNotNullOf { it.unitRef }
        val entityIdentifier: String = list.firstNotNullOf { it.entityIdentifier }
        val entityScheme: String = list.firstNotNullOf { it.entityScheme }

        // Loop over elements and their assignment to the appropriate report component
        list.forEach { item ->
            val type = item.type

            if (type == TYPE.DATE_ITEM.str || type == TYPE.STRING_ITEM.str) {
                ifrsGeneralInformation.setValue(item)
            }
            if (type == TYPE.MONETARY_ITEM.str || type == TYPE.PER_SHARE_ITEM.str || type == TYPE.STRING_ITEM.str) {
                if (type != TYPE.STRING_ITEM.str) {
                    ifrsStatementOfFinancialPositionCurrentNotCurrent.setValue(item)
                    ifrsStatementOfFinancialPositionOrderOfLiquidity.setValue(item)
                }
                ifrsStatementOfComprehensiveIncomeByFunctionOfExpense.setValue(item)
                ifrsStatementOfComprehensiveIncomeByNatureOfExpense.setValue(item)
            }

        }

        return FinancialReport(
            entityIdentifier,
            entityScheme,
            date,
            ifrsGeneralInformation,
            ifrsStatementOfFinancialPositionCurrentNotCurrent,
            ifrsStatementOfFinancialPositionOrderOfLiquidity,
            ifrsStatementOfComprehensiveIncomeByFunctionOfExpense,
            ifrsStatementOfComprehensiveIncomeByNatureOfExpense,
            currency
        )
    }

}