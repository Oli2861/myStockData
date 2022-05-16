package com.mystockdata.financialreportservice

import com.mystockdata.financialreportservice.arelle.ArelleAdapter
import com.mystockdata.financialreportservice.arelle.Item
import com.mystockdata.financialreportservice.xbrlfilings.XBRLFilingsAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FinancialReportService(
    @Autowired
    val arelleAdapter: ArelleAdapter,
    @Autowired
    val xbrlFilingsAdapter: XBRLFilingsAdapter
) {
    /**
     * Tells arelle to load a taxonomy.
     * @param fileName Name of the taxonomy file to be loaded. File has to be placed within the taxonomies (docker) volume. By default, loads the esef taxonomy.
     * @return Boolean indicating success (true) or failure (false) of the operation.
     */
    suspend fun loadEsefTaxonomy(fileName: String = "esef_taxonomy_2021.zip"): Boolean = arelleAdapter.loadTaxonomy(fileName)

    suspend fun loadFinancialReports(): Flow<List<Item>?> {
        val reportFlow = xbrlFilingsAdapter.getAvailableFinancialReports()

        // TODO: Check whether the data is already stored and only request the zip when it is not
        val first = reportFlow.first()
        println(first.toString())
        val facts = arelleAdapter.retrieveFacts(first.url)
        facts.collect {
           println(it.toString())
        }
        return facts
    }

}