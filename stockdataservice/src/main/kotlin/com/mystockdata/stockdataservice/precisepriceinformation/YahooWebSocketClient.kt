package com.mystockdata.stockdataservice.precisepriceinformation

import Yahoo
import com.google.protobuf.ByteString
import com.google.protobuf.StringValue.parseFrom
import com.google.protobuf.kotlin.toByteString
import com.mystockdata.stockdataservice.company.Symbol
import com.mystockdata.stockdataservice.company.SymbolConstants.YAHOO_FINANCE_SYSTEM
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
import java.util.*

@Component
class YahooWebSocketClient(
    var initialMsg: String? = null,
    serverUri: URI = URI.create("wss://streamer.finance.yahoo.com/"),
    private val base64Decoder: Base64.Decoder = Base64.getDecoder(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    override val flow: MutableSharedFlow<PrecisePriceInformation> = MutableSharedFlow()
) : WebSocketClient(serverUri), PrecisePriceInformationProvider {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(YahooWebSocketClient::class.java)
    }

    override fun establishConnection(symbols: Set<Symbol>) {
        val yahooSymbols = toYahooSymbolStrings(symbols)
        initialMsg = prepareSymbolString(yahooSymbols)
        connect()
    }

    override fun setWatchedSecurities(symbols: Set<Symbol>) {
        val yahooSymbols = toYahooSymbolStrings(symbols)
        send(prepareSymbolString(yahooSymbols))
    }

    private fun toYahooSymbolStrings(symbols: Set<Symbol>): Set<String> {
        return symbols.filter {
            if(it.system == YAHOO_FINANCE_SYSTEM){
                true
            }else{
                logger.debug("Unable to retrieve $it since it is not a Yahoo Finance stock symbol.")
                false
            }
        }.map { it.symbol }.toSet()
    }

    private fun prepareSymbolString(symbols: Set<String>): String {
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
            logger.trace("Retrieved: ${parsed.id} ${parsed.price}")
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