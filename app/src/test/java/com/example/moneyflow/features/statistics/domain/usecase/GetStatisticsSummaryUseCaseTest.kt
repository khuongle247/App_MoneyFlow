package com.example.moneyflow.features.statistics.domain.usecase

import app.cash.turbine.test
import com.example.moneyflow.data.local.entity.BudgetEntity
import com.example.moneyflow.data.local.entity.TransactionEntity
import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.data.repository.BudgetRepository
import com.example.moneyflow.data.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

class GetStatisticsSummaryUseCaseTest {

    private lateinit var useCase: GetStatisticsSummaryUseCase
    private val transactionRepository: TransactionRepository = mockk()
    private val budgetRepository: BudgetRepository = mockk()

    @Before
    fun setUp() {
        useCase = GetStatisticsSummaryUseCase(transactionRepository, budgetRepository)
    }

    @Test
    fun `invoke aggregates transactions by budget correctly using Long`() = runTest {
        val monthId = "06-2026"
        val calendar = Calendar.getInstance()
        calendar.set(2026, 5, 10) // June 2026
        val timestamp = calendar.timeInMillis

        val budgets = listOf(
            BudgetEntity(id = 1, name = "Food", amount = 1000L, color = 0, monthId = monthId),
            BudgetEntity(id = 2, name = "Travel", amount = 500L, color = 1, monthId = monthId)
        )
        val transactions = listOf(
            TransactionEntity(id = 1, title = "Lunch", amount = -100L, type = TransactionType.EXPENSE, categoryId = 1, budgetId = 1, date = timestamp),
            TransactionEntity(id = 2, title = "Dinner", amount = -200L, type = TransactionType.EXPENSE, categoryId = 1, budgetId = 1, date = timestamp),
            TransactionEntity(id = 3, title = "Bus", amount = -50L, type = TransactionType.EXPENSE, categoryId = 2, budgetId = 2, date = timestamp),
            TransactionEntity(id = 4, title = "Salary", amount = 5000L, type = TransactionType.INCOME, categoryId = 3, date = timestamp)
        )

        every { transactionRepository.allTransactions } returns flowOf(transactions)
        every { budgetRepository.getBudgetsByMonth(monthId) } returns flowOf(budgets)

        useCase(monthId).test {
            val summary = awaitItem()
            // Total spent should be sum of absolute expenses: 100 + 200 + 50 = 350
            assertEquals(350L, summary.totalSpent)
            // Spending Map check
            assertEquals(300L, summary.spendingMap[budgets[0]])
            assertEquals(50L, summary.spendingMap[budgets[1]])
            cancelAndIgnoreRemainingEvents()
        }
    }
}
