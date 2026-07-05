package com.example.moneyflow.features.dashboard.domain.usecase

import com.example.moneyflow.data.local.entity.FinancialCycleEntity
import com.example.moneyflow.features.dashboard.domain.repository.FinancialCycleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentCycleUseCase @Inject constructor(
    private val repository: FinancialCycleRepository
) {
    operator fun invoke(): Flow<FinancialCycleEntity?> = repository.getCurrentCycle()
}
