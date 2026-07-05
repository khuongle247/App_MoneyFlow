package com.example.moneyflow.features.transaction.presentation.model

import com.example.moneyflow.features.dashboard.presentation.model.TransactionUiModel
import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.data.local.entity.CategoryEntity
import com.example.moneyflow.data.local.entity.BudgetEntity

// Level 1: UI Control State
data class TransactionScreenState(
    val isLoading: Boolean = false,
    val error: String? = null
)

// Level 2: Data Display State
data class TransactionContentState(
    val transactions: List<TransactionUiModel> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val budgets: List<BudgetEntity> = emptyList()
)

// Level 3: Input/Filter/Form State
data class TransactionFilterState(
    val selectedMonthId: String = "",
    val searchQuery: String = "",
    val typeFilter: TransactionType? = null,
    
    // Form fields for Add/Edit
    val title: String = "",
    val amountText: String = "",
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val selectedBudgetId: Long? = null,
    val selectedIcon: String = "wallet"
)

// Wrapper
data class TransactionUiState(
    val screen: TransactionScreenState = TransactionScreenState(),
    val content: TransactionContentState = TransactionContentState(),
    val filter: TransactionFilterState = TransactionFilterState()
)

sealed interface TransactionEvent {
    // History Events
    data class OnSearchChanged(val query: String) : TransactionEvent
    data class OnMonthChanged(val monthId: String) : TransactionEvent
    data class OnDeleteTransaction(val id: Long) : TransactionEvent
    
    // Form Events
    data class OnTitleChanged(val title: String) : TransactionEvent
    data class OnAmountChanged(val amount: String) : TransactionEvent
    data class OnTypeChanged(val type: TransactionType) : TransactionEvent
    data class OnBudgetSelected(val id: Long?) : TransactionEvent
    data class OnIconSelected(val icon: String) : TransactionEvent
    object OnSaveTransaction : TransactionEvent
}
