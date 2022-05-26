package com.mystockdata.financialreportservice.financialreportevent

import com.mystockdata.financialreportservice.financialreports.FinancialReportService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class FinancialReportEventConsumerConfig (@Autowired val financialReportService: FinancialReportService){
    private val scope = CoroutineScope(Dispatchers.Default)
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FinancialReportEventConsumerConfig::class.java)
    }

    @Bean
    fun financialReportEventConsumer(): Consumer<FinancialReportEvent> = Consumer<FinancialReportEvent> {
        logger.debug("Consumer received $it")
        scope.launch {
            financialReportService.handleEvent(it)
        }
    }
}