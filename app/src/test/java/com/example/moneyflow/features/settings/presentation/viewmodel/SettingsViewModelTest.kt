package com.example.moneyflow.features.settings.presentation.viewmodel

import app.cash.turbine.test
import com.example.moneyflow.MainDispatcherRule
import com.example.moneyflow.features.settings.domain.usecase.SettingsUseCases
import com.example.moneyflow.features.settings.presentation.model.SettingsEffect
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
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SettingsViewModel
    private val useCases: SettingsUseCases = mockk()

    @Before
    fun setUp() {
        every { useCases.getSettingsMetadata.userName } returns flowOf("Tester")
        every { useCases.getSettingsMetadata.isDarkMode } returns flowOf(false)
        every { useCases.getSettingsMetadata.avatarUri } returns flowOf(null)
        every { useCases.getSettingsMetadata.language } returns flowOf("vi")
        
        viewModel = SettingsViewModel(useCases)
    }

    @Test
    fun `clearAllData triggers usecase and emits NavigateToOnboarding`() = runTest {
        coEvery { useCases.clearAllData() } just Runs

        viewModel.effect.test {
            viewModel.clearAllData()
            
            coVerify { useCases.clearAllData() }
            assertTrue(awaitItem() is SettingsEffect.NavigateToOnboarding)
        }
    }

    @Test
    fun `importBackup failure emits ShowToast with error message`() = runTest {
        val mockContext = mockk<android.content.Context>()
        val mockUri = mockk<android.net.Uri>()
        coEvery { useCases.importBackup(any(), any()) } returns false

        viewModel.effect.test {
            viewModel.importBackup(mockContext, mockUri) { }
            
            val effect = awaitItem()
            assertTrue(effect is SettingsEffect.ShowToast)
            assertEquals("Khôi phục thất bại", (effect as SettingsEffect.ShowToast).message)
        }
    }
}
