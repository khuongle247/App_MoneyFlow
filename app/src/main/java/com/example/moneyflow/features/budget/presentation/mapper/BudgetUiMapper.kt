package com.example.moneyflow.features.budget.presentation.mapper

import androidx.compose.ui.graphics.Color
import com.example.moneyflow.data.local.entity.BudgetEntity
import com.example.moneyflow.features.budget.presentation.model.BudgetUiModel
import com.example.moneyflow.utils.Formatter

object BudgetUiMapper {
    fun mapBudget(entity: BudgetEntity, spent: Long): BudgetUiModel {
        val progress = if (entity.amount > 0) (spent.toDouble() / entity.amount).toFloat().coerceIn(0f, 1.2f) else 0f
        return BudgetUiModel(
            id = entity.id,
            name = entity.name,
            amountText = Formatter.formatCurrency(entity.amount),
            spentText = Formatter.formatCurrency(spent),
            remainingText = Formatter.formatCurrency((entity.amount - spent).coerceAtLeast(0L)),
            progress = progress,
            progressColor = when {
                progress > 1f -> Color(0xFFF44336) 
                progress > 0.8f -> Color(0xFFFF9800) 
                else -> Color(0xFF4CAF50) 
            },
            iconName = entity.icon,
            color = entity.color
        )
    }
}
