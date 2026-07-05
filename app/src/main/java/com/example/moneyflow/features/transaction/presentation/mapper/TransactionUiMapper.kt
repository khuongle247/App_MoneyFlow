package com.example.moneyflow.features.transaction.presentation.mapper

import com.example.moneyflow.data.local.entity.TransactionEntity
import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.features.dashboard.presentation.model.TransactionUiModel
import com.example.moneyflow.utils.Formatter

object TransactionUiMapper {
    fun mapTransaction(entity: TransactionEntity): TransactionUiModel {
        val absAmount = Math.abs(entity.amount)
        return TransactionUiModel(
            id = entity.id,
            title = entity.title,
            amountText = (if (entity.type == TransactionType.EXPENSE) "-" else "+") + Formatter.formatCurrency(absAmount),
            dateText = Formatter.formatDate(entity.date),
            iconName = entity.icon,
            isExpense = entity.type == TransactionType.EXPENSE
        )
    }
}
