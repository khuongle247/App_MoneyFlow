package com.example.moneyflow.features.budget.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.features.budget.domain.usecase.BudgetUseCases
import com.example.moneyflow.features.dashboard.domain.usecase.GetDashboardSummaryUseCase
import com.example.moneyflow.features.budget.presentation.mapper.BudgetUiMapper
import com.example.moneyflow.features.budget.presentation.model.*
import com.example.moneyflow.utils.Formatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetUseCases: BudgetUseCases,
    private val getDashboardSummaryUseCase: GetDashboardSummaryUseCase
) : ViewModel() {

    private val _effect = MutableSharedFlow<BudgetEffect>()
    val effect = _effect.asSharedFlow()

    private val _selectedMonth = MutableStateFlow(SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date()))
    private val _showAddDialog = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    // 1. Filter State (Level 3)
    val filterState: StateFlow<BudgetFilterState> = _selectedMonth.map { monthId ->
        BudgetFilterState(
            selectedMonthId = monthId,
            selectedMonthLabel = Formatter.formatMonthLabel(monthId)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetFilterState())

    // 2. Content State (Level 2)
    @OptIn(ExperimentalCoroutinesApi::class)
    val contentState: StateFlow<BudgetContentState> = _selectedMonth.flatMapLatest { monthId ->
        combine(
            getDashboardSummaryUseCase(monthId),
            budgetUseCases.getBudgetsByMonth(monthId),
            budgetUseCases.getBudgetSpending(monthId)
        ) { summary, budgetEntities, spendingMap ->
            BudgetContentState(
                totalBudgetText = Formatter.formatCurrency(summary.totalBudgetAllocated),
                totalSpentText = Formatter.formatCurrency(summary.totalSpent),
                remainingText = Formatter.formatCurrency(summary.availableToBudget),
                budgetList = budgetEntities.map { entity ->
                    BudgetUiMapper.mapBudget(entity, spendingMap[entity.id] ?: 0L)
                }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetContentState())

    // 3. Screen State (Level 1)
    val screenState: StateFlow<BudgetScreenState> = combine(
        _showAddDialog,
        _error
    ) { showDialog, error ->
        BudgetScreenState(
            isLoading = false, // Add logic if background loading is needed
            error = error,
            showAddBudgetDialog = showDialog
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetScreenState())

    fun onEvent(event: BudgetEvent) {
        when (event) {
            is BudgetEvent.OnChangeMonth -> {
                _selectedMonth.value = event.monthId
            }
            is BudgetEvent.OnAddBudget -> {
                viewModelScope.launch {
                    budgetUseCases.addBudget(event.name, event.amount.toLong(), event.color, event.icon, _selectedMonth.value)
                    _showAddDialog.value = false
                }
            }
            is BudgetEvent.OnDeleteBudget -> {
                viewModelScope.launch {
                    val budget = budgetUseCases.getBudgetById(event.id)
                    budget?.let { budgetUseCases.deleteBudget(it) }
                }
            }
            BudgetEvent.OnNavigateToAddTransaction -> {
                viewModelScope.launch {
                    _effect.emit(BudgetEffect.NavigateToAddTransaction)
                }
            }
            BudgetEvent.OnShowAddDialog -> _showAddDialog.value = true
            BudgetEvent.OnDismissAddDialog -> _showAddDialog.value = false
        }
    }
}
