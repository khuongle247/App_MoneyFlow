package com.example.moneyflow.data.repository

import com.example.moneyflow.data.local.dao.FinlyDao
import com.example.moneyflow.data.local.entity.SavingGoalEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val dao: FinlyDao
) : GoalRepository {

    override val allSavingGoals: Flow<List<SavingGoalEntity>> = dao.getAllSavingGoals()

    override suspend fun insertSavingGoal(goal: SavingGoalEntity) {
        dao.insertSavingGoal(goal)
    }

    override suspend fun deleteSavingGoal(goal: SavingGoalEntity) {
        dao.deleteSavingGoal(goal)
    }
}
