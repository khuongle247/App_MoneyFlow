package com.example.moneyflow.features.budget.domain.usecase

import app.cash.turbine.test
import com.example.moneyflow.data.local.entity.TransactionEntity
import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.data.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*

class GetBudgetSpendingUseCaseTest {

    private lateinit var useCase: GetBudgetSpendingUseCase
    private val repository: TransactionRepository = mockk()

    @Before
    fun setUp() {
        useCase = GetBudgetSpendingUseCase(repository)
    }

    @Test
    fun `invoke returns correct spending map`() = runTest {
        val monthId = "06-2026"
        val calendar = Calendar.getInstance()
        calendar.set(2026, 5, 20)
        val timestamp = calendar.timeInMillis

        val transactions = listOf(
            TransactionEntity(id = 1, title = "Lunch", amount = -50L, type = TransactionType.EXPENSE, categoryId = 1, budgetId = 10L, date = timestamp),
            TransactionEntity(id = 2, title = "Coffee", amount = -10L, type = TransactionType.EXPENSE, categoryId = 1, budgetId = 10L, date = timestamp),
            TransactionEntity(id = 3, title = "Salary", amount = 1000L, type = TransactionType.INCOME, categoryId = 2, date = timestamp)
        )

        every { repository.allTransactions } returns flowOf(transactions)

        useCase(monthId).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals(60L, result[10L] ?: 0L)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
