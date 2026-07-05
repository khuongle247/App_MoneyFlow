package com.example.moneyflow.data.local.dao

import androidx.room.*
import com.example.moneyflow.data.local.entity.FinancialCycleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleDao {
    @Query("SELECT * FROM financial_cycles ORDER BY startDate DESC LIMIT 1")
    fun getCurrentCycle(): Flow<FinancialCycleEntity?>

    @Query("SELECT * FROM financial_cycles WHERE isClosed = 0 LIMIT 1")
    suspend fun getActiveCycle(): FinancialCycleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycle(cycle: FinancialCycleEntity)

    @Update
    suspend fun updateCycle(cycle: FinancialCycleEntity)
}
