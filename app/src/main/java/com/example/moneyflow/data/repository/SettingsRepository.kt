package com.example.moneyflow.data.repository

import com.example.moneyflow.data.local.entity.MonthlyBalanceEntity
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val userName: Flow<String>
    val avatarUri: Flow<String?>
    val isFirstLaunch: Flow<Boolean>
    val isDarkMode: Flow<Boolean?>
    val language: Flow<String>
    val totalBalance: Flow<Long>
    val initialBalance: Flow<Long>

    suspend fun updateUserName(name: String)
    suspend fun saveAvatarUri(uri: String)
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun saveLanguage(lang: String)
    suspend fun saveOnboardingData(name: String, balance: Long)
    suspend fun clearAllData()
    
    fun getMonthlyBalance(monthId: String): Flow<MonthlyBalanceEntity?>
    suspend fun insertMonthlyBalance(monthlyBalance: MonthlyBalanceEntity)
}
