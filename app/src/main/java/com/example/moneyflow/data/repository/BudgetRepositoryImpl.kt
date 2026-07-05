package com.example.moneyflow.data.repository

import com.example.moneyflow.data.local.dao.FinlyDao
import com.example.moneyflow.data.local.entity.BudgetEntity
import com.example.moneyflow.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val dao: FinlyDao
) : BudgetRepository {

    override val allBudgets: Flow<List<BudgetEntity>> = dao.getAllBudgets()
    
    override val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()

    override fun getBudgetsByMonth(monthId: String): Flow<List<BudgetEntity>> = 
        dao.getBudgetsByMonth(monthId)

    override suspend fun insertBudget(budget: BudgetEntity) {
        dao.insertBudget(budget)
    }

    override suspend fun deleteBudget(budget: BudgetEntity) {
        dao.deleteBudget(budget)
    }

    override suspend fun insertCategory(category: CategoryEntity) {
        dao.insertCategory(category)
    }

    override suspend fun deleteCategory(category: CategoryEntity) {
        dao.deleteCategory(category)
    }

    override fun getSpentAmountByCategory(catId: Long): Flow<Long?> =
        dao.getSpentAmountByCategory(catId)
}
