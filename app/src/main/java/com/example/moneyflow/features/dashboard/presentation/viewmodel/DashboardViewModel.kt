package com.example.moneyflow.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.data.repository.SettingsRepository
import com.example.moneyflow.features.dashboard.domain.usecase.DashboardUseCases
import com.example.moneyflow.features.dashboard.domain.usecase.FinancialCycleStatus
import com.example.moneyflow.features.dashboard.presentation.model.*
import com.example.moneyflow.features.dashboard.presentation.mapper.DashboardUiMapper
import com.example.moneyflow.utils.Formatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardUseCases: DashboardUseCases,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _effect = MutableSharedFlow<DashboardEffect>()
    val effect = _effect.asSharedFlow()

    private val _globalSelectedMonth = MutableStateFlow(SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date()))
    private val _forceDismissDialog = MutableStateFlow(false)

    // Level 3: Filter State Flow
    val filterState: StateFlow<DashboardFilterState> = _globalSelectedMonth.map { monthId ->
        DashboardFilterState(
            selectedMonthId = monthId,
            selectedMonthLabel = Formatter.formatMonthLabel(monthId)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardFilterState())

    // Level 2: Content State Flow
    @OptIn(ExperimentalCoroutinesApi::class)
    val contentState: StateFlow<DashboardContentState> = _globalSelectedMonth.flatMapLatest { monthId ->
        combine(
            settingsRepository.userName,
            settingsRepository.avatarUri,
            settingsRepository.language,
            dashboardUseCases.getSummary(monthId),
            dashboardUseCases.getTransactions(monthId)
        ) { name, avatar, lang, summary, txs ->
            val locale = Locale(lang)
            val todaySdf = SimpleDateFormat("'Hôm nay:' EEEE, dd/MM/yyyy", locale)
            DashboardContentState(
                userName = name,
                avatarUri = avatar,
                todayLabel = todaySdf.format(Date()),
                totalAssetText = Formatter.formatCurrency(summary.totalAsset),
                totalIncomeText = Formatter.formatCurrency(summary.totalIncome),
                totalSpentText = Formatter.formatCurrency(summary.totalSpent),
                totalBudgetText = Formatter.formatCurrency(summary.totalBudgetAllocated),
                availableText = Formatter.formatCurrency(summary.availableToBudget),
                recentTransactions = txs.take(10).map { DashboardUiMapper.mapTransaction(it) }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardContentState())

    private val _cycleStatus = MutableStateFlow<FinancialCycleStatus?>(null)

    // Level 1: Screen State Flow
    @OptIn(ExperimentalCoroutinesApi::class)
    val screenState: StateFlow<DashboardScreenState> = combine(
        _globalSelectedMonth.flatMapLatest { dashboardUseCases.getSummary(it) },
        _forceDismissDialog,
        _cycleStatus
    ) { summary, forceDismiss, cycle ->
        DashboardScreenState(
            isLoading = false,
            showInitialBalanceDialog = !summary.isBalanceSet && !forceDismiss,
            cycleStatus = cycle
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardScreenState())

    init {
        checkFinancialCycle()
    }

    private fun checkFinancialCycle() {
        viewModelScope.launch {
            val status = dashboardUseCases.checkCycle()
            _cycleStatus.value = status
        }
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.OnChangeMonth -> {
                _forceDismissDialog.value = false
                _globalSelectedMonth.value = event.monthId
            }
            is DashboardEvent.OnSetInitialBalance -> {
                viewModelScope.launch {
                    dashboardUseCases.setBalance(_globalSelectedMonth.value, event.amount.toLong())
                    _effect.emit(DashboardEffect.ShowSnackbar("Đã thiết lập số dư tháng"))
                }
            }
            DashboardEvent.OnDismissBalanceDialog -> {
                _forceDismissDialog.value = true
                _cycleStatus.value = null
            }
            DashboardEvent.OnRefresh -> { }
            is DashboardEvent.OnTransactionClick -> {
                viewModelScope.launch { _effect.emit(DashboardEffect.NavigateToTransaction(event.id)) }
            }
            DashboardEvent.OnSeeAllClick -> {
                viewModelScope.launch { _effect.emit(DashboardEffect.NavigateToHistory) }
            }
            DashboardEvent.OnCalendarClick -> {
                viewModelScope.launch { _effect.emit(DashboardEffect.NavigateToCalendar) }
            }
            DashboardEvent.OnNotificationClick -> {
                viewModelScope.launch { _effect.emit(DashboardEffect.NavigateToNotifications) }
            }
            is DashboardEvent.OnStartNewCycle -> {
                viewModelScope.launch {
                    dashboardUseCases.checkCycle.startNewCycle(event.initialBalance)
                    _cycleStatus.value = null
                    _effect.emit(DashboardEffect.ShowSnackbar("Chào mừng bạn đến chu kỳ mới!"))
                }
            }
        }
    }

    fun getDailySummary(start: Long, end: Long) = dashboardUseCases.getDailySummary(start, end)
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
