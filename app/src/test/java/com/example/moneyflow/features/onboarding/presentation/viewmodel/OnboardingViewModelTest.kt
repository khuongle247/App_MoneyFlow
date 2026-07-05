package com.example.moneyflow.features.onboarding.presentation.viewmodel

import app.cash.turbine.test
import com.example.moneyflow.MainDispatcherRule
import com.example.moneyflow.features.onboarding.domain.usecase.OnboardingUseCases
import com.example.moneyflow.features.onboarding.presentation.model.OnboardingEffect
import com.example.moneyflow.ui.navigation.AppRoute
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
class OnboardingViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: OnboardingViewModel
    private val useCases: OnboardingUseCases = mockk()

    @Before
    fun setUp() {
        every { useCases.checkFirstLaunch() } returns flowOf(true)
        viewModel = OnboardingViewModel(useCases)
    }

    @Test
    fun `startApp navigates to Onboarding when first launch is true`() = runTest {
        every { useCases.checkFirstLaunch() } returns flowOf(true)

        viewModel.effect.test {
            viewModel.startApp()
            val effect = awaitItem()
            assert(effect is OnboardingEffect.Navigate && effect.route == AppRoute.Onboarding.route)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startApp navigates to Dashboard when first launch is false`() = runTest {
        every { useCases.checkFirstLaunch() } returns flowOf(false)

        viewModel.effect.test {
            viewModel.startApp()
            val effect = awaitItem()
            assert(effect is OnboardingEffect.Navigate && effect.route == AppRoute.Dashboard.route)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
