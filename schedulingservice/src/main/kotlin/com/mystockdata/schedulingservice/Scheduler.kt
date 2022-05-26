package com.mystockdata.schedulingservice

import com.mystockdata.schedulingservice.financialreportevent.FinancialReportEvent
import com.mystockdata.schedulingservice.financialreportevent.FinancialReportEventConfig
import com.mystockdata.schedulingservice.financialreportevent.FuelStationEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

@Component
class Scheduler(
    @Autowired val financialReportEventConfig: FinancialReportEventConfig,

) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Scheduler::class.java)
    }
    val scope = CoroutineScope(Dispatchers.Unconfined)
    val financialReportEventFlow: MutableSharedFlow<FinancialReportEvent> =
        financialReportEventConfig.financialReportEventFlow

    // @Scheduled(cron = "* * * * 1")
    @Scheduled(cron = "2 * * * * *")
    fun triggerCollectFinancialReportsEvent() = scope.launch {
        val event = FinancialReportEvent("${Date()}_REFRESH_DATA", FuelStationEventType.REFRESH_DATA)
        logger.debug("Sent event $event")
        financialReportEventFlow.emit(event)
    }
}