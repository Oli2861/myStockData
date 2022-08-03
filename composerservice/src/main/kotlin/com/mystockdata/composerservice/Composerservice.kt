package com.mystockdata.composerservice

import com.mystockdata.composerservice.company.Company
import com.mystockdata.composerservice.company.CompanyService
import com.mystockdata.composerservice.csv.*
import com.mystockdata.composerservice.financialreport.FinancialReport
import com.mystockdata.composerservice.financialreport.FinancialReportServiceAdapter
import com.mystockdata.composerservice.financialreport.IFRSTAGS
import com.mystockdata.composerservice.indicator.IndicatorName
import com.mystockdata.composerservice.indicator.IndicatorType
import com.mystockdata.composerservice.indicator.smaForAllOfASymbol
import com.mystockdata.composerservice.stockdata.AggregatedPriceInformationResponse
import com.mystockdata.composerservice.stockdata.StockDataServiceAdapter
import com.mystockdata.composerservice.stockdata.toCSVEntryList
import com.mystockdata.financialreportservice.financialreports.MonetaryFact
import com.mystockdata.financialreportservice.financialreports.NumericFact
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class Composerservice(
    @Autowired private val stockDataServiceAdapter: StockDataServiceAdapter,
    @Autowired private val financialReportServiceAdapter: FinancialReportServiceAdapter,
    @Autowired private val companyService: CompanyService
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Composerservice::class.java)

    }

    suspend fun getPrecisePriceCSV(
        symbols: List<String>,
        start: Instant,
        end: Instant,
        indicators: List<Pair<IndicatorName, IndicatorType>>,
        missingValueHandlingStrategy: MissingValueHandlingStrategy
    ): InputStreamResource {
        val precisePriceInformation = stockDataServiceAdapter.getPrecisePriceInformation(symbols, start, end).toList()
        val csv = TimeIndexedCSVBuilder(precisePriceInformation.toCSVEntryList(), missingValueHandlingStrategy)
        val headerWithoutTimeCol = csv.csvHeader.subList(1, csv.csvHeader.size)
        val technicalIndicators = indicators.filter { (_, type) -> type == IndicatorType.TECHNICAL_INDICATOR }
            .map { (name, _) -> name }
        addTechnicalIndicators(csv, headerWithoutTimeCol, technicalIndicators)
        return csv.buildCSV()
    }

    suspend fun getAggregatedPriceCSV(
        leis: List<String>,
        start: Instant,
        end: Instant,
        indicators: List<Pair<IndicatorName, IndicatorType>>,
        missingValueHandlingStrategy: MissingValueHandlingStrategy
    ): InputStreamResource? {

        // Get companies of the provided lei.
        val companies = companyService.getCompanies(leis).toSet()

        // Retrieve aggregated price information for the symbols of the lei.
        val symbols: List<String> = companies.map { it.getSymbolNames() }.flatten()
        val aggregatedPriceInformation: List<AggregatedPriceInformationResponse> =
            getAggregatedPriceInformation(symbols, start, end) ?: return null

        // Create base-csv
        val csv = TimeIndexedCSVBuilder(aggregatedPriceInformation.toCSVEntryList(), missingValueHandlingStrategy)

        // Retrieve reports
        val reports =
            financialReportServiceAdapter.getFinancialReports(companies.map { it.lei }.toList(), start, end).toList()


        val technicalIndicators = indicators.filter { (_, type) -> type == IndicatorType.TECHNICAL_INDICATOR }
            .map { (name, _) -> name }
        val fundamentalIndicators = indicators.filter { (_, type) -> type == IndicatorType.FUNDAMENTAL_INDICATOR }
            .map { (name, _) -> name }

        val priceColNames = csv.csvHeader.subList(1, csv.csvHeader.size)
            .filter { it.contains(CSVEntryConstants.CLOSE_COLUMN_NAME_PREFIX) }

        addTechnicalIndicators(csv, priceColNames, technicalIndicators)

        // Match retrieved price information back to company
        addFundamentalIndicators(
            companies,
            csv,
            priceColNames,
            fundamentalIndicators,
            reports
        )

        val equityCols = csv.csvHeader.filter { it.contains("equity") }

        return csv.buildCSV()
    }


    private suspend fun getAggregatedPriceInformation(
        symbols: List<String>,
        start: Instant,
        end: Instant
    ): List<AggregatedPriceInformationResponse>? {
        val aggregatedPriceInformation: List<AggregatedPriceInformationResponse> =
            stockDataServiceAdapter.getAggregatedPriceInformation(symbols, start, end).toList()
        return aggregatedPriceInformation.ifEmpty {
            logger.debug("Could not retrieve aggregated price information.")
            null
        }
    }

    private suspend fun addFundamentalIndicators(
        companies: Set<Company>,
        csv: TimeIndexedCSVBuilder,
        priceColNames: List<String>,
        fundamentalIndicators: List<IndicatorName>,
        reports: List<FinancialReport>,
        useSymbolAsColumnName: Boolean = true
    ) {
        fundamentalIndicators.forEach { fundamentalIndicators ->
            when(fundamentalIndicators){
                IndicatorName.EPS -> addFact(reports, csv, IFRSTAGS.DELUTED_EPS, companies, useSymbolAsColumnName)
                IndicatorName.PE_RATIO -> addPERatio(reports, csv, companies, priceColNames)
            }
        }

    }

    private fun addFact(
        reports: List<FinancialReport>,
        csv: TimeIndexedCSVBuilder,
        tag: String,
        companies: Set<Company>,
        useSymbolAsColumnName: Boolean
    ) {
        val epsCols: List<CsvEntry> = reports.mapNotNull { report ->
            findFactAndParseToCSVEntry(report, tag, companies.firstOrNull { company -> company.lei == report.entityIdentifier }, useSymbolAsColumnName)
        }
        csv.addColumns(epsCols)
    }

    private fun findFact(financialReport: FinancialReport, ifrsTag: String) = financialReport.factList.firstOrNull { fact -> fact.ifrsTag == ifrsTag }

    private fun findFactAndParseToCSVEntry(
        financialReport: FinancialReport,
        ifrsTag: String,
        company: Company?,
        useSymbolAsColumnName: Boolean
    ): CsvEntry? {
        // Find fact
        val fact =  findFact(financialReport, ifrsTag) ?: return null
        // Parse to CSV entry
        return if (fact is MonetaryFact && fact.end != null) {
            CsvEntry(fact.end!!.toInstant(), "${ifrsTag}_${if (useSymbolAsColumnName) company?.getSymbolNames()?.firstOrNull() ?: financialReport.entityIdentifier else financialReport.entityIdentifier}", fact.value)
        } else if (fact is NumericFact && fact.end != null) {
            CsvEntry(fact.end!!.toInstant(), "${ifrsTag}_${if (useSymbolAsColumnName) company?.getSymbolNames()?.firstOrNull() ?: financialReport.entityIdentifier else financialReport.entityIdentifier}", fact.value)
        } else {
            logger.debug("Only monetary and numeric facts are supported. $fact")
            null
        }

    }


    private fun addPERatio(
        csv: List<FinancialReport>,
        name: TimeIndexedCSVBuilder,
        relevantCols: Set<Company>,
        equityData: List<String>
    ) {

    }

    // Technical indicators

    /**
     * Adds the technical indicators to the csv file.
     * @param csv the csv.
     * @param columnNames list of columns to calculate the technical indicators for.
     * @param indicatorNames list indicator names representing the indicators to be calculated.
     */
    private fun addTechnicalIndicators(
        csv: TimeIndexedCSVBuilder,
        columnNames: List<String>,
        indicatorNames: List<IndicatorName>
    ) {
        indicatorNames.forEach { name ->
            when (name) {
                IndicatorName.SMA -> addSMAIndicator(csv, name, columnNames)
                else -> logger.debug("Unknown Indicator ${name.indicatorName}")
            }
        }
    }

    /**
     * Add the SMA Indicator the the csv file.
     * @param csv to add the indicator to.
     * @param name name of the indicator to be added.
     * @param relevantCols columns to calculate the indicator for.
     */
    private fun addSMAIndicator(csv: TimeIndexedCSVBuilder, name: IndicatorName, relevantCols: List<String>) {
        csv.addIndicator(name, relevantCols) { column ->
            return@addIndicator smaForAllOfASymbol(column)
        }
    }


}
