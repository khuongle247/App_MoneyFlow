package com.example.moneyflow.data.local.dao

import androidx.room.*
import com.example.moneyflow.data.local.entity.BudgetEntity
import com.example.moneyflow.data.local.entity.CategoryEntity
import com.example.moneyflow.data.local.entity.MonthlyBalanceEntity
import com.example.moneyflow.data.local.entity.SavingGoalEntity
import com.example.moneyflow.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinlyDao {
    // Budgets (Envelopes)
    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE monthId = :monthId OR monthId = ''")
    fun getBudgetsByMonth(monthId: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    // ... (existing code)
    
    // Saving Goals
    @Query("SELECT * FROM saving_goals")
    fun getAllSavingGoals(): Flow<List<SavingGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingGoal(goal: SavingGoalEntity)

    @Delete
    suspend fun deleteSavingGoal(goal: SavingGoalEntity)

    // Categories
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    // Transactions
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE categoryId = :catId ORDER BY date DESC")
    fun getTransactionsByCategory(catId: Long): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM budgets")
    suspend fun deleteAllBudgets()

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()

    @Query("DELETE FROM saving_goals")
    suspend fun deleteAllSavingGoals()

    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getTransactionsInRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE categoryId = :catId")
    fun getSpentAmountByCategory(catId: Long): Flow<Long?>

    // Monthly Balances
    @Query("SELECT * FROM monthly_balances WHERE monthId = :monthId")
    fun getMonthlyBalance(monthId: String): Flow<MonthlyBalanceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyBalance(monthlyBalance: MonthlyBalanceEntity)
}
