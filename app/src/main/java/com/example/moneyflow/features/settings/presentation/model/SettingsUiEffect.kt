package com.example.moneyflow.features.settings.presentation.model

sealed interface SettingsEffect {
    data class ShowToast(val message: String) : SettingsEffect
    object NavigateToOnboarding : SettingsEffect
}
