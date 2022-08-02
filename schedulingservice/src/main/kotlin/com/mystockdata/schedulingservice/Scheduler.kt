package com.mystockdata.schedulingservice

import com.mystockdata.schedulingservice.financialreportevent.FinancialReportEvent
import com.mystockdata.schedulingservice.financialreportevent.FinancialReportEventConfig
import com.mystockdata.schedulingservice.financialreportevent.FinancialReportEventType
import com.mystockdata.schedulingservice.stockdataevent.StockDataEvent
import com.mystockdata.schedulingservice.stockdataevent.StockDataEventConsumerConfig
import com.mystockdata.schedulingservice.stockdataevent.StockDataEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class Scheduler(
    @Autowired val financialReportEventConfig: FinancialReportEventConfig,
    @Autowired val stockDataEventConsumerConfig: StockDataEventConsumerConfig
) {
    val scope = CoroutineScope(Dispatchers.Unconfined)
    val financialReportEventFlow: MutableSharedFlow<FinancialReportEvent> =
        financialReportEventConfig.financialReportEventFlow
    val stockDataEventConsumerFlow: MutableSharedFlow<StockDataEvent> = stockDataEventConsumerConfig.stockDataEventFlow

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Scheduler::class.java)
    }

    // Every monday at 8 am: 0 0 8 * * MON
    @Scheduled(cron = "0 0 8 * * MON")
    fun triggerCollectFinancialReportsEvent() = scope.launch {
        val event = FinancialReportEvent(
            "${Date().time}_REFRESH_DATA",
            FinancialReportEventType.REFRESH_DATA
        )
        logger.debug("Sent event $event")
        financialReportEventFlow.emit(event)
    }

    // Every day 10 pm
    @Scheduled(cron = "0 0 22 * * *")
    fun triggerStockDataTest() = scope.launch {
        val event = StockDataEvent(
            "${Date().time}_RETRIEVE_AGGREGATED",
            StockDataEventType.RETRIEVE_AGGREGATED,
            Instant.now().minus(1, ChronoUnit.DAYS),
            Instant.now()
        )
        logger.debug("Sent event $event")
        stockDataEventConsumerFlow.emit(event)
    }

/*
    // Once per Month
    @Scheduled(cron = "0 1 0 1 * *")
    fun triggerAggregatedHistoricalStockDataTest() = scope.launch {
        val event = StockDataEvent(
            "${Date().time}_RETRIEVE_HISTORIC_AGGREGATED_OHLCV",
            StockDataEventType.RETRIEVE_HISTORIC_AGGREGATED_OHLCV
        )
        logger.debug("Sent event $event")
        stockDataEventConsumerFlow.emit(event)
    }
*/

}
/*
    Spring Scheduled (https://stackoverflow.com/questions/30887822/spring-cron-vs-normal-cron):
    1 2 3 4 5 6 Index
    - - - - - -
    * * * * * * command to be executed
    - - - - - -
    | | | | | |
    | | | | | ------- Day of week (MON - SUN)
    | | | | --------- Month (1 - 12)
    | | | ----------- Day of month (1 - 31)
    | |-------------- Hour (0 - 23)
    | --------------- Minute (0 - 59)
    ----------------- Seconds (0 - 59)
 */