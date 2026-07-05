package com.example.moneyflow.features.budget.presentation.model

import androidx.compose.ui.graphics.Color

// UI Model cho từng dòng ngân sách
data class BudgetUiModel(
    val id: Long,
    val name: String,
    val amountText: String,
    val spentText: String,
    val remainingText: String,
    val progress: Float,
    val progressColor: Color,
    val iconName: String,
    val color: Int
)

// Level 1: UI Control State (Điều khiển giao diện)
data class BudgetScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddBudgetDialog: Boolean = false
)

// Level 2: Data Display State (Dữ liệu hiển thị)
data class BudgetContentState(
    val totalBudgetText: String = "0 ₫",
    val totalSpentText: String = "0 ₫",
    val remainingText: String = "0 ₫",
    val budgetList: List<BudgetUiModel> = emptyList()
)

// Level 3: Input Filter State (Bộ lọc/Input)
data class BudgetFilterState(
    val selectedMonthId: String = "",
    val selectedMonthLabel: String = ""
)

// Root Wrapper
data class BudgetUiState(
    val screen: BudgetScreenState = BudgetScreenState(),
    val content: BudgetContentState = BudgetContentState(),
    val filter: BudgetFilterState = BudgetFilterState()
)

sealed interface BudgetEvent {
    data class OnChangeMonth(val monthId: String) : BudgetEvent
    data class OnAddBudget(val name: String, val amount: Double, val color: Int, val icon: String) : BudgetEvent
    data class OnDeleteBudget(val id: Long) : BudgetEvent
    object OnNavigateToAddTransaction : BudgetEvent
    object OnShowAddDialog : BudgetEvent
    object OnDismissAddDialog : BudgetEvent
}

sealed interface BudgetEffect {
    object NavigateToAddTransaction : BudgetEffect
    data class ShowSnackbar(val message: String) : BudgetEffect
}
