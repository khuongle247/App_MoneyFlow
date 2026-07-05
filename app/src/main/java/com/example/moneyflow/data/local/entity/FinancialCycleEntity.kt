package com.example.moneyflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_cycles")
data class FinancialCycleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startDate: Long,
    val endDate: Long,
    val initialBalance: Long,
    val remainingBalance: Long,
    val totalExpense: Long,
    val isClosed: Boolean = false
)
