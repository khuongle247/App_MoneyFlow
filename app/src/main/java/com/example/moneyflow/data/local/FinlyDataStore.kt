package com.example.moneyflow.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "finly_prefs")

class FinlyDataStore(private val context: Context) {
    companion object {
        private val TOTAL_BALANCE_DOUBLE = doublePreferencesKey("total_balance")
        private val INITIAL_BALANCE_DOUBLE = doublePreferencesKey("initial_balance")

        val TOTAL_BALANCE = longPreferencesKey("total_balance_long")
        val INITIAL_BALANCE = longPreferencesKey("initial_balance_long")
        
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val USER_NAME = stringPreferencesKey("user_name")
        val AVATAR_URI = stringPreferencesKey("avatar_uri")
        val LANGUAGE = stringPreferencesKey("language")
        val CYCLE_START_DAY = intPreferencesKey("cycle_start_day")
    }

    val cycleStartDay: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[CYCLE_START_DAY] ?: 1
    }

    suspend fun saveCycleStartDay(day: Int) {
        context.dataStore.edit { prefs ->
            prefs[CYCLE_START_DAY] = day
        }
    }

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE] ?: "vi"
    }

    suspend fun saveLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE] = lang
        }
    }

    val avatarUri: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[AVATAR_URI]
    }

    val totalBalanceLong: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[TOTAL_BALANCE] ?: prefs[TOTAL_BALANCE_DOUBLE]?.toLong() ?: 0L
    }

    val initialBalanceLong: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[INITIAL_BALANCE] ?: prefs[INITIAL_BALANCE_DOUBLE]?.toLong() ?: 0L
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME] ?: "Người dùng"
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_FIRST_LAUNCH] ?: true
    }

    val isDarkMode: Flow<Boolean?> = context.dataStore.data.map { prefs ->
        prefs[IS_DARK_MODE]
    }

    suspend fun saveOnboardingData(name: String, balance: Long) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[INITIAL_BALANCE] = balance
            prefs[IS_FIRST_LAUNCH] = false
        }
    }

    suspend fun updateUserName(name: String) {
        context.dataStore.edit { it[USER_NAME] = name }
    }

    suspend fun saveInitialBalance(balance: Long) {
        context.dataStore.edit { prefs ->
            prefs[INITIAL_BALANCE] = balance
            prefs[IS_FIRST_LAUNCH] = false
        }
    }

    suspend fun saveTotalBalance(balance: Long) {
        context.dataStore.edit { prefs ->
            prefs[TOTAL_BALANCE] = balance
        }
    }

    suspend fun updateBalance(amount: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[TOTAL_BALANCE] ?: prefs[TOTAL_BALANCE_DOUBLE]?.toLong() ?: 0L
            prefs[TOTAL_BALANCE] = current + amount
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_DARK_MODE] = enabled
        }
    }

    suspend fun setFirstLaunch(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_FIRST_LAUNCH] = enabled
        }
    }

    suspend fun saveAvatarUri(uri: String) {
        context.dataStore.edit { prefs ->
            prefs[AVATAR_URI] = uri
        }
    }

    val totalBalance: Flow<Double> = totalBalanceLong.map { it.toDouble() }
    val initialBalance: Flow<Double> = initialBalanceLong.map { it.toDouble() }
}
