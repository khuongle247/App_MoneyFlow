package com.example.moneyflow.features.dashboard.presentation.viewmodel

import app.cash.turbine.test
import com.example.moneyflow.MainDispatcherRule
import com.example.moneyflow.data.repository.SettingsRepository
import com.example.moneyflow.features.dashboard.domain.model.DashboardSummary
import com.example.moneyflow.features.dashboard.domain.usecase.DashboardUseCases
import com.example.moneyflow.features.dashboard.presentation.model.DashboardEvent
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: DashboardViewModel
    private val dashboardUseCases: DashboardUseCases = mockk()
    private val settingsRepository: SettingsRepository = mockk()

    @Before
    fun setUp() {
        val summary = DashboardSummary(100L, 50L, 30L, 70L, true)
        every { dashboardUseCases.getSummary(any()) } returns flowOf(summary)
        every { dashboardUseCases.getTransactions(any()) } returns flowOf(emptyList())
        every { settingsRepository.userName } returns flowOf("User")
        every { settingsRepository.avatarUri } returns flowOf(null)

        viewModel = DashboardViewModel(dashboardUseCases, settingsRepository)
    }

    @Test
    fun `contentState provides formatted financial data`() = runTest {
        viewModel.contentState.test {
            val state = awaitItem()
            assertEquals("User", state.userName)
            assertTrue(state.totalAssetText.contains("100"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onEvent OnChangeMonth updates filterState`() = runTest {
        val newMonth = "07-2026"
        viewModel.onEvent(DashboardEvent.OnChangeMonth(newMonth))

        viewModel.filterState.test {
            val state = awaitItem()
            assertEquals(newMonth, state.selectedMonthId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
