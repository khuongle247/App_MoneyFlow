package com.example.moneyflow.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME, EXPENSE
}

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["date"]),
        Index(value = ["budgetId"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Long,
    val type: TransactionType,
    val categoryId: Long,
    val budgetId: Long? = null,
    val icon: String = "wallet",
    val date: Long,
    val note: String = ""
)
