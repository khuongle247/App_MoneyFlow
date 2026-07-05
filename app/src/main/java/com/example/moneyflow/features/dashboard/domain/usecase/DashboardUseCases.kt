package com.example.moneyflow.features.dashboard.domain.usecase

import javax.inject.Inject

data class DashboardUseCases @Inject constructor(
    val getSummary: GetDashboardSummaryUseCase,
    val getTransactions: GetMonthlyTransactionsUseCase,
    val setBalance: SetMonthlyBalanceUseCase,
    val checkCycle: CheckFinancialCycleUseCase,
    val getCurrentCycle: GetCurrentCycleUseCase,
    val getDailySummary: GetDailyExpenseSummaryUseCase
)
