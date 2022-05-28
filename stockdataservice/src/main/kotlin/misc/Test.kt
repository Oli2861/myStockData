package misc

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.util.*


fun yahoo() {
    // Yahoo Finance: History
    val today = Date()

    val ticker = "TSLA"
    val startTimeStamp: Long = 1653523200
    val endTimeSpamp: Long = today.time
    val interval = "1d"
    val frequency = "1d"
    val url =
        "https://finance.yahoo.com/quote/$ticker/history?period1=$startTimeStamp&period2=$endTimeSpamp&interval=$interval&filter=history&frequency=$frequency&includeAdjustedClose=true"
    println(url)


    val document: Document = Jsoup.connect(url).get()

    val query = "table"
    val matches: Elements = document.select("table[data-test=historical-prices]")
    matches.forEach { println(it) }
    val table = matches.first()
    println(table.toString())
    table?.let {
        val headerRow = table.select("th")
        headerRow.forEach { println(it) }
        val rows = table.select("td")
        rows.forEach { println(it) }
    }
}