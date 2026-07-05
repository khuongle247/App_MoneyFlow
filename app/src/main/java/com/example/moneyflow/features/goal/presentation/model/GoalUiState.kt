package com.example.moneyflow.features.goal.presentation.model

data class SavingGoalUiModel(
    val id: Long,
    val name: String,
    val targetText: String,
    val currentText: String,
    val remainingText: String,
    val progress: Float,
    val progressPercent: Int
)
