package com.example.moneyflow.features.dashboard.domain.usecase

import com.example.moneyflow.data.local.entity.FinancialCycleEntity
import com.example.moneyflow.features.dashboard.domain.repository.FinancialCycleRepository
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class CheckFinancialCycleUseCase @Inject constructor(
    private val repository: FinancialCycleRepository
) {
    suspend operator fun invoke(): FinancialCycleStatus {
        val activeCycle = repository.getActiveCycle()
        val now = System.currentTimeMillis()

        if (activeCycle == null) {
            // First time setup needed or create initial cycle
            return FinancialCycleStatus.NeedsInitialSetup
        }

        if (now > activeCycle.endDate) {
            // Cycle has ended. Mark as closed and signal UI for new balance
            repository.updateCycle(activeCycle.copy(isClosed = true))
            return FinancialCycleStatus.CycleEnded(activeCycle)
        }

        return FinancialCycleStatus.Active(activeCycle)
    }

    suspend fun startNewCycle(initialBalance: Long) {
        val startDay = repository.getCycleStartDay().first()
        val calendar = Calendar.getInstance()
        
        // Start date: Today 00:00:00
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        // End date: Next month, day (startDay - 1)
        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, startDay)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endDate = calendar.timeInMillis

        val newCycle = FinancialCycleEntity(
            startDate = startDate,
            endDate = endDate,
            initialBalance = initialBalance,
            remainingBalance = initialBalance,
            totalExpense = 0,
            isClosed = false
        )
        repository.insertCycle(newCycle)
    }
}

sealed interface FinancialCycleStatus {
    object NeedsInitialSetup : FinancialCycleStatus
    data class Active(val cycle: FinancialCycleEntity) : FinancialCycleStatus
    data class CycleEnded(val lastCycle: FinancialCycleEntity) : FinancialCycleStatus
}
