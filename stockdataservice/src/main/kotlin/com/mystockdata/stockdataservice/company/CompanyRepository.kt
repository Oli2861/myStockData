package com.mystockdata.stockdataservice.company

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository: CoroutineCrudRepository<Company, String> {
    suspend fun findByLeiIs(lei: String): Flow<Company>
}