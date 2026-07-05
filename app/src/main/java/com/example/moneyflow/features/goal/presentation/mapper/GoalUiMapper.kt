package com.example.moneyflow.features.goal.presentation.mapper

import com.example.moneyflow.data.local.entity.SavingGoalEntity
import com.example.moneyflow.features.goal.presentation.model.SavingGoalUiModel
import com.example.moneyflow.utils.Formatter

object GoalUiMapper {
    fun mapSavingGoal(entity: SavingGoalEntity): SavingGoalUiModel {
        val progress = if (entity.targetAmount > 0) (entity.currentAmount.toDouble() / entity.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
        return SavingGoalUiModel(
            id = entity.id,
            name = entity.name,
            targetText = Formatter.formatCurrency(entity.targetAmount),
            currentText = Formatter.formatCurrency(entity.currentAmount),
            remainingText = Formatter.formatCurrency((entity.targetAmount - entity.currentAmount).coerceAtLeast(0L)),
            progress = progress,
            progressPercent = (progress * 100).toInt()
        )
    }
}
