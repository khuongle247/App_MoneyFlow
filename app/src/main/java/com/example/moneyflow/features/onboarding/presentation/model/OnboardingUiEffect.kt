package com.example.moneyflow.features.onboarding.presentation.model

sealed interface OnboardingEffect {
    data class Navigate(val route: String) : OnboardingEffect
}
