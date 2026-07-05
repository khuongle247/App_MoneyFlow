package com.example.moneyflow.features.statistics.presentation.model

import com.example.moneyflow.features.budget.presentation.model.BudgetUiModel

// Level 1: UI Control State
data class StatisticsScreenState(
    val isLoading: Boolean = false,
    val error: String? = null
)

// Level 2: Data Display State
data class StatisticsContentState(
    val totalSpentText: String = "0 ₫",
    val budgetList: List<BudgetUiModel> = emptyList(),
    val spendingMap: Map<Long, Long> = emptyMap()
)

// Level 3: Filter/Input State
data class StatisticsFilterState(
    val selectedMonthId: String = "",
    val selectedMonthLabel: String = "",
    val isDatePickerVisible: Boolean = false
)

// Wrapper
data class StatisticsUiState(
    val screen: StatisticsScreenState = StatisticsScreenState(),
    val content: StatisticsContentState = StatisticsContentState(),
    val filter: StatisticsFilterState = StatisticsFilterState()
)

sealed interface StatisticsEvent {
    data class OnChangeMonth(val monthId: String) : StatisticsEvent
    object OnToggleDatePicker : StatisticsEvent
}
