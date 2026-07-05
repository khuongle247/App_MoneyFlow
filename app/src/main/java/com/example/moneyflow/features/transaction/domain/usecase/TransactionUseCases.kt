package com.example.moneyflow.features.transaction.domain.usecase

import com.example.moneyflow.data.local.entity.BudgetEntity
import com.example.moneyflow.data.local.entity.CategoryEntity
import com.example.moneyflow.data.local.entity.TransactionEntity
import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.data.repository.BudgetRepository
import com.example.moneyflow.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class TransactionUseCases @Inject constructor(
    val addTransaction: AddTransactionUseCase,
    val updateTransaction: UpdateTransactionUseCase,
    val deleteTransaction: DeleteTransactionUseCase,
    val getTransactionsByMonth: GetTransactionsByMonthUseCase,
    val getTransactionById: GetTransactionByIdUseCase,
    val getMetadata: GetTransactionMetadataUseCase
)

class AddTransactionUseCase @Inject constructor(private val repository: TransactionRepository) {
    suspend operator fun invoke(title: String, amount: Long, type: TransactionType, categoryId: Long, budgetId: Long?, icon: String, monthId: String) {
        val sdf = SimpleDateFormat("MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(monthId) ?: Date()
        val now = Calendar.getInstance()
        if (calendar.get(Calendar.MONTH) != now.get(Calendar.MONTH) || calendar.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
            calendar.set(Calendar.DAY_OF_MONTH, 1)
        } else {
            calendar.time = Date()
        }
        repository.insertTransaction(TransactionEntity(title = title, amount = if (type == TransactionType.EXPENSE) -Math.abs(amount) else Math.abs(amount), type = type, categoryId = categoryId, budgetId = budgetId, icon = icon, date = calendar.timeInMillis))
    }
}

class UpdateTransactionUseCase @Inject constructor(private val repository: TransactionRepository) {
    suspend operator fun invoke(id: Long, title: String, amount: Long, type: TransactionType, categoryId: Long, budgetId: Long?, icon: String) {
        val oldTx = repository.getTransactionById(id) ?: return
        val newTx = oldTx.copy(title = title, amount = if (type == TransactionType.EXPENSE) -Math.abs(amount) else Math.abs(amount), type = type, categoryId = categoryId, budgetId = budgetId, icon = icon)
        repository.insertTransaction(newTx)
    }
}

class DeleteTransactionUseCase @Inject constructor(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: TransactionEntity) = repository.deleteTransaction(transaction)
}

class GetTransactionsByMonthUseCase @Inject constructor(private val repository: TransactionRepository) {
    operator fun invoke(monthId: String): Flow<List<TransactionEntity>> = repository.allTransactions.map { txs ->
        txs.filter { SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date(it.date)) == monthId }
    }
}

class GetTransactionByIdUseCase @Inject constructor(private val repository: TransactionRepository) {
    suspend operator fun invoke(id: Long): TransactionEntity? = repository.getTransactionById(id)
}

class GetTransactionMetadataUseCase @Inject constructor(private val budgetRepository: BudgetRepository) {
    val categories: Flow<List<CategoryEntity>> = budgetRepository.allCategories
    val budgets: Flow<List<BudgetEntity>> = budgetRepository.allBudgets
}
