package com.example.moneyflow.data.repository

import com.example.moneyflow.data.local.FinlyDataStore
import com.example.moneyflow.data.local.dao.FinlyDao
import com.example.moneyflow.data.local.entity.MonthlyBalanceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dao: FinlyDao,
    private val dataStore: FinlyDataStore
) : SettingsRepository {

    override val userName: Flow<String> = dataStore.userName
    override val avatarUri: Flow<String?> = dataStore.avatarUri
    override val isFirstLaunch: Flow<Boolean> = dataStore.isFirstLaunch
    override val isDarkMode: Flow<Boolean?> = dataStore.isDarkMode
    override val language: Flow<String> = dataStore.language
    override val totalBalance: Flow<Long> = dataStore.totalBalanceLong
    override val initialBalance: Flow<Long> = dataStore.initialBalanceLong

    override suspend fun updateUserName(name: String) = dataStore.updateUserName(name)

    override suspend fun saveAvatarUri(uri: String) = dataStore.saveAvatarUri(uri)

    override suspend fun setDarkMode(enabled: Boolean) = dataStore.setDarkMode(enabled)

    override suspend fun saveLanguage(lang: String) = dataStore.saveLanguage(lang)

    override suspend fun saveOnboardingData(name: String, balance: Long) = 
        dataStore.saveOnboardingData(name, balance)

    override suspend fun clearAllData() {
        dao.deleteAllTransactions()
        dao.deleteAllBudgets()
        dao.deleteAllCategories()
        dao.deleteAllSavingGoals()
        dataStore.saveInitialBalance(0L)
        dataStore.saveTotalBalance(0L)
        dataStore.setFirstLaunch(true)
    }

    override fun getMonthlyBalance(monthId: String): Flow<MonthlyBalanceEntity?> = 
        dao.getMonthlyBalance(monthId)

    override suspend fun insertMonthlyBalance(monthlyBalance: MonthlyBalanceEntity) = 
        dao.insertMonthlyBalance(monthlyBalance)
}
