package com.mystockdata.financialreportservice.utility

import java.util.*

/**
 * @param year The year.
 * @param date The Date to check whether it is in the year.
 * @return True if the date is in the year.
 */
fun isDateInYear(year: Int, date: Date): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = date
   return year == calendar.get(Calendar.YEAR)
}