package com.example.moneyflow.features.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.features.onboarding.domain.usecase.OnboardingUseCases
import com.example.moneyflow.features.onboarding.presentation.model.OnboardingEffect
import com.example.moneyflow.ui.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingUseCases: OnboardingUseCases
) : ViewModel() {

    private val _effect = MutableSharedFlow<OnboardingEffect>()
    val effect = _effect.asSharedFlow()

    val isFirstLaunch: StateFlow<Boolean> = onboardingUseCases.checkFirstLaunch()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun startApp() {
        viewModelScope.launch {
            val isFirst = onboardingUseCases.checkFirstLaunch().first()
            val destination = if (isFirst) AppRoute.Onboarding.route else AppRoute.Dashboard.route
            _effect.emit(OnboardingEffect.Navigate(destination))
        }
    }

    fun completeOnboarding(name: String, balance: Double) {
        viewModelScope.launch {
            onboardingUseCases.completeOnboarding(name, balance.toLong())
            _effect.emit(OnboardingEffect.Navigate(AppRoute.Dashboard.route))
        }
    }
}
