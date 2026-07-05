package com.example.moneyflow.features.budget.domain.usecase

import app.cash.turbine.test
import com.example.moneyflow.data.local.entity.BudgetEntity
import com.example.moneyflow.data.repository.BudgetRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetBudgetsByMonthUseCaseTest {

    private lateinit var useCase: GetBudgetsByMonthUseCase
    private val repository: BudgetRepository = mockk()

    @Before
    fun setUp() {
        useCase = GetBudgetsByMonthUseCase(repository)
    }

    @Test
    fun `invoke returns budgets from repository`() = runTest {
        val budgets = listOf(BudgetEntity(id = 1, name = "Test", amount = 100L, color = 0))
        every { repository.getBudgetsByMonth("06-2026") } returns flowOf(budgets)

        useCase("06-2026").test {
            assertEquals(budgets, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
