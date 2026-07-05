package com.example.moneyflow.data.repository

import app.cash.turbine.test
import com.example.moneyflow.data.local.dao.FinlyDao
import com.example.moneyflow.data.local.entity.BudgetEntity
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BudgetRepositoryImplTest {

    private lateinit var repository: BudgetRepositoryImpl
    private val dao: FinlyDao = mockk()

    @Before
    fun setUp() {
        every { dao.getAllBudgets() } returns flowOf(emptyList())
        every { dao.getAllCategories() } returns flowOf(emptyList())
        repository = BudgetRepositoryImpl(dao)
    }

    @Test
    fun `allBudgets returns flow from dao`() = runTest {
        val budgets = listOf(BudgetEntity(id = 1, name = "Food", amount = 500L, color = 0))
        every { dao.getAllBudgets() } returns flowOf(budgets)
        
        val repo = BudgetRepositoryImpl(dao)
        repo.allBudgets.test {
            assertEquals(budgets, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getBudgetsByMonth returns flow from dao`() = runTest {
        val budgets = listOf(BudgetEntity(id = 1, name = "Food", amount = 500L, color = 0, monthId = "06-2026"))
        every { dao.getBudgetsByMonth("06-2026") } returns flowOf(budgets)

        repository.getBudgetsByMonth("06-2026").test {
            assertEquals(budgets, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertBudget calls dao`() = runTest {
        val budget = BudgetEntity(id = 1, name = "Food", amount = 500L, color = 0)
        coEvery { dao.insertBudget(any()) } just Runs

        repository.insertBudget(budget)

        coVerify { dao.insertBudget(budget) }
    }

    @Test
    fun `deleteBudget calls dao`() = runTest {
        val budget = BudgetEntity(id = 1, name = "Food", amount = 500L, color = 0)
        coEvery { dao.deleteBudget(any()) } just Runs

        repository.deleteBudget(budget)

        coVerify { dao.deleteBudget(budget) }
    }
}
