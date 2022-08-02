package com.mystockdata.stockdataservice.company

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

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
        @RequestBody companies: List<Company>
    ) = companyService.addCompanies(companies)

}