package com.example.moneyflow.features.settings.domain.usecase

import com.example.moneyflow.data.repository.SettingsRepository
import com.example.moneyflow.data.repository.TransactionRepository
import com.example.moneyflow.data.repository.BudgetRepository
import com.example.moneyflow.features.dashboard.domain.repository.FinancialCycleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import android.content.Context
import com.example.moneyflow.data.local.entity.BudgetEntity
import com.example.moneyflow.data.local.entity.TransactionEntity
import com.example.moneyflow.data.local.entity.TransactionType
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

data class SettingsUseCases @Inject constructor(
    val updateUserName: UpdateUserNameUseCase,
    val toggleDarkMode: ToggleDarkModeUseCase,
    val updateLanguage: UpdateLanguageUseCase,
    val clearAllData: ClearAllDataUseCase,
    val getSettingsMetadata: GetSettingsMetadataUseCase,
    val exportBackup: ExportBackupUseCase,
    val importBackup: ImportBackupUseCase,
    val updateAvatar: UpdateAvatarUseCase,
    val saveCycleStartDay: SaveCycleStartDayUseCase
)

class SaveCycleStartDayUseCase @Inject constructor(private val repository: FinancialCycleRepository) {
    suspend operator fun invoke(day: Int) = repository.saveCycleStartDay(day)
}

class UpdateUserNameUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(name: String) = repository.updateUserName(name)
}

class ToggleDarkModeUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDarkMode(enabled)
}

class UpdateLanguageUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(lang: String) = repository.saveLanguage(lang)
}

class ClearAllDataUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke() = repository.clearAllData()
}

class GetSettingsMetadataUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val cycleRepository: FinancialCycleRepository
) {
    val userName: Flow<String> = settingsRepository.userName
    val isDarkMode: Flow<Boolean?> = settingsRepository.isDarkMode
    val avatarUri: Flow<String?> = settingsRepository.avatarUri
    val language: Flow<String> = settingsRepository.language
    val cycleStartDay: Flow<Int> = cycleRepository.getCycleStartDay()
}

class ExportBackupUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(context: Context): Boolean {
        return try {
            val transactions = transactionRepository.allTransactions.first()
            val budgetsList = budgetRepository.allBudgets.first()
            val backupJson = JSONObject().apply {
                put("transactions", JSONArray().apply {
                    transactions.forEach { tx ->
                        put(JSONObject().apply {
                            put("title", tx.title)
                            put("amount", tx.amount)
                            put("type", tx.type.name)
                            put("date", tx.date)
                            put("categoryId", tx.categoryId)
                            put("budgetId", tx.budgetId ?: -1L)
                            put("icon", tx.icon)
                        })
                    }
                })
                put("budgets", JSONArray().apply {
                    budgetsList.forEach { b ->
                        put(JSONObject().apply {
                            put("name", b.name)
                            put("amount", b.amount)
                            put("color", b.color)
                            put("icon", b.icon)
                            put("monthId", b.monthId)
                        })
                    }
                })
            }
            val file = File(context.getExternalFilesDir(null), "moneyflow_backup.json")
            FileOutputStream(file).use { it.write(backupJson.toString().toByteArray()) }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

class ImportBackupUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(context: Context, uri: android.net.Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val jsonString = inputStream?.bufferedReader()?.use { it.readText() } ?: return false
            val backupJson = JSONObject(jsonString)
            val budgetsArray = backupJson.optJSONArray("budgets")
            budgetsArray?.let {
                for (i in 0 until it.length()) {
                    val obj = it.getJSONObject(i)
                    budgetRepository.insertBudget(BudgetEntity(
                        name = obj.getString("name"),
                        amount = obj.getLong("amount"),
                        color = obj.getInt("color"),
                        icon = obj.getString("icon"),
                        monthId = obj.optString("monthId", "")
                    ))
                }
            }
            val transactionsArray = backupJson.optJSONArray("transactions")
            transactionsArray?.let {
                for (i in 0 until it.length()) {
                    val obj = it.getJSONObject(i)
                    val bId = obj.optLong("budgetId", -1L)
                    transactionRepository.insertTransaction(TransactionEntity(
                        title = obj.getString("title"),
                        amount = obj.getLong("amount"),
                        type = TransactionType.valueOf(obj.getString("type")),
                        date = obj.getLong("date"),
                        categoryId = obj.getLong("categoryId"),
                        budgetId = if (bId == -1L) null else bId,
                        icon = obj.optString("icon", "wallet")
                    ))
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

class UpdateAvatarUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(context: Context, uri: android.net.Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return
            val file = File(context.filesDir, "user_avatar.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.use { input -> outputStream.use { output -> input.copyTo(output) } }
            repository.saveAvatarUri(file.absolutePath)
        } catch (e: Exception) { e.printStackTrace() }
    }
}
