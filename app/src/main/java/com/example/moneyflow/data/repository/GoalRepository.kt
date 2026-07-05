package com.example.moneyflow.data.repository

import com.example.moneyflow.data.local.entity.SavingGoalEntity
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    val allSavingGoals: Flow<List<SavingGoalEntity>>
    suspend fun insertSavingGoal(goal: SavingGoalEntity)
    suspend fun deleteSavingGoal(goal: SavingGoalEntity)
}
