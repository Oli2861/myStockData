@file:Suppress("SpringJavaInjectionPointsAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection")

package com.mystockdata.financialreportservice.financialreports

import com.mystockdata.financialreportservice.arelle.ArelleAdapter
import com.mystockdata.financialreportservice.arelle.Item
import com.mystockdata.financialreportservice.arelle.TYPE
import com.mystockdata.financialreportservice.xbrlfilings.RetrievedReportInfo
import com.mystockdata.financialreportservice.xbrlfilings.XBRLFilingsAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class FinancialReportService(
    @Autowired
    val arelleAdapter: ArelleAdapter,
    @Autowired
    val xbrlFilingsAdapter: XBRLFilingsAdapter
) {
    /**
     * Tells arelle to load a taxonomy.
     * @param fileName Name of the taxonomy file to be loaded. File has to be placed within the taxonomies (docker) volume. By default, loads the esef taxonomy.
     * @return Boolean indicating success (true) or failure (false) of the operation.
     */
    suspend fun loadEsefTaxonomy(fileName: String = "esef_taxonomy_2021.zip"): Boolean =
        arelleAdapter.loadTaxonomy(fileName)

    suspend fun loadFinancialReports(): Flow<List<FinancialReport>> {
        val retrievedReportInfoFlow: Flow<RetrievedReportInfo> = xbrlFilingsAdapter.getAvailableFinancialReports()
        // TODO: Check whether the data is already stored and only request the zip when it is not

        val financialReportFlow =
            retrievedReportInfoFlow.transform<RetrievedReportInfo, List<FinancialReport>> { retrievedReportInfo ->
                val factList = arelleAdapter.retrieveFacts(retrievedReportInfo.url)
                if (factList != null) emit(getFinancialReports(factList))
            }
        return financialReportFlow
    }

    /**
     * Retrieve financial reports from a given path.
     * @param path Path to the local report.
     * @return Map containing the date for the financial report and the financial report.
     */
    suspend fun getFinancialReports(factList: List<Item>): List<FinancialReport> {
        //TODO: Maybe there is a more efficient way to do this
        val map = splitByDate(factList)

        val reportMap = map.mapValues { (date, list) -> createFinancialReportFromList(date, list) }

        return reportMap.values.toList()
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
        val currency: String = list.firstNotNullOf { it.unitRef }
        val entityIdentifier: String = list.firstNotNullOf { it.entityIdentifier }
        val entityScheme: String = list.firstNotNullOf { it.entityScheme }

        // Loop over elements and their assignment to the appropriate report component
        list.forEach { item ->
            val type = item.type

            if (type == TYPE.DATE_ITEM.str || type == TYPE.STRING_ITEM.str) {
                ifrsGeneralInformation.setValue(item)
            }

            if (type == TYPE.MONETARY_ITEM.str || type == TYPE.PER_SHARE_ITEM.str) {
                ifrsStatementOfFinancialPositionCurrentNotCurrent.setValue(item)
                ifrsStatementOfFinancialPositionOrderOfLiquidity.setValue(item)
            }

        }

        return FinancialReport(
            ifrsGeneralInformation,
            ifrsStatementOfFinancialPositionCurrentNotCurrent,
            ifrsStatementOfFinancialPositionOrderOfLiquidity,
            date,
            currency,
            entityIdentifier,
            entityScheme
        )
    }

}