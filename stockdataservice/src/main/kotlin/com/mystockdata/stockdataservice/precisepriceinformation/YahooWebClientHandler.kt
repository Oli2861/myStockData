package com.mystockdata.stockdataservice.precisepriceinformation

import org.springframework.stereotype.Component

@Component
class YahooWebClientHandler() : PreciseInformationProvider {
    private val yahooWebSocketClient = YahooWebSocketClient.getInstance()

    override fun establishConnection(symbols: List<String>) {
        yahooWebSocketClient.initialMsg = prepareSymbolString(symbols)
        yahooWebSocketClient.connect()
    }

    override fun setWatchedSecurities(symbols: List<String>) {
        yahooWebSocketClient.send(prepareSymbolString(symbols))
    }

    override fun closeConnection() {
        yahooWebSocketClient.close()
    }

}

private fun prepareSymbolString(symbols: List<String>): String {
    val symbolString = symbols.foldIndexed("") { idx, acc, str ->
        acc.plus("\"$str\"".plus(if (idx < symbols.size - 1) "," else ""))
    }
    return "{\"subscribe\":[$symbolString]}"
}