package com.mystockdata.financialreportservice.financialreports

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FinancialReportRepository: CoroutineCrudRepository<FinancialReport, String> {

    /**
     * Retrieves financial reports matching one of the entity identifiers with a endOfReportingPeriod date between the two provided endOfReportingPeriod dates.
     * @param entityIdentifier Identifier of the entity. LEI
     * @param endOfReportingPeriod matching financial reports have to be older than this date.
     * @param endOfReportingPeriod2 matching financial reports have to be younger than this date.
     * @return Flow emitting the retrieved financial reports.
     * Equivalent query { $and: [ { entityIdentifier: "549300CSLHPO6Y1AZN37" }, { endOfReportingPeriod: {$gt:ISODate("2020-01-01"), $lt:ISODate("2021-01-01")} } ] }
     */
   suspend fun getFinancialReportByEntityIdentifierInAndEndOfReportingPeriodBetween(entityIdentifier: List<String>, endOfReportingPeriod: Date, endOfReportingPeriod2: Date): Flow<FinancialReport>

    /**
     * Retrieves financial reports matching one of the entity identifiers.
     * @param entityIdentifier Identifier of the entity. LEI
     * @return Flow emitting the retrieved financial reports.
     */
    suspend fun findFinancialReportByEntityIdentifierIn(entityIdentifier: List<String>): Flow<FinancialReport>

    /**
     * Retrieves financial reports with a endOfReportingPeriod date between the two provided endOfReportingPeriod dates.
     * @param endOfReportingPeriod matching financial reports have to be older than this date.
     * @param endOfReportingPeriod2 matching financial reports have to be younger than this date.
     * @return Flow emitting the retrieved financial reports.
     */
    suspend fun findFinancialReportByEndOfReportingPeriodBetween(endOfReportingPeriod: Date, endOfReportingPeriod2: Date): Flow<FinancialReport>

}