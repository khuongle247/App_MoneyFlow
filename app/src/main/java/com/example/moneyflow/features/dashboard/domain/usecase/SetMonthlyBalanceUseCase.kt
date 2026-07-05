package com.example.moneyflow.features.dashboard.domain.usecase

import com.example.moneyflow.data.local.entity.MonthlyBalanceEntity
import com.example.moneyflow.data.repository.SettingsRepository
import javax.inject.Inject

class SetMonthlyBalanceUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(monthId: String, balance: Long) {
        repository.insertMonthlyBalance(MonthlyBalanceEntity(monthId, balance))
    }
}
