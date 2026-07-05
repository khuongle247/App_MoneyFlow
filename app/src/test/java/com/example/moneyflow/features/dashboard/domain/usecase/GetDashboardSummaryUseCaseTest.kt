package com.example.moneyflow.features.dashboard.domain.usecase

import app.cash.turbine.test
import com.example.moneyflow.data.local.entity.BudgetEntity
import com.example.moneyflow.data.local.entity.MonthlyBalanceEntity
import com.example.moneyflow.data.local.entity.TransactionEntity
import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.data.repository.BudgetRepository
import com.example.moneyflow.data.repository.SettingsRepository
import com.example.moneyflow.data.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

class GetDashboardSummaryUseCaseTest {

    private lateinit var useCase: GetDashboardSummaryUseCase
    private val transactionRepository: TransactionRepository = mockk()
    private val budgetRepository: BudgetRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()

    @Before
    fun setUp() {
        useCase = GetDashboardSummaryUseCase(transactionRepository, budgetRepository, settingsRepository)
    }

    @Test
    fun `invoke returns correct summary`() = runTest {
        val monthId = "06-2026"
        val calendar = Calendar.getInstance()
        calendar.set(2026, 5, 20) // June 2026
        val timestamp = calendar.timeInMillis

        val transactions = listOf(
            TransactionEntity(id = 1, title = "Income", amount = 1000L, type = TransactionType.INCOME, categoryId = 1, date = timestamp),
            TransactionEntity(id = 2, title = "Expense", amount = -500L, type = TransactionType.EXPENSE, categoryId = 2, date = timestamp, budgetId = 1)
        )
        val monthlyBalance = MonthlyBalanceEntity(monthId, 2000L)
        val budgets = listOf(BudgetEntity(id = 1, name = "Food", amount = 300L, color = 0, monthId = monthId))

        every { transactionRepository.allTransactions } returns flowOf(transactions)
        every { settingsRepository.getMonthlyBalance(monthId) } returns flowOf(monthlyBalance)
        every { budgetRepository.getBudgetsByMonth(monthId) } returns flowOf(budgets)

        useCase(monthId).test {
            val summary = awaitItem()
            assertEquals(2500L, summary.totalAsset)
            assertEquals(500L, summary.totalSpent)
            assertEquals(300L, summary.totalBudgetAllocated)
            assertEquals(2200L, summary.availableToBudget)
            assertEquals(true, summary.isBalanceSet)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
