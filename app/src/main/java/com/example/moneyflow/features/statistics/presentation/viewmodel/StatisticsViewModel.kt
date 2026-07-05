package com.example.moneyflow.features.statistics.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.features.budget.domain.usecase.BudgetUseCases
import com.example.moneyflow.features.dashboard.domain.usecase.GetDailyExpenseSummaryUseCase
import com.example.moneyflow.features.statistics.domain.usecase.StatisticsUseCases
import com.example.moneyflow.features.budget.presentation.mapper.BudgetUiMapper
import com.example.moneyflow.features.statistics.presentation.model.*
import com.example.moneyflow.utils.Formatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsUseCases: StatisticsUseCases,
    private val budgetUseCases: BudgetUseCases,
    private val getDailySummaryUseCase: GetDailyExpenseSummaryUseCase
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date()))
    private val _isDatePickerVisible = MutableStateFlow(false)

    // Level 3: Filter State
    val filterState: StateFlow<StatisticsFilterState> = combine(_selectedMonth, _isDatePickerVisible) { monthId, isVisible ->
        StatisticsFilterState(
            selectedMonthId = monthId,
            selectedMonthLabel = Formatter.formatMonthLabel(monthId),
            isDatePickerVisible = isVisible
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatisticsFilterState())

    // Level 2: Content State
    @OptIn(ExperimentalCoroutinesApi::class)
    val contentState: StateFlow<StatisticsContentState> = _selectedMonth.flatMapLatest { monthId ->
        statisticsUseCases.getStatisticsSummary(monthId).map { summary ->
            StatisticsContentState(
                totalSpentText = Formatter.formatCurrency(summary.totalSpent),
                budgetList = summary.budgets.map { budget ->
                    BudgetUiMapper.mapBudget(budget, summary.spendingMap[budget] ?: 0L)
                },
                spendingMap = summary.spendingMap.mapKeys { it.key.id }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatisticsContentState())

    // Level 1: Screen State
    val screenState: StateFlow<StatisticsScreenState> = MutableStateFlow(StatisticsScreenState())
        .asStateFlow()

    fun onEvent(event: StatisticsEvent) {
        when (event) {
            is StatisticsEvent.OnChangeMonth -> {
                _selectedMonth.value = event.monthId
                _isDatePickerVisible.value = false
            }
            StatisticsEvent.OnToggleDatePicker -> {
                _isDatePickerVisible.value = !_isDatePickerVisible.value
            }
        }
    }

    fun getDailySummary(start: Long, end: Long) = getDailySummaryUseCase(start, end)
}
