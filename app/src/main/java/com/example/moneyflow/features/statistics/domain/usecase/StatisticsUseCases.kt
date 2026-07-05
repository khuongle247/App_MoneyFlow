package com.example.moneyflow.features.statistics.domain.usecase

import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.data.repository.BudgetRepository
import com.example.moneyflow.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class StatisticsUseCases @Inject constructor(
    val getStatisticsSummary: GetStatisticsSummaryUseCase
)

class GetStatisticsSummaryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(monthId: String): Flow<StatisticsSummary> {
        val txsFlow = transactionRepository.allTransactions.map { txs ->
            txs.filter { SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date(it.date)) == monthId }
        }
        val budgetsFlow = budgetRepository.getBudgetsByMonth(monthId)

        return combine(txsFlow, budgetsFlow) { txs, budgets ->
            val totalSpent = txs.filter { it.type == TransactionType.EXPENSE }.sumOf { Math.abs(it.amount) }
            val spendingMap = budgets.associateWith { budget ->
                txs.filter { it.budgetId == budget.id && it.type == TransactionType.EXPENSE }
                    .sumOf { Math.abs(it.amount) }
            }.filterValues { it > 0L }
            
            StatisticsSummary(totalSpent, spendingMap, budgets)
        }
    }
}

data class StatisticsSummary(
    val totalSpent: Long,
    val spendingMap: Map<com.example.moneyflow.data.local.entity.BudgetEntity, Long>,
    val budgets: List<com.example.moneyflow.data.local.entity.BudgetEntity>
)
