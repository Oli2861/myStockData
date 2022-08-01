package com.mystockdata.composerservice

import com.mystockdata.composerservice.csv.CsvEntry
import com.mystockdata.composerservice.csv.MissingValueHandlingStrategy
import com.mystockdata.composerservice.csv.TimeIndexedCSVBuilder
import com.mystockdata.composerservice.financialreport.FinancialReportServiceAdapter
import com.mystockdata.composerservice.stockdata.StockDataServiceAdapter
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

    suspend fun test() {
        val list = listOf("VOW3.DE", "SOW.DE", "SAP.DE", "AMC", "TSLA")
        val precisePriceInformation = stockDataServiceAdapter.getPrecisePriceInformation(list).toList()
        logger.debug(precisePriceInformation.toString())
    }


    suspend fun getStockData(
        symbols: List<String>,
        start: Instant,
        end: Instant,
        indicatorName: List<IndicatorName>,
        missingValueHandlingStrategy: MissingValueHandlingStrategy
    ): InputStreamResource {
        val aggregatedPriceInformation =
            stockDataServiceAdapter.getAggregatedPriceInformation(symbols, start, end).toList()
        val csv =
            TimeIndexedCSVBuilder(
                aggregatedPriceInformation.map { CsvEntry(it.time, it.symbol, it.close) },
                missingValueHandlingStrategy
            )

        val closePriceCols = csv.csvHeader.subList(1, csv.csvHeader.size)

        indicatorName.forEach { name ->
            when (name) {
                IndicatorName.SMA -> {
                    csv.addIndicator(name, closePriceCols) { entries ->
                        return@addIndicator smaForAllOfASymbol(entries)
                    }
                }
                else -> {
                    logger.debug("Unknown indicator $name")
                }
            }

        }

        return csv.buildCSV()
    }

}