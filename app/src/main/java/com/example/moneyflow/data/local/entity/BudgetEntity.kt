package com.example.moneyflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Long,
    val icon: String = "account_balance_wallet",
    val color: Int,
    val monthId: String = "" // MM-yyyy
)
