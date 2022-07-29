package com.mystockdata.financialreportservice.financialreportdatasource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

/**
 * Adapter using a WebClient to query an Arelle Webservice.
 * URL and port are injected into the configuration using environmental variables in docker-compose.
 */

@Component
class ArelleAdapter(
    @Autowired val arelleWebClient: WebClient
): FinancialReportDataSource {
    /**
     * Calls the local Arelle webservice and asks for a xml representation of the financial report which can be found at the provided path.
     * @param fileName Path to the financial report to be read. (Local path or from Web (URL))
     * @return List containing the retrieved items.
     */
    suspend fun retrieveFactsFromLocalFile(fileName: String) = retrieveFacts("/var/lib/financial-reports/$fileName")

    /**
     * Calls the local Arelle webservice and asks for a xml representation of the financial report which can be found at the provided location (either local path (--> use retrieveFactsLocal) or remote by providing a URL).
     * @param path Path to the financial report to be read. (Local path or from Web (URL))
     * @return Flow containing the retrieved items.
     */
    override suspend fun retrieveFacts(path: String): List<Item>? {
        return arelleWebClient.get()
            .uri("rest/xbrl/view?file=$path&view=facts&factListCols=Concept,Name,Label,LocalName,Namespace,contextRef,unitRef,Dec,Prec,Lang,Value,EntityScheme,EntityIdentifier,Period,ID,Type,PeriodType,Balance,Documentation,Dimension&media=xml")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
            .retrieve()
            .awaitBody<FactList>().item
    }

    /**
     * Check whether the string is included in the response listing the loaded packages.
     * @param taxonomy Taxonomy to search for.
     * @return Whether the taxonomy is included (true) or not (false).
     */
    suspend fun checkTaxonomyLoaded(taxonomy: String): Boolean = checkTaxonomiesLoaded(setOf(taxonomy))[taxonomy] ?: false


    /**
     * Check whether the strings are included in the response listing the loaded packages.
     * Note: Won't return either json or xml (with correct application/xml or application/json header and query parameters)
     * @param taxonomies Taxonomies to search for.
     * @return HashMap containing the passed taxonomies with the corresponding results.
     */
    suspend fun checkTaxonomiesLoaded(taxonomies: Set<String>): HashMap<String, Boolean> {
        val response = arelleWebClient.get()
            .uri("/rest/configure?packages=show")
            .header(MediaType.TEXT_HTML_VALUE)
            .retrieve()
            .awaitBody<String>()

        val included = HashMap<String, Boolean>()
        for (taxonomy: String in taxonomies) included[taxonomy] = response.contains("/${taxonomy}")
        return included
    }

    // Esef taxonomy: esef_taxonomy_2021.zip
    /**
     * Add a taxonomy file by its name.
     * @param fileName Name of the file to be added.
     * @return Boolean indicating success (true) or failure (false)
     */
    suspend fun loadTaxonomy(fileName: String) = include("/var/lib/taxonomies/$fileName")

    /**
     * Add a taxonomy file by its path.
     * Note: Won't return either json or xml (with correct application/xml or application/json header and query parameters)
     * @param path Path to location of the file to be added (inside Docker container).
     * @return Boolean indicating success (true) or failure (false)
     */
    private suspend fun include(path: String): Boolean {
        val response = arelleWebClient.get()
            .uri("/rest/configure?packages=$path")
            .header(MediaType.TEXT_HTML_VALUE)
            .retrieve()
            .awaitBody<String>()
        return response.contains("successful")
    }
}