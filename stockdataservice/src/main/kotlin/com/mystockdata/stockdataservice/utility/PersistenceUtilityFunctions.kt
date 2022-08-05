package com.mystockdata.stockdataservice.utility

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.ArrayList

/**
 * Function to build Flux filter functions
 * i.e.   |> filter(fn: (r) => r["_field"] == "adjClose" or r["_field"] == "close" or r["_field"] == "high" or r["_field"] == "low" or r["_field"] == "open" or r["_field"] == "volume")
 * @param fieldName field according to which you want to filter
 * @param allowedOptions allowed values
 * @return Flux filter function as a string
 */
fun createFilter(fieldName: String, allowedOptions: List<String>): String {
    val stringBuilder = StringBuilder()
    for ((index, option) in allowedOptions.withIndex()) {
        stringBuilder.append("${if (index > 0) " or " else ""}r[\"$fieldName\"] == \"$option\"")
    }
    return "|> filter(fn: (r) => $stringBuilder)"
}

/**
 * Accumulates a certain number of elements and emit them in a new flow.
 * As in https://github.com/influxdata/influxdb-client-java/blob/master/examples/src/main/java/example/KotlinWriteBatchingByFlow.kt
 * @param size The amount of elements to be accumulated.
 * @return New Flow emitting lists of accumulated elements.
 */
suspend fun <T> Flow<T>.chunks(size: Int = 50): Flow<List<T>> = flow {
    val chunk = ArrayList<T>(size)

    collect { t ->
        chunk += t
        if (chunk.size >= size) {
            emit(chunk)
            chunk.clear()
        }
    }

    if (chunk.size > 0) {
        emit(chunk)
    }
}