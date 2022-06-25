package com.mystockdata.stockdataservice.precisepriceinformation

interface PreciseInformationProvider {
    fun establishConnection(symbols: List<String>)
    fun setWatchedSecurities(symbols: List<String>)
    fun closeConnection()
}