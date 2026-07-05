package com.example.moneyflow.features.transaction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.features.transaction.domain.usecase.TransactionUseCases
import com.example.moneyflow.features.transaction.presentation.model.*
import com.example.moneyflow.features.transaction.presentation.mapper.TransactionUiMapper
import com.example.moneyflow.utils.MoneyParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionUseCases: TransactionUseCases
) : ViewModel() {

    private val _effect = MutableSharedFlow<TransactionEffect>()
    val effect = _effect.asSharedFlow()

    private val _selectedMonth = MutableStateFlow(SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date()))
    private val _searchQuery = MutableStateFlow("")
    private val _formState = MutableStateFlow(TransactionFilterState())

    val filterState: StateFlow<TransactionFilterState> = combine(
        _selectedMonth, _searchQuery, _formState
    ) { month, query, form ->
        form.copy(selectedMonthId = month, searchQuery = query)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, TransactionFilterState())

    @OptIn(ExperimentalCoroutinesApi::class)
    val contentState: StateFlow<TransactionContentState> = combine(
        _selectedMonth, _searchQuery, 
        transactionUseCases.getMetadata.categories, 
        transactionUseCases.getMetadata.budgets
    ) { month, query, cats, budgets ->
        Quad(month, query, cats, budgets)
    }.flatMapLatest { (month, query, cats, budgets) ->
        transactionUseCases.getTransactionsByMonth(month).map { txs ->
            val filtered = if (query.isBlank()) txs else txs.filter { it.title.contains(query, ignoreCase = true) }
            TransactionContentState(
                transactions = filtered.map { TransactionUiMapper.mapTransaction(it) },
                categories = cats,
                budgets = budgets
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TransactionContentState())

    val screenState: StateFlow<TransactionScreenState> = MutableStateFlow(TransactionScreenState())
        .asStateFlow()

    fun onEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.OnMonthChanged -> _selectedMonth.value = event.monthId
            is TransactionEvent.OnSearchChanged -> _searchQuery.value = event.query
            is TransactionEvent.OnDeleteTransaction -> {
                viewModelScope.launch {
                    val tx = transactionUseCases.getTransactionById(event.id)
                    tx?.let { transactionUseCases.deleteTransaction(it) }
                }
            }
            is TransactionEvent.OnTitleChanged -> _formState.update { it.copy(title = event.title) }
            is TransactionEvent.OnAmountChanged -> _formState.update { it.copy(amountText = event.amount) }
            is TransactionEvent.OnTypeChanged -> _formState.update { it.copy(selectedType = event.type) }
            is TransactionEvent.OnBudgetSelected -> _formState.update { it.copy(selectedBudgetId = event.id) }
            is TransactionEvent.OnIconSelected -> _formState.update { it.copy(selectedIcon = event.icon) }
            TransactionEvent.OnSaveTransaction -> saveTransaction()
        }
    }

    private fun saveTransaction() {
        viewModelScope.launch {
            val state = _formState.value
            val amount = MoneyParser.parse(state.amountText)
            transactionUseCases.addTransaction(
                state.title, amount, state.selectedType, 1L,
                state.selectedBudgetId, state.selectedIcon, _selectedMonth.value
            )
            _effect.emit(TransactionEffect.NavigateBack)
        }
    }

    suspend fun getTransactionById(id: Long) = transactionUseCases.getTransactionById(id)
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
