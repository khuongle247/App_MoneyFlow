package com.example.moneyflow.features.dashboard.domain.repository

import com.example.moneyflow.data.local.entity.FinancialCycleEntity
import kotlinx.coroutines.flow.Flow

interface FinancialCycleRepository {
    fun getCurrentCycle(): Flow<FinancialCycleEntity?>
    suspend fun getActiveCycle(): FinancialCycleEntity?
    suspend fun insertCycle(cycle: FinancialCycleEntity)
    suspend fun updateCycle(cycle: FinancialCycleEntity)
    fun getCycleStartDay(): Flow<Int>
    suspend fun saveCycleStartDay(day: Int)
}
