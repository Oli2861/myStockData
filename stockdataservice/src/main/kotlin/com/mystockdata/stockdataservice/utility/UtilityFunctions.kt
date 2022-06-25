package com.mystockdata.stockdataservice.utility

import java.time.*
import java.util.*

fun epochMilliToLocalDateTime(epochSecond: Long): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochSecond), ZoneId.systemDefault())

fun localDateTimeToEpochMilli(date: LocalDateTime) = Date.from(date.toInstant(ZoneOffset.UTC)).time

fun localDateToEpochMilli(date: LocalDate) = Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC)).time

fun localDateToEpochSeconds(date: LocalDate) = localDateToEpochMilli(date) / 1000
