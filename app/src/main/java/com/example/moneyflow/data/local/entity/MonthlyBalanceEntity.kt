package com.example.moneyflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_balances")
data class MonthlyBalanceEntity(
    @PrimaryKey val monthId: String, // Format: "MM-yyyy" (e.g., "06-2026")
    val balance: Long
)
