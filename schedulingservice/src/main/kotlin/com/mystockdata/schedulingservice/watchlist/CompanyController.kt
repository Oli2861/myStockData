package com.mystockdata.schedulingservice.watchlist

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

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