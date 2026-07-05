package com.example.moneyflow.features.goal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.features.goal.domain.usecase.GoalUseCases
import com.example.moneyflow.features.goal.presentation.mapper.GoalUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalUseCases: GoalUseCases
) : ViewModel() {

    val savingGoals = goalUseCases.getAllSavingGoals().map { list ->
        list.map { GoalUiMapper.mapSavingGoal(it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSavingGoal(name: String, target: Double, current: Double) {
        viewModelScope.launch {
            goalUseCases.addSavingGoal(name, target.toLong(), current.toLong())
        }
    }
}
