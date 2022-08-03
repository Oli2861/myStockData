package com.mystockdata.composerservice.company

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository: CoroutineCrudRepository<Company, String> {

}