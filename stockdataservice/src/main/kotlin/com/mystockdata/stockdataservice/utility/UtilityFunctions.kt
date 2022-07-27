package com.mystockdata.stockdataservice.utility

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.*
import java.util.*

fun epochMilliToLocalDateTime(epochSecond: Long): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochSecond), ZoneId.systemDefault())

fun localDateTimeToEpochMilli(date: LocalDateTime) = Date.from(date.toInstant(ZoneOffset.UTC)).time

fun localDateToEpochMilli(date: LocalDate) = Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC)).time

fun localDateToEpochSeconds(date: LocalDate) = localDateToEpochMilli(date) / 1000


/**
 * Accumulates a certain number of elements and emit them in a new flow.
 * As in https://github.com/influxdata/influxdb-client-java/blob/master/examples/src/main/java/example/KotlinWriteBatchingByFlow.kt
 * @param size The amount of elements to be accumulated.
 * @return New Flow emitting lists of accumulated elements.
 */
suspend fun <T> Flow<T>.chunks(size: Int): Flow<List<T>> = flow{
    val chunk = ArrayList<T>(size)

    collect{ t ->
        chunk += t
        if(chunk.size >= size){
            emit(chunk)
            chunk.clear()
        }
    }

    if(chunk.size > 0){
        emit(chunk)
    }
}

fun printMultiDimensionalArray(arr: Array<Array<String>>) {
    for (subArr in arr) {
        var row = ""
        for ((index, str) in subArr.withIndex()) {
            row += if (index < subArr.size - 1) "$str,\t" else str
        }
        println(row)
    }
}