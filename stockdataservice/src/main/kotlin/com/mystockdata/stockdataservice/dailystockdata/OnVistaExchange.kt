package com.mystockdata.stockdataservice.dailystockdata

enum class OnVistaExchange(val exchangeName: String) {
    XETRA("Xetra"),
    TRADEGATE("Tradegate"),
    NASDAQ_OTIC("Nasdaq OTC"),
    LS_X("LS Exchange"),
    HAMBURG("Hamburg"),
    LS("Lang &amp; Schwarz"),
    STUTTGART("Stuttgart"),
    QUOTRIX("Quotrix"),
    GETTEX("Gettex"),
    MÜNCHEN("München"),
    LONDON_TR("London Trade Rep."),
    BERLIN("Berlin"),
    FRANKFURT("Frankfurt"),
    DÜSSELDORF("Düsseldorf"),
    BB("Baader Bank"),
    HANNOVER("Hannover"),
    SE("Swiss Exchange"),
    SE_1("Swiss Exchange")
}