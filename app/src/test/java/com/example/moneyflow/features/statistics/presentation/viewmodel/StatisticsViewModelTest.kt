package com.example.moneyflow.features.statistics.presentation.viewmodel

import app.cash.turbine.test
import com.example.moneyflow.MainDispatcherRule
import com.example.moneyflow.features.budget.domain.usecase.BudgetUseCases
import com.example.moneyflow.features.statistics.domain.usecase.GetStatisticsSummaryUseCase
import com.example.moneyflow.features.statistics.domain.usecase.StatisticsSummary
import com.example.moneyflow.features.statistics.domain.usecase.StatisticsUseCases
import com.example.moneyflow.features.statistics.presentation.model.StatisticsEvent
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: StatisticsViewModel
    private val statisticsUseCases: StatisticsUseCases = mockk()
    private val budgetUseCases: BudgetUseCases = mockk()

    @Before
    fun setUp() {
        val summary = StatisticsSummary(500L, emptyMap(), emptyList())
        every { statisticsUseCases.getStatisticsSummary(any()) } returns flowOf(summary)
        viewModel = StatisticsViewModel(statisticsUseCases, budgetUseCases)
    }

    @Test
    fun `OnChangeMonth updates filterState and contentState`() = runTest {
        val newMonth = "07-2026"
        val newSummary = StatisticsSummary(1000L, emptyMap(), emptyList())
        every { statisticsUseCases.getStatisticsSummary(newMonth) } returns flowOf(newSummary)

        viewModel.onEvent(StatisticsEvent.OnChangeMonth(newMonth))

        viewModel.filterState.test {
            assertEquals(newMonth, awaitItem().selectedMonthId)
        }
        
        viewModel.contentState.test {
            val state = awaitItem()
            assert(state.totalSpentText.contains("1.000"))
        }
    }
}
