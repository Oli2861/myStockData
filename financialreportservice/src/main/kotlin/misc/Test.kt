package misc
/*
import com.mystockdata.financialreportservice.arelle.FactList
import com.mystockdata.financialreportservice.arelle.Item
import com.mystockdata.financialreportservice.arelle.TYPE
import com.mystockdata.financialreportservice.financialreports.FinancialReport
import com.mystockdata.financialreportservice.financialreports.IFRSGeneralInformation
import com.mystockdata.financialreportservice.financialreports.IFRSStatementOfFinancialPositionCurrentNotCurrent
import com.mystockdata.financialreportservice.financialreports.IFRSStatementOfFinancialPositionOrderOfLiquidity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.xml.Jaxb2XmlDecoder
import org.springframework.http.codec.xml.Jaxb2XmlEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.util.*

val bufferSize = 16 * 1024 * 1024
private val webClient: WebClient = WebClient
    .builder()
    .baseUrl("http://localhost:8081/")
    .exchangeStrategies(ExchangeStrategies.builder().codecs { configurer: ClientCodecConfigurer ->
        configurer.defaultCodecs().jaxb2Encoder(Jaxb2XmlEncoder())
        configurer.defaultCodecs().jaxb2Decoder(Jaxb2XmlDecoder())
        configurer.defaultCodecs().maxInMemorySize(bufferSize)
    }.build())
    .build()

suspend fun main() {
    val reports = getFinancialReports("sap-2020-12-31AR.zip")
    //val reports = getFinancialReports("softwareag-2020-12-31.zip")
}

suspend fun retrieveFactsFromLocalFile(fileName: String) = retrieveFacts("/var/lib/financial-reports/$fileName")

suspend fun retrieveFacts(path: String): List<Item>? {
    return webClient.get()
        .uri("rest/xbrl/view?file=$path&view=facts&factListCols=Label,unitRef,Value,EntityScheme,EntityIdentifier,Period,PeriodType,Prec,Lang,Type,Balance&media=xml")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
        .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
        .retrieve()
        .awaitBody<FactList>().item
}

/**
 * Retrieve financial reports from a given path.
 * @param path Path to the local report.
 * @return Map containing the date for the financial report and the financial report.
 */
suspend fun getFinancialReports(path: String): Map<Date, FinancialReport>? {
    var reportMap: Map<Date, FinancialReport>? = null

    val factList = retrieveFactsFromLocalFile(path)
    factList?.forEach {
        println(it.toString())
    }
    factList?.let { factList ->
        val map = splitByDate(factList)
        reportMap = map.mapValues { (date, list) -> createFinancialReportFromList(list) }
    }

    reportMap?.forEach { (endDate, report) ->
        println("Financial report for $endDate")
        println(report.currency)
        println(report.entityIdentifier)
        println(report.entityIdentifierScheme)
        println(report.IFRSGeneralInformation.toString())
        println(report.IFRSStatementOfFinancialPositionCurrentNotCurrent.toString())
        println(report.IFRSStatementOfFinancialPositionOrderOfLiquidity.toString())
    }
    return reportMap
}

/**
 * Map end instants on lists of items in order to differentiate between different report items (balance sheet 2019, 2020,..)
 * @param list List containing all items.
 * @return Map mapping Dates to Lists of items.
 */
fun splitByDate(list: List<Item>): Map<Date, List<Item>> {
    val map = HashMap<Date, MutableList<Item>>()

    list.forEach { item ->
        item.endInstant?.let { endDate ->
            if (map[endDate].isNullOrEmpty()) {
                map[endDate] = mutableListOf(item)
            } else {
                map[endDate]?.add(item)
            }
        }
    }

    return map
}

/**
 * Create a financial reports from a list of items.
 * @param list List of items used to create a financial reports.
 * @return Financial report containing the information of the provided items.
 */
fun createFinancialReportFromList(list: List<Item>): FinancialReport {
    val IFRSGeneralInformation = IFRSGeneralInformation()
    val IFRSStatementOfFinancialPositionCurrentNotCurrent = IFRSStatementOfFinancialPositionCurrentNotCurrent()
    val IFRSStatementOfFinancialPositionOrderOfLiquidity = IFRSStatementOfFinancialPositionOrderOfLiquidity()
    val currency: String = list.firstNotNullOf { it.unitRef }
    val entityIdentifier: String = list.firstNotNullOf { it.entityIdentifier }
    val entityScheme: String = list.firstNotNullOf { it.entityScheme }
    // Loop over elements and their assignment to the appropriate report component
    list.forEach { item ->
        val type = item.type

        if (type == TYPE.DATE_ITEM.str || type == TYPE.STRING_ITEM.str) {
            IFRSGeneralInformation.setValue(item)
        }

        if (type == TYPE.MONETARY_ITEM.str || type == TYPE.PER_SHARE_ITEM.str) {
            IFRSStatementOfFinancialPositionCurrentNotCurrent.setValue(item)
            IFRSStatementOfFinancialPositionOrderOfLiquidity.setValue(item)
        }

    }
    return FinancialReport(
        IFRSGeneralInformation,
        IFRSStatementOfFinancialPositionCurrentNotCurrent,
        IFRSStatementOfFinancialPositionOrderOfLiquidity,
        currency,
        entityIdentifier,
        entityScheme
    )
}
*/