package com.mystockdata.schedulingservice.financialreportevent

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.util.function.Supplier

/**
 * Configures a supplier, used to emit FinancialReportEvents.
 * To the financialReportEventFlow emitted events will be published to the bound channel,
 * specified in the application.yml file.
 * @property financialReportEventFlow MutableSharedFlow, emitted FinancialReportEvent will be collected by the supplier which will publish an event to the channel.
 */
@Configuration
class FinancialReportEventConfig {
    val financialReportEventFlow: MutableSharedFlow<FinancialReportEvent> = MutableSharedFlow()

    @Bean
    fun financialReportEventSupplier(): Supplier<Flux<FinancialReportEvent>>{
        return Supplier{
            financialReportEventFlow.asFlux()
        }
    }

}