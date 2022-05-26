package com.mystockdata.stockdataservice.stockdataevent

import com.mystockdata.stockdataservice.StockDataService
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
class StockDataEventConsumerConfig(@Autowired val stockDataService: StockDataService) {
    private val scope = CoroutineScope(Dispatchers.Default)
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StockDataEventConsumerConfig::class.java)
    }

    @Bean
    fun stockDataEventConsumer(): Consumer<StockDataEvent> = Consumer<StockDataEvent>{
        logger.debug("Consumer received $it")
        scope.launch {
            stockDataService.handleEvent(it)
        }
    }

}