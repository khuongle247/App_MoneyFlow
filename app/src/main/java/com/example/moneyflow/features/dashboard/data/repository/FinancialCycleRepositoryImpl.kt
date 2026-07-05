package com.example.moneyflow.features.dashboard.data.repository

import com.example.moneyflow.data.local.FinlyDataStore
import com.example.moneyflow.data.local.dao.CycleDao
import com.example.moneyflow.data.local.entity.FinancialCycleEntity
import com.example.moneyflow.features.dashboard.domain.repository.FinancialCycleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinancialCycleRepositoryImpl @Inject constructor(
    private val cycleDao: CycleDao,
    private val dataStore: FinlyDataStore
) : FinancialCycleRepository {
    override fun getCurrentCycle(): Flow<FinancialCycleEntity?> = cycleDao.getCurrentCycle()

    override suspend fun getActiveCycle(): FinancialCycleEntity? = cycleDao.getActiveCycle()

    override suspend fun insertCycle(cycle: FinancialCycleEntity) = cycleDao.insertCycle(cycle)

    override suspend fun updateCycle(cycle: FinancialCycleEntity) = cycleDao.updateCycle(cycle)

    override fun getCycleStartDay(): Flow<Int> = dataStore.cycleStartDay

    override suspend fun saveCycleStartDay(day: Int) = dataStore.saveCycleStartDay(day)
}
