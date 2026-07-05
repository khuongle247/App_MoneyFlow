package com.example.moneyflow.features.dashboard.domain.usecase

import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.data.repository.BudgetRepository
import com.example.moneyflow.data.repository.SettingsRepository
import com.example.moneyflow.data.repository.TransactionRepository
import com.example.moneyflow.features.dashboard.domain.model.DashboardSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GetDashboardSummaryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(monthId: String): Flow<DashboardSummary> {
        val transactionsFlow = transactionRepository.allTransactions.map { txs ->
            txs.filter { SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date(it.date)) == monthId }
        }
        
        val monthlyBalanceFlow = settingsRepository.getMonthlyBalance(monthId)
        
        val budgetsFlow = budgetRepository.getBudgetsByMonth(monthId)

        return combine(transactionsFlow, monthlyBalanceFlow, budgetsFlow) { txs, mBalance, budgets ->
            val totalSpent = txs.filter { it.type == TransactionType.EXPENSE }.sumOf { Math.abs(it.amount) }
            val totalIncome = txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val totalBudgetAllocated = budgets.sumOf { it.amount }
            val currentAsset = (mBalance?.balance ?: 0L) + txs.sumOf { it.amount }
            
            DashboardSummary(
                totalAsset = currentAsset,
                totalIncome = totalIncome,
                totalSpent = totalSpent,
                totalBudgetAllocated = totalBudgetAllocated,
                availableToBudget = currentAsset - totalBudgetAllocated,
                isBalanceSet = mBalance != null
            )
        }
    }
}
