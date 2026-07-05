package com.example.moneyflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String, // Icon name or resource ID as string
    val color: Int,   // Color hex value
    val budget: Double,
    val isCustom: Boolean = false
)
