package com.mystockdata.stockdataservice.utility

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.*
import java.util.*

fun epochMilliToLocalDateTime(epochSecond: Long): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochSecond), ZoneId.systemDefault())

fun localDateTimeToEpochMilli(date: LocalDateTime) = Date.from(date.toInstant(ZoneOffset.UTC)).time

fun localDateToEpochMilli(date: LocalDate) = Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC)).time

fun localDateToEpochSeconds(date: LocalDate) = localDateToEpochMilli(date) / 1000

fun printMultiDimensionalArray(arr: Array<Array<String>>) {
    for (subArr in arr) {
        var row = ""
        for ((index, str) in subArr.withIndex()) {
            row += if (index < subArr.size - 1) "$str,\t" else str
        }
        println(row)
    }
}