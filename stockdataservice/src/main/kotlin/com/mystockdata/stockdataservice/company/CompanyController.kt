package com.mystockdata.stockdataservice.company

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("v1/company")
class CompanyController(
    @Autowired val companyService: CompanyService
) {

    @GetMapping()
    suspend fun getCompanies(
        @RequestParam lei: List<String>
    ) = companyService.getCompanies(lei.toSet())

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
    ): List<String>? {
        return companyService.addCompaniesToWatchlist(lei.toSet())
    }
}
