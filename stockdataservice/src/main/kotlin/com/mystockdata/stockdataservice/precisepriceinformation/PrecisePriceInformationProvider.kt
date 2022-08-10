package com.mystockdata.stockdataservice.precisepriceinformation

import com.mystockdata.stockdataservice.company.Symbol
import kotlinx.coroutines.flow.SharedFlow

/**
 * Interface to provide abstraction from the collection of PrecisePriceInformation
 */
interface PrecisePriceInformationProvider {
    val flow: SharedFlow<PrecisePriceInformation>

    /**
     * Connect to the datasource and subscribe to the desired symbols.
     * @param symbols List containing symbols of the desired securities on the correct exchange.
     */
    fun establishConnection(symbols: Set<Symbol>)

    /**
     * Change the subscribed symbols to the ones provided.
     * @param symbols List containing symbols of the desired securities on the correct exchange.
     */
    fun setWatchedSecurities(symbols: Set<Symbol>)

    /**
     * Closes the connection to the datasource.
     */
    fun close()

}