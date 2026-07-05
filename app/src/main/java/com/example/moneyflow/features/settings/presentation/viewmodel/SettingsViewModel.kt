package com.example.moneyflow.features.settings.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.features.settings.domain.usecase.SettingsUseCases
import com.example.moneyflow.features.settings.presentation.model.SettingsEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCases: SettingsUseCases
) : ViewModel() {

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect = _effect.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val userName: StateFlow<String> = settingsUseCases.getSettingsMetadata.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Người dùng")
    val isDarkMode: StateFlow<Boolean?> = settingsUseCases.getSettingsMetadata.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val avatarUri: StateFlow<String?> = settingsUseCases.getSettingsMetadata.avatarUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val language: StateFlow<String> = settingsUseCases.getSettingsMetadata.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "vi")
    val cycleStartDay: StateFlow<Int> = settingsUseCases.getSettingsMetadata.cycleStartDay
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    fun updateLanguage(lang: String) { viewModelScope.launch { settingsUseCases.updateLanguage(lang) } }
    fun updateUserName(name: String) { viewModelScope.launch { settingsUseCases.updateUserName(name) } }
    fun toggleDarkMode(enabled: Boolean) { viewModelScope.launch { settingsUseCases.toggleDarkMode(enabled) } }
    fun saveCycleStartDay(day: Int) { viewModelScope.launch { settingsUseCases.saveCycleStartDay(day) } }
    
    fun updateAvatar(context: Context, uri: android.net.Uri) {
        viewModelScope.launch {
            settingsUseCases.updateAvatar(context, uri)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            settingsUseCases.clearAllData()
            _effect.emit(SettingsEffect.NavigateToOnboarding)
        }
    }

    fun exportBackup(context: Context, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = settingsUseCases.exportBackup(context)
            _isLoading.value = false
            if (success) {
                _effect.emit(SettingsEffect.ShowToast("Sao lưu thành công"))
            } else {
                _effect.emit(SettingsEffect.ShowToast("Sao lưu thất bại"))
            }
            onComplete(success)
        }
    }

    fun importBackup(context: Context, uri: android.net.Uri, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = settingsUseCases.importBackup(context, uri)
            _isLoading.value = false
            if (success) {
                _effect.emit(SettingsEffect.ShowToast("Khôi phục thành công"))
            } else {
                _effect.emit(SettingsEffect.ShowToast("Khôi phục thất bại"))
            }
            onComplete(success)
        }
    }
}
