package misc

import com.mystockdata.financialreportservice.xbrlfilings.RetrievedReportInfo
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow

private val webClient: WebClient = WebClient.create("https://filings.xbrl.org/")

/*
suspend fun main() {
    val response =  webClient.get()
        .uri("table-index.json")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType(MediaType.APPLICATION_JSON))
        .retrieve()
        .bodyToFlow<RetrievedReportInfo>()
    response.collect {
        println(it.toString())
    }
}*/