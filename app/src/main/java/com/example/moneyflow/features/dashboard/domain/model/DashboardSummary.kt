package com.example.moneyflow.features.dashboard.domain.model

data class DashboardSummary(
    val totalAsset: Long,
    val totalIncome: Long,
    val totalSpent: Long,
    val totalBudgetAllocated: Long,
    val availableToBudget: Long,
    val isBalanceSet: Boolean
)
