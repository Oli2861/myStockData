package com.mystockdata.schedulingservice.stockdataevent

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.util.function.Supplier

/**
 * Configures a supplier, used to emit StockDataEvents.
 * To the stockDataEventFlow emitted events will be published to the bound channel,
 * specified in the application.yml file.
 * @property stockDataEventFlow MutableSharedFlow, emitted StockDataEvent will be collected by the supplier which will publish an event to the channel.
 */

@Configuration
class StockDataEventConsumerConfig {
    val stockDataEventFlow: MutableSharedFlow<StockDataEvent> = MutableSharedFlow()

    @Bean
    fun stockDataEventSupplier(): Supplier<Flux<StockDataEvent>> {
        return Supplier {
            stockDataEventFlow.asFlux()
        }
    }

}