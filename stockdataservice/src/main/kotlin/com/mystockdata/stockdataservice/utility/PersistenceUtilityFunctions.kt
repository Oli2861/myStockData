package com.mystockdata.stockdataservice.persistence

/**
 * Function to build Flux filter functions
 * i.e.   |> filter(fn: (r) => r["_field"] == "adjClose" or r["_field"] == "close" or r["_field"] == "high" or r["_field"] == "low" or r["_field"] == "open" or r["_field"] == "volume")
 * @param fieldName field according to which you want to filter
 * @param allowedOptions allowed values
 * @return Flux filter function as a string
 */
fun createFilter(fieldName: String, allowedOptions: List<String>): String{
    val stringBuilder = StringBuilder()
    for ((index, option) in allowedOptions.withIndex()) {
        stringBuilder.append("${if (index > 0) " or " else ""}r[\"$fieldName\"] == \"$option\"")
    }
    return "|> filter(fn: (r) => ${stringBuilder.toString()})"
}