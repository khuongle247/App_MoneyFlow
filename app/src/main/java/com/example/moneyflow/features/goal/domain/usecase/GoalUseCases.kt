package com.example.moneyflow.features.goal.domain.usecase

import com.example.moneyflow.data.local.entity.SavingGoalEntity
import com.example.moneyflow.data.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class GoalUseCases @Inject constructor(
    val addSavingGoal: AddSavingGoalUseCase,
    val updateSavingGoal: UpdateSavingGoalUseCase,
    val deleteSavingGoal: DeleteSavingGoalUseCase,
    val getAllSavingGoals: GetAllSavingGoalsUseCase
)

class AddSavingGoalUseCase @Inject constructor(private val repository: GoalRepository) {
    suspend operator fun invoke(name: String, target: Long, current: Long) {
        repository.insertSavingGoal(SavingGoalEntity(name = name, targetAmount = target, currentAmount = current))
    }
}

class UpdateSavingGoalUseCase @Inject constructor(private val repository: GoalRepository) {
    suspend operator fun invoke(goal: SavingGoalEntity) = repository.insertSavingGoal(goal)
}

class DeleteSavingGoalUseCase @Inject constructor(private val repository: GoalRepository) {
    suspend operator fun invoke(goal: SavingGoalEntity) = repository.deleteSavingGoal(goal)
}

class GetAllSavingGoalsUseCase @Inject constructor(private val repository: GoalRepository) {
    operator fun invoke(): Flow<List<SavingGoalEntity>> = repository.allSavingGoals
}
