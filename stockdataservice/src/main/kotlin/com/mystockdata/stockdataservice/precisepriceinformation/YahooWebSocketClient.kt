package com.mystockdata.stockdataservice.precisepriceinformation

import Yahoo
import com.google.protobuf.ByteString
import com.google.protobuf.StringValue.parseFrom
import com.google.protobuf.kotlin.toByteString
import com.mystockdata.stockdataservice.utility.epochMilliToLocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URI
import java.nio.ByteBuffer
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class YahooWebSocketClient(
    var initialMsg: String? = null,
    serverUri: URI = URI.create("wss://streamer.finance.yahoo.com/"),
    private val base64Decoder: Base64.Decoder = Base64.getDecoder(),
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss.SSS"),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    override val flow: MutableSharedFlow<PrecisePriceInformation> = MutableSharedFlow<PrecisePriceInformation>()
) : WebSocketClient(serverUri), PrecisePriceInformationProvider {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(YahooWebSocketClient::class.java)
    }

    override fun establishConnection(symbols: List<String>) {
        initialMsg = prepareSymbolString(symbols)
        connect()
    }

    override fun setWatchedSecurities(symbols: List<String>) {
        send(prepareSymbolString(symbols))
    }

    private fun prepareSymbolString(symbols: List<String>): String {
        val symbolString = symbols.foldIndexed("") { idx, acc, str ->
            acc.plus("\"$str\"".plus(if (idx < symbols.size - 1) "," else ""))
        }
        return "{\"subscribe\":[$symbolString]}"
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        if (initialMsg != null) {
            send(initialMsg)
        }
        logger.debug("Connection open ${handshakedata.toString()}")
    }

    override fun onMessage(message: String?) {
        if (message == null) return
        scope.launch {

            val decodedByteString: ByteString = base64Decoder.decode(message).toByteString()
            val parsed = Yahoo.Yaticker.parseFrom(decodedByteString)
            logger.trace("Retrieved:$parsed")
            flow.emit(
                PrecisePriceInformation(
                    time = Instant.ofEpochMilli(parsed.time),
                    symbol = parsed.id,
                    exchange = parsed.exchange,
                    price = parsed.price.toBigDecimal(),
                    //dayVolume = parsed.dayVolume,
                    marketHours = parsed.marketHours.name
                )
            )
        }

    }

    override fun onMessage(message: ByteBuffer?) {
        val msg = parseFrom(message)
        logger.debug("received ByteBuffer $message $msg")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        logger.debug("WebSocket connection closed Code: $code Reason: $reason Remote: $remote")
    }

    override fun onError(exception: Exception?) {
        logger.debug("An Error occurred: $exception")
    }

}


/*
@Component
class YahooStockDataRetriever(
    @Autowired private val yahooWebSocketHandler: YahooWebSocketHandler,
    @Autowired private val websocketClient: WebSocketClient
){
    fun start(){
        val uri = URI.create("wss://streamer.finance.yahoo.com/")
        websocketClient.execute(uri, yahooWebSocketHandler).block()

    }
    fun stop(){

    }
}

fun main(){
    val webSocketConfig = WebSocketConfig()
    val yahooStockDataRetriever = YahooStockDataRetriever(webSocketConfig.yahooWebSocketHandler(), webSocketConfig.webSocketClient())
    yahooStockDataRetriever.start()
}


@Configuration
class WebSocketConfig {
    @Bean
    fun handlerMapping(): HandlerMapping {
        val map = mapOf("/path" to YahooWebSocketHandler())
        val order = -1
        return SimpleUrlHandlerMapping(map, order)
    }

    @Bean
    fun yahooWebSocketHandler(): YahooWebSocketHandler = YahooWebSocketHandler()

    @Bean
    fun webSocketClient(): WebSocketClient = ReactorNettyWebSocketClient()

    @Bean
    fun protobufHttpMessageConverter(): ProtobufHttpMessageConverter = ProtobufHttpMessageConverter()

}


@EnableWebFlux
class YahooWebSocketHandler(
    val message: String = "{\"subscribe\":[\"AMC\", \"TSLA\"]}"
) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        return session.send(
            Mono.just(session.textMessage(message))
        ).thenMany(
            session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .log()
        ).then()
    }
}
 */