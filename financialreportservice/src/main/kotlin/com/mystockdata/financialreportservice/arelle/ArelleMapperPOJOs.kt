package com.mystockdata.financialreportservice.arelle

import javax.xml.bind.annotation.XmlRootElement

data class Item(
    var label: String? = null,
    var contextRef: String? = null,
    var unitRef: String? = null,
    var dec: Int = 0,
    var value: Double = 0.0,
    var name: String? = null,
    var text: String? = null,
    var lang: String? = null,
)
@XmlRootElement(name="factList")
data class FactList(
    var item: List<Item>? = null
)


