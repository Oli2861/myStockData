package com.mystockdata.stockdataservice.precisepriceinformation

import kotlinx.coroutines.flow.Flow

/**
 * Interface to provide abstraction from the collection of PrecisePriceInformation
 */
interface PrecisePriceInformationProvider {
    val flow: Flow<PrecisePriceInformation>
    /**
     * Connect to the datasource and subscribe to the desired symbols.
     * @param symbols List containing symbols of the desired securities on the correct exchange.
     */
    fun establishConnection(symbols: List<String>)

    /**
     * Change the subscribed symbols to the ones provided.
     * @param symbols List containing symbols of the desired securities on the correct exchange.
     */
    fun setWatchedSecurities(symbols: List<String>)

    /**
     * Closes the connection to the datasource.
     */
    fun close()

}