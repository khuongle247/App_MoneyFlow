package com.example.moneyflow.features.dashboard.presentation.model

import com.example.moneyflow.features.dashboard.domain.usecase.FinancialCycleStatus

data class TransactionUiModel(
    val id: Long,
    val title: String,
    val amountText: String,
    val dateText: String,
    val iconName: String,
    val isExpense: Boolean
)

// Level 1: UI Control State
data class DashboardScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val showInitialBalanceDialog: Boolean = false,
    val cycleStatus: FinancialCycleStatus? = null
)

// Level 2: Data Display State
data class DashboardContentState(
    val userName: String = "Người dùng",
    val avatarUri: String? = null,
    val todayLabel: String = "",
    val totalAssetText: String = "0 ₫",
    val totalIncomeText: String = "0 ₫",
    val totalSpentText: String = "0 ₫",
    val totalBudgetText: String = "0 ₫",
    val availableText: String = "0 ₫",
    val recentTransactions: List<TransactionUiModel> = emptyList()
)

// Level 3: Input/Filter State
data class DashboardFilterState(
    val selectedMonthId: String = "",
    val selectedMonthLabel: String = ""
)

// Root Wrapper
data class DashboardUiState(
    val screen: DashboardScreenState = DashboardScreenState(),
    val content: DashboardContentState = DashboardContentState(),
    val filter: DashboardFilterState = DashboardFilterState()
)

sealed interface DashboardEvent {
    data class OnChangeMonth(val monthId: String) : DashboardEvent
    data class OnSetInitialBalance(val amount: Double) : DashboardEvent
    object OnDismissBalanceDialog : DashboardEvent
    object OnRefresh : DashboardEvent
    data class OnTransactionClick(val id: Long = -1L) : DashboardEvent
    object OnSeeAllClick : DashboardEvent
    object OnCalendarClick : DashboardEvent
    object OnNotificationClick : DashboardEvent
    data class OnStartNewCycle(val initialBalance: Long) : DashboardEvent
}

sealed interface DashboardEffect {
    data class ShowSnackbar(val message: String) : DashboardEffect
    data class NavigateToTransaction(val id: Long = -1L) : DashboardEffect
    object NavigateToHistory : DashboardEffect
    object NavigateToCalendar : DashboardEffect
    object NavigateToNotifications : DashboardEffect
}
