package com.example.moneyflow.features.onboarding.domain.usecase

import com.example.moneyflow.data.local.entity.MonthlyBalanceEntity
import com.example.moneyflow.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class OnboardingUseCases @Inject constructor(
    val completeOnboarding: CompleteOnboardingUseCase,
    val checkFirstLaunch: CheckFirstLaunchUseCase
)

class CompleteOnboardingUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(name: String, balance: Long) {
        repository.saveOnboardingData(name, balance)
        val currentMonth = SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date())
        repository.insertMonthlyBalance(MonthlyBalanceEntity(currentMonth, balance))
    }
}

class CheckFirstLaunchUseCase @Inject constructor(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<Boolean> = repository.isFirstLaunch
}
