package com.example.moneyflow.data.repository

import com.example.moneyflow.data.local.entity.BudgetEntity
import com.example.moneyflow.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    val allBudgets: Flow<List<BudgetEntity>>
    val allCategories: Flow<List<CategoryEntity>>
    fun getBudgetsByMonth(monthId: String): Flow<List<BudgetEntity>>
    suspend fun insertBudget(budget: BudgetEntity)
    suspend fun deleteBudget(budget: BudgetEntity)
    suspend fun insertCategory(category: CategoryEntity)
    suspend fun deleteCategory(category: CategoryEntity)
    fun getSpentAmountByCategory(catId: Long): Flow<Long?>
}
