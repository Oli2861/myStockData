package com.mystockdata.stockdataservice.livestockdata


import com.google.protobuf.ByteString
import com.google.protobuf.StringValue.parseFrom
import com.google.protobuf.kotlin.toByteString
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.net.URI
import java.nio.ByteBuffer
import java.util.*

class YahooWebSocketClient(
    serverUri: URI,
    private val initialRequest: String,
    private val base64Decoder: Base64.Decoder = Base64.getDecoder(),
    private val protoParser: YahooProtoParser = YahooProtoParser()
) : WebSocketClient(serverUri) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(YahooWebSocketClient::class.java)
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        send(initialRequest)
        logger.debug("Connection open ${handshakedata.toString()}")
    }

    override fun onMessage(message: String?) {
        if (message == null) return
        val decodedByteString: ByteString = base64Decoder.decode(message).toByteString()
        val parsed = protoParser.parseProto(decodedByteString)
        logger.debug("Parsed Info:\nType: ${parsed?.javaClass}\nContent:$parsed")
    }


    override fun onMessage(message: ByteBuffer?) {
        val msg = parseFrom(message)
        logger.debug(msg.value)
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