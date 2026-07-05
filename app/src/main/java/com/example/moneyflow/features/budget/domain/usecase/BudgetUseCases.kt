package com.example.moneyflow.features.budget.domain.usecase

import com.example.moneyflow.data.local.entity.BudgetEntity
import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.data.repository.BudgetRepository
import com.example.moneyflow.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class BudgetUseCases @Inject constructor(
    val addBudget: AddBudgetUseCase,
    val deleteBudget: DeleteBudgetUseCase,
    val getBudgetsByMonth: GetBudgetsByMonthUseCase,
    val getBudgetSpending: GetBudgetSpendingUseCase,
    val getBudgetById: GetBudgetByIdUseCase
)

class GetBudgetByIdUseCase @Inject constructor(private val repository: BudgetRepository) {
    suspend operator fun invoke(id: Long): BudgetEntity? {
        return repository.allBudgets.first().find { it.id == id }
    }
}

class AddBudgetUseCase @Inject constructor(private val repository: BudgetRepository) {
    suspend operator fun invoke(name: String, amount: Long, color: Int, icon: String, monthId: String) {
        repository.insertBudget(BudgetEntity(name = name, amount = amount, color = color, icon = icon, monthId = monthId))
    }
}

class DeleteBudgetUseCase @Inject constructor(private val repository: BudgetRepository) {
    suspend operator fun invoke(budget: BudgetEntity) = repository.deleteBudget(budget)
}

class GetBudgetsByMonthUseCase @Inject constructor(private val repository: BudgetRepository) {
    operator fun invoke(monthId: String): Flow<List<BudgetEntity>> = repository.getBudgetsByMonth(monthId)
}

class GetBudgetSpendingUseCase @Inject constructor(private val repository: TransactionRepository) {
    operator fun invoke(monthId: String): Flow<Map<Long, Long>> {
        return repository.allTransactions.map { txs ->
            txs.filter { 
                SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date(it.date)) == monthId &&
                it.type == TransactionType.EXPENSE && it.budgetId != null 
            }
            .groupBy { it.budgetId!! }
            .mapValues { entry -> entry.value.sumOf { Math.abs(it.amount) } }
        }
    }
}
