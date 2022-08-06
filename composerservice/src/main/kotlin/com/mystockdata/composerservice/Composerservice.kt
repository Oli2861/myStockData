package com.mystockdata.composerservice

import com.mystockdata.composerservice.company.Company
import com.mystockdata.composerservice.company.CompanyService
import com.mystockdata.composerservice.company.findCompanyBySymbol
import com.mystockdata.composerservice.csv.CSVEntryConstants
import com.mystockdata.composerservice.csv.MissingValueHandlingStrategy
import com.mystockdata.composerservice.csv.PriceEntry
import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilder
import com.mystockdata.composerservice.financialreport.FinancialReport
import com.mystockdata.composerservice.financialreport.FinancialReportServiceAdapter
import com.mystockdata.composerservice.financialreport.IFRSTAGS
import com.mystockdata.composerservice.financialreport.toFactMap
import com.mystockdata.composerservice.indicator.*
import com.mystockdata.composerservice.stockdata.AggregatedPriceInformationResponse
import com.mystockdata.composerservice.stockdata.StockDataServiceAdapter
import com.mystockdata.composerservice.stockdata.toCSVEntryList
import com.mystockdata.financialreportservice.financialreports.Fact
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
        indicators: List<RequestedIndicator>,
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
        indicators: List<RequestedIndicator>,
        missingValueHandlingStrategy: MissingValueHandlingStrategy,
        refreshData: Boolean = false,
        factTags: List<String>?
    ): InputStreamResource? {

        // Get companies of the provided lei.
        val companies = companyService.getCompanies(leis).toSet()

        // Retrieve aggregated price information for the symbols of the lei.
        val symbols: List<String> = companies.map { it.getSymbolNames() }.flatten()
        val (reports, aggregatedPriceInformation) = retrieveData(leis, start, symbols, end)

        // Can not build a csv if the required data is not present.
        if (aggregatedPriceInformation.isNullOrEmpty()) return null

        // Create base-csv.
        val csv = TimeIndexedCSVBuilder(aggregatedPriceInformation.toCSVEntryList(), missingValueHandlingStrategy)

        // Calculate indicators based on close price columns.
        val closePriceColumnNames = csv.csvHeader.subList(1, csv.csvHeader.size)
            .filter { it.contains(CSVEntryConstants.CLOSE_COLUMN_NAME_PREFIX) }

        // Add indicators.
        addIndicators(indicators, csv, closePriceColumnNames, companies, reports)

        // Add facts.
        if (!factTags.isNullOrEmpty() && !reports.isNullOrEmpty()) {
            addFacts(factTags, reports, csv, companies)
        }

        return csv.buildCSV()
    }

    private suspend fun retrieveData(
        leis: List<String>,
        start: Instant,
        symbols: List<String>,
        end: Instant
    ): Pair<List<FinancialReport>?, List<AggregatedPriceInformationResponse>?> = coroutineScope {
        val financialReports = async { getFinancialReports(leis, start, end) }
        val aggregatedPriceInformationResponse = async { getAggregatedPriceInformation(symbols, start, end) }
        return@coroutineScope Pair(financialReports.await(), aggregatedPriceInformationResponse.await())
    }

    private suspend fun getFinancialReports(leis: List<String>, start: Instant, end: Instant): List<FinancialReport>? {
        return if (leis.isEmpty()) {
            logger.debug("No leis specified.")
            null
        } else {
            val reports = financialReportServiceAdapter.getFinancialReports(leis, start, end).toList()
            reports.ifEmpty {
                logger.debug("Could not retrieve financial reports.")
                null
            }
        }
    }

    private suspend fun getAggregatedPriceInformation(
        symbols: List<String>,
        start: Instant,
        end: Instant
    ): List<AggregatedPriceInformationResponse>? {
        return if (symbols.isEmpty()) {
            logger.debug("No symbols found.")
            null
        } else {
            val aggregatedPriceInformation: List<AggregatedPriceInformationResponse> =
                stockDataServiceAdapter.getAggregatedPriceInformation(symbols.toSet(), start, end).toList()
            aggregatedPriceInformation.ifEmpty {
                logger.debug("Could not retrieve aggregated price information.")
                null
            }
        }
    }

    /**
     * Adds the requested indicators.
     * @param indicators a list containing the indicators to be calculated and added.
     * @param csv the TimeIndexedCSVBuilder to add the indicators to.
     * @param relevantColumnNames a list containing the name of columns indicators are calculated for.
     * @param companies set containing the companies of the requested lei.
     * @param reports a list containing the retrieved financial reports.
     */
    private suspend fun addIndicators(
        indicators: List<RequestedIndicator>,
        csv: TimeIndexedCSVBuilder,
        relevantColumnNames: List<String>,
        companies: Set<Company>,
        reports: List<FinancialReport>?
    ) {
        // Split by indicator type.
        val (technicalIndicators, fundamentalIndicators) = indicators.splitByType()
        // Add technical indicators.
        addTechnicalIndicators(csv, relevantColumnNames, technicalIndicators)
        // Add fundamental indicators if reports are present.
        if (reports != null) {
            addFundamentalIndicators(companies, csv, relevantColumnNames, fundamentalIndicators, reports)
        }
    }

    private suspend fun addFundamentalIndicators(
        companies: Set<Company>,
        csv: TimeIndexedCSVBuilder,
        relevantCols: List<String>,
        fundamentalIndicators: List<IndicatorName>,
        reports: List<FinancialReport>
    ) {
        fundamentalIndicators.forEach { indicatorName ->
            when (indicatorName) {
                IndicatorName.PE_RATIO -> addPERatio(indicatorName, reports, csv, relevantCols, companies)
                else -> logger.debug("Unknown Indicator ${indicatorName.indicatorName}")
            }
        }

    }

    private fun addPERatio(
        indicatorName: IndicatorName,
        reports: List<FinancialReport>,
        csv: TimeIndexedCSVBuilder,
        relevantCols: List<String>,
        companies: Set<Company>
    ) {
        // Mapping legal entity identifiers to facts matching the ifrs-full:DilutedEarningsLossPerShare tag.
        val entityIdentifierEPSMap: Map<String, Set<Fact>> = reports.toFactMap(IFRSTAGS.DELUTED_EPS)
        // Mapping symbols to legal entity identifiers.
        val symbolLeiMap = mutableMapOf<String, String>()

        csv.addIndicator(indicatorName, relevantCols, true) { priceEntries ->
            return@addIndicator priceEntries.map { priceEntry ->
                val symbol = priceEntry.symbol
                // Looking up legal entity identifiers in the map is more performant than searching them among all companies every time.
                val lei = if (symbolLeiMap[symbol] == null) {
                    val entityIdentifier = companies.findCompanyBySymbol(symbol)?.lei ?: return@map Indicator(
                        priceEntry.time,
                        priceEntry.symbol,
                        IndicatorName.PE_RATIO,
                        IndicatorType.FUNDAMENTAL_INDICATOR,
                        null
                    )
                    symbolLeiMap[symbol] = entityIdentifier
                    entityIdentifier
                } else {
                    symbolLeiMap[symbol]
                }
                // Searching for the ifrs-full:DilutedEarningsLossPerShare fact covering the time of the priceEntry.
                val facts = entityIdentifierEPSMap[lei]
                val matchingFact = facts?.firstOrNull { fact ->
                    fact.start != null &&
                            fact.end != null &&
                            priceEntry.time.isAfter(fact.start?.toInstant()) &&
                            priceEntry.time.isBefore(fact.end?.toInstant())
                }
                val eps = matchingFact?.parseValueToBigDecimal()
                // Price to earnings ratio = share price / earnings per share.
                val per = if (eps != null) priceEntry.price?.div(eps) else null
                // Return indicator which will be added by the csv builder.
                Indicator(
                    priceEntry.time, priceEntry.symbol, IndicatorName.PE_RATIO,
                    IndicatorType.FUNDAMENTAL_INDICATOR, per
                )
            }
        }
    }

    private fun addFacts(
        ifrsFactTag: List<String>,
        reports: List<FinancialReport>,
        csv: TimeIndexedCSVBuilder,
        companies: Set<Company>,
        useSymbolAsColumnName: Boolean = true
    ) = ifrsFactTag.forEach { factTag ->
        addFact(reports, csv, factTag, companies, useSymbolAsColumnName)
    }

    private fun addFact(
        reports: List<FinancialReport>,
        csv: TimeIndexedCSVBuilder,
        ifrsFactTag: String,
        companies: Set<Company>,
        useSymbolAsColumnName: Boolean
    ) {
        val epsCols: List<PriceEntry> = reports.map { report ->
            val company = companies.firstOrNull { company -> company.lei == report.entityIdentifier }
            findFactAndParseToCSVEntry(report, ifrsFactTag, company, useSymbolAsColumnName)
        }.flatten()
        csv.addColumns(epsCols)
    }

    private fun findFactAndParseToCSVEntry(
        financialReport: FinancialReport,
        ifrsTag: String,
        company: Company?,
        useSymbolAsColumnName: Boolean
    ): List<PriceEntry> {
        // Find fact
        val factList = financialReport.findFacts(ifrsTag)
        val symbolOfCompanyOrLei = company?.getSymbolNames()?.firstOrNull() ?: financialReport.entityIdentifier
        return factList.mapNotNull { it.parseToCSVEntry(if (useSymbolAsColumnName) symbolOfCompanyOrLei else financialReport.entityIdentifier) }
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
     * Add the SMA Indicator the csv file.
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
