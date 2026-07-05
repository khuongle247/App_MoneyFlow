package com.example.moneyflow.di

import com.example.moneyflow.data.repository.*
import com.example.moneyflow.features.dashboard.data.repository.FinancialCycleRepositoryImpl
import com.example.moneyflow.features.dashboard.domain.repository.FinancialCycleRepository
import com.example.moneyflow.features.notification.data.repository.NotificationRepositoryImpl
import com.example.moneyflow.features.notification.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        budgetRepositoryImpl: BudgetRepositoryImpl
    ): BudgetRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        goalRepositoryImpl: GoalRepositoryImpl
    ): GoalRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindFinancialCycleRepository(
        financialCycleRepositoryImpl: FinancialCycleRepositoryImpl
    ): FinancialCycleRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}
