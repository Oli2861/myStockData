package com.mystockdata.composerservice

import com.mystockdata.composerservice.csv.MissingValueHandlingStrategy
import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilder
import com.mystockdata.composerservice.financialreport.FinancialReportServiceAdapter
import com.mystockdata.composerservice.stockdata.StockDataServiceAdapter
import com.mystockdata.composerservice.stockdata.toCSVEntryList
import com.mystockdata.composerservice.technicalindicators.smaForAllOfASymbol
import kotlinx.coroutines.flow.toList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class Composerservice(
    @Autowired private val stockDataServiceAdapter: StockDataServiceAdapter,
    @Autowired private val financialReportServiceAdapter: FinancialReportServiceAdapter
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Composerservice::class.java)

    }

    suspend fun getPrecisePriceCSV(
        symbols: List<String>,
        start: Instant,
        end: Instant,
        indicatorNames: List<IndicatorName>,
        missingValueHandlingStrategy: MissingValueHandlingStrategy
    ): InputStreamResource {
        val precisePriceInformation = stockDataServiceAdapter.getPrecisePriceInformation(symbols, start, end).toList()
        val csv = TimeIndexedCSVBuilder(precisePriceInformation.toCSVEntryList(), missingValueHandlingStrategy)
        val headerWithoutTimeCol = csv.csvHeader.subList(1, csv.csvHeader.size)
        addIndicatorsToCSV(csv, indicatorNames, headerWithoutTimeCol)
        return csv.buildCSV()
    }

    suspend fun getAggregatedPriceCSV(
        symbols: List<String>,
        start: Instant,
        end: Instant,
        indicatorNames: List<IndicatorName>,
        missingValueHandlingStrategy: MissingValueHandlingStrategy
    ): InputStreamResource {

        val aggregatedPriceInformation =
            stockDataServiceAdapter.getAggregatedPriceInformation(symbols, start, end).toList()

        val csv =
            TimeIndexedCSVBuilder(
                aggregatedPriceInformation.toCSVEntryList(),
                missingValueHandlingStrategy
            )

        val headerWithoutTimeCol = csv.csvHeader.subList(1, csv.csvHeader.size)
        addIndicatorsToCSV(csv, indicatorNames, headerWithoutTimeCol)

        return csv.buildCSV()
    }

    private fun addIndicatorsToCSV(
        csv: TimeIndexedCSVBuilder,
        indicatorNames: List<IndicatorName>,
        relevantCols: List<String>
    ) {
        indicatorNames.forEach { name ->
           when(name){
               IndicatorName.SMA -> addSMAIndicator(csv, name, relevantCols)
               IndicatorName.RSI -> addRSIIndicator(csv, name, relevantCols)
               IndicatorName.MACD -> addMACDIndicator(csv, name, relevantCols)
               else -> logger.debug("Unknown Indicator ${name.indicatorName}")
           }
        }
    }

    private fun addMACDIndicator(csv: TimeIndexedCSVBuilder, name: IndicatorName, relevantCols: List<String>) {
        // TODO
    }

    private fun addRSIIndicator(csv: TimeIndexedCSVBuilder, name: IndicatorName, relevantCols: List<String>) {
        // TODO
    }

    private fun addSMAIndicator(csv: TimeIndexedCSVBuilder, name: IndicatorName, relevantCols: List<String>) {
        csv.addIndicator(name, relevantCols) { entries ->
            return@addIndicator smaForAllOfASymbol(entries)
        }
    }



}
