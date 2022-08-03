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
fun Date.sameDay(other: Date): Boolean{
    val calendar = Calendar.getInstance()
    val otherCalendar = Calendar.getInstance()
    calendar.time = this
    otherCalendar.time = other

    return calendar.get(Calendar.DAY_OF_YEAR) == otherCalendar.get(Calendar.DAY_OF_YEAR) && calendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR)
}
fun Date.addDays(days: Int): Date? {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.DATE, days)
    return calendar.time
}