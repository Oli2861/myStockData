package com.mystockdata.financialreportservice.financialreports

import com.mystockdata.financialreportservice.financialreportdatasource.Item
import com.mystockdata.financialreportservice.financialreportdatasource.ItemType
import com.mystockdata.financialreportservice.financialreportdatasource.FinancialReportDataSource
import com.mystockdata.financialreportservice.financialreportevent.FinancialReportEvent
import com.mystockdata.financialreportservice.financialreportevent.FinancialReportEventType
import com.mystockdata.financialreportservice.financialreportinformation.ReportInfoDataSource
import com.mystockdata.financialreportservice.financialreports.FinancialReportServiceConstants.DELAY_TIME
import com.mystockdata.financialreportservice.utility.sameDay
import com.mystockdata.financialreportservice.financialreportinformation.RetrievedReportInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.transform
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

object FinancialReportServiceConstants {
    const val DELAY_TIME: Long = 30_000L
}

@Service
class FinancialReportService(
    @Autowired val financialReportDataSource: FinancialReportDataSource,
    @Autowired val reportInfoDataSource: ReportInfoDataSource
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FinancialReportService::class.java)
    }

    suspend fun handleEvent(financialReportEvent: FinancialReportEvent) {
        when (financialReportEvent.financialReportEventType) {
            FinancialReportEventType.REFRESH_DATA -> retrieveAvailableFinancialReports().collect()
            else -> logger.error("Unexpected FinancialReportEventType. Event: $financialReportEvent")
        }
    }

    /**
     * Retrieves Financial reports from a remote data source.
     * @param relevantLEI LEI of the reports to retrieve. If none are specified all available reports will be retrieved.
     * @return Flow emitting lists of the retrieved financial reports.
     */
    suspend fun retrieveAvailableFinancialReports(relevantLEI: List<String> = listOf()): Flow<List<FinancialReport>> {
        val eligibleReportInfoFlow = if (relevantLEI.isEmpty()) {
            reportInfoDataSource.getAvailableFinancialReports()
                .filter { checkEligibility(it) }
        } else {
            reportInfoDataSource.getAvailableFinancialReports()
                .filter { checkEligibility(it) }
                .filter { retrievedReportInfo -> relevantLEI.contains(retrievedReportInfo.lei) }
        }

        val retrievedReportFlow = getFinancialReports(eligibleReportInfoFlow)
        // TODO Save reports to database
        return retrievedReportFlow
    }

    /**
     * Retrieves available reports based on a given report info flow.
     * @param reportInfoFlow Report information used to retrieve financial reports from the financial report data source. Mainly the url-property is of importance.
     * @return Flow containing the retrieved reports.
     */
    private suspend fun getFinancialReports(reportInfoFlow: Flow<RetrievedReportInfo>): Flow<List<FinancialReport>> {
        return reportInfoFlow.transform { retrievedReportInfo ->
            logger.debug("Delayed for ${DELAY_TIME / 1000} seconds, afterwards retrieving report for ${retrievedReportInfo.name}")
            kotlinx.coroutines.delay(DELAY_TIME)

            val financialReportList = retrieveReportByReportInfo(retrievedReportInfo)
            if (financialReportList != null) {
                emit(financialReportList)
                logger.debug("Retrieved financial report ${financialReportList[0].entityIdentifier} for reporting Period ${financialReportList[0].endOfReportingPeriod}")
                // financialReportList.forEach { report -> report.factList.forEach { fact -> logger.debug("$fact") } }
            }
        }
    }

    /**
     * Retrieves financial reports associated with the given RetrievedReportInfo.
     * @param retrievedReportInfo RetrievedReportInfo associated with the financial reports to be retrieved.
     * @return List containing the retrieved financial reports or null if anything goes wrong.
     */
    suspend fun retrieveReportByReportInfo(retrievedReportInfo: RetrievedReportInfo): List<FinancialReport>? {
        val factList = financialReportDataSource.retrieveFacts(retrievedReportInfo.url)
        return if (factList != null) {
            // Generates financial reports for each year for which data is found in the financial report (often includes data from the previous year to compare the current year against)
            val reports = getFinancialReports(factList)
            checkMainReportExists(retrievedReportInfo, reports)
            reports
        } else {
            logger.debug("Unable to retrieve facts for $retrievedReportInfo")
            null
        }
    }

    /**
     * Checks whether a report is eligible for retrieval.
     * @param retrievedReportInfo Information about the report to be retrieved.
     * @return Whether a report should be retrieved.
     */
    private suspend fun checkEligibility(retrievedReportInfo: RetrievedReportInfo): Boolean {
        return retrievedReportInfo.lei != null
                && retrievedReportInfo.date != null
                && !checkReportAlreadyExists(retrievedReportInfo.lei!!, retrievedReportInfo.date!!)
    }

    /**
     * Checks whether a financial report already exists.
     * @param lei Legal identity identifier.
     * @param endDate End of the reporting period.
     * @return whether the report already exists.
     */
    private suspend fun checkReportAlreadyExists(lei: String, endDate: Date): Boolean {
        // TODO: Implement function
        return false
    }


    /**
     * Generate financial reports from items / facts.
     * @param factList List containing items / facts.
     * @return Financial reports for each year.
     */
    private suspend fun getFinancialReports(factList: List<Item>): List<FinancialReport> {
        val factsSplitByPeriodEnd: Map<Date, List<Item>> = splitByPeriodEnd(factList)
        val financialReports: List<FinancialReport?> =
            factsSplitByPeriodEnd.map { (endOfPeriod, items) -> createFinancialReportFromList(endOfPeriod, items) }
        return financialReports.filterNotNull()
    }

    /**
     * Map end instants on lists of items in order to differentiate between different report items (balance sheet 2019, 2020,..)
     * @param list List containing all items.
     * @return Map mapping instants to lists of items.
     */
    fun splitByPeriodEnd(list: List<Item>): Map<Date, List<Item>> {
        val map = HashMap<Date, List<Item>>()
        val endDate = list.distinctBy { it.endInstant }.mapNotNull { it.endInstant }
        endDate.forEach { date ->
            map[date] = list.filter { it.endInstant == date }
        }

        return map
    }

    /**
     * Build a Financial report based on a list of items.
     * @param endOfPeriod End of the reporting period of all associated Facts.
     * @param list List of fact items.
     * @return Financial report or null if there was no entityIdentifier found.
     */
    private suspend fun createFinancialReportFromList(endOfPeriod: Date, list: List<Item>): FinancialReport? {
        val entityIdentifier: String = list.firstNotNullOfOrNull { it.entityIdentifier } ?: return null
        val entityScheme: String? = list.firstNotNullOfOrNull { it.entityScheme }

        val factList: List<Fact> = list.mapNotNull { item -> itemToFact(item) }

        return FinancialReport(endOfPeriod, entityIdentifier, entityScheme ?: "unknown", factList)
    }

    private fun itemToFact(item: Item): Fact? {
        return if (item.value == null || item.name == null) {
            logger.trace("${if (item.value == null) "Item Value is null" else "Item name is null"} item retrieved: $item")
            null
        } else {
            return when (item.type) {
                ItemType.MONETARY_ITEM.typeName -> parseMonetary(item)
                ItemType.PER_SHARE_ITEM.typeName -> parseNumeric(item)
                ItemType.PER_SHARE_ITEM_NON_XBRLI.typeName -> parseNumeric(item)
                ItemType.STRING_ITEM.typeName -> parseTextual(item)
                ItemType.DATE_ITEM.typeName -> parseTextual(item)
                else -> {
                    logger.debug("Unknown type: ${item.type}")
                    parseTextual(item)
                }
            }
        }
    }

    private fun parseNumeric(item: Item): NumericFact? {
        return if (item.name == null || item.start == null || item.endInstant == null || item.valueNumeric == null) null
        else NumericFact(item.name!!, item.start!!, item.endInstant!!, item.valueNumeric!!)
    }

    private fun parseMonetary(item: Item): Fact? {
        return if (item.name == null || item.start == null || item.endInstant == null || item.valueNumeric == null || item.unitRef == null || item.balance == null) null
        else if (item.value == "(nil)") return parseTextual(item)
        else MonetaryFact(
            item.name!!, item.start!!, item.endInstant!!, item.valueNumeric!!, item.unitRef!!, item.balance!!
        )
    }

    private fun parseTextual(item: Item): TextualFact? {
        return if (item.name == null || item.start == null || item.endInstant == null || item.value == null) null
        else TextualFact(item.name!!, item.start!!, item.endInstant!!, item.value!!)
    }

    /**
     * For debugging purposes:
     * Checks whether the main report (matches year with RetrievedReportInfo) could be identified.
     */
    private fun checkMainReportExists(retrievedReportInfo: RetrievedReportInfo, reports: List<FinancialReport>) {
        if (retrievedReportInfo.date == null) {
            logger.debug("No date retrieved. RetrievedReportInfo: $retrievedReportInfo.")
        }
        retrievedReportInfo.date?.let { reportInfoDate ->
            val mainReport = reports.find { it.endOfReportingPeriod.sameDay(reportInfoDate) }
            if (mainReport != null) {
                // Log if the lei and date in the report does not match the lei and date from the location the report was retrieved from ("https://filings.xbrl.org/)
                if (mainReport.entityIdentifier == retrievedReportInfo.lei) {
                    logger.debug("Successfully retrieved financial report for ${mainReport.endOfReportingPeriod} of company ${mainReport.entityIdentifier}")
                } else {
                    // No report was generated whose year matches the year specified in the data source
                    logger.debug("Mismatch: Entity identifier of the generated report: ${mainReport.entityIdentifier}\tEntity identifier provided by data source: ${retrievedReportInfo.lei}")
                }
            } else {
                logger.debug("Main report could not be identified. RetrievedReportInfo: $retrievedReportInfo\nRetrieved Reports: $reports")
            }
        }
    }
    /**
     * Tells arelle to load a taxonomy.
     * @param fileName Name of the taxonomy file to be loaded. File has to be placed within the taxonomies (docker) volume. By default, loads the esef taxonomy.
     * @return Boolean indicating success (true) or failure (false) of the operation.

    suspend fun loadEsefTaxonomy(fileName: String = "esef_taxonomy_2021.zip"): Boolean {
    val result = financialReportDataSource.loadTaxonomy(fileName)
    logger.debug("Taxonomy $fileName was ${if (result) "successfully" else "not"} loaded.")
    return result
    }
     */
}
