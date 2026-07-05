package com.example.moneyflow.features.onboarding.domain.usecase

import app.cash.turbine.test
import com.example.moneyflow.data.repository.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CheckFirstLaunchUseCaseTest {

    private lateinit var useCase: CheckFirstLaunchUseCase
    private val repository: SettingsRepository = mockk()

    @Before
    fun setUp() {
        useCase = CheckFirstLaunchUseCase(repository)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        every { repository.isFirstLaunch } returns flowOf(true)

        useCase().test {
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
