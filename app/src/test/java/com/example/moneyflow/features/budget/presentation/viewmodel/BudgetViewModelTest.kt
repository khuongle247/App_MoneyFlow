package com.example.moneyflow.features.budget.presentation.viewmodel

import app.cash.turbine.test
import com.example.moneyflow.MainDispatcherRule
import com.example.moneyflow.features.budget.domain.usecase.BudgetUseCases
import com.example.moneyflow.features.dashboard.domain.model.DashboardSummary
import com.example.moneyflow.features.dashboard.domain.usecase.GetDashboardSummaryUseCase
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: BudgetViewModel
    private val budgetUseCases: BudgetUseCases = mockk()
    private val getDashboardSummaryUseCase: GetDashboardSummaryUseCase = mockk()

    @Before
    fun setUp() {
        val summary = DashboardSummary(100L, 50L, 30L, 70L, true)
        every { getDashboardSummaryUseCase(any()) } returns flowOf(summary)
        every { budgetUseCases.getBudgetsByMonth(any()) } returns flowOf(emptyList())
        every { budgetUseCases.getBudgetSpending(any()) } returns flowOf(emptyMap())

        viewModel = BudgetViewModel(budgetUseCases, getDashboardSummaryUseCase)
    }

    @Test
    fun `contentState accurately maps total spent from summary`() = runTest {
        viewModel.contentState.test {
            // summary.totalSpent = 50L, Formatter.formatCurrency(50L) contains "50"
            val state = awaitItem()
            assert(state.totalSpentText.contains("50"))
            cancelAndIgnoreRemainingEvents()
        }
    }
}
