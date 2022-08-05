package com.mystockdata.composerservice.company

import com.mystockdata.composerservice.financialreport.FinancialReport
import com.mystockdata.composerservice.stockdata.AggregatedPriceInformationResponse
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("v1/company")
class CompanyController(
    @Autowired val companyService: CompanyService
) {

    @GetMapping("/{lei}")
    suspend fun getCompany(
        @PathVariable lei: String
    ) = companyService.getCompany(lei)

    @PutMapping
    suspend fun addCompany(
        @RequestBody companies: Set<Company>,
        @RequestParam addToWatchList: Boolean?
    ): Any {
        return if (addToWatchList == null || addToWatchList == false) {
            companyService.addCompanies(companies)
        } else {
            companyService.addCompaniesAndSaveToWatchList(companies)
        }
    }

    @PutMapping("/toWatchList")
    suspend fun addCompaniesToWatchList(
        @RequestParam lei: List<String>
    ): Flow<String> {
        return companyService.addCompaniesToWatchlist(lei)
    }

    @GetMapping("/retrieveDataForCompanies")
    suspend fun retrieveDataForCompanies(
        @RequestParam lei: List<String>,
        @RequestParam aggregatedPriceInfoStart: Instant,
        @RequestParam aggregatedPriceInfoEnd: Instant
    ): Pair<List<FinancialReport>, List<AggregatedPriceInformationResponse>> {
        return companyService.retrieveDataForCompanies(lei, aggregatedPriceInfoStart, aggregatedPriceInfoEnd)
    }
}
