package com.example.moneyflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NotificationType {
    BUDGET_WARNING,
    CATEGORY_WARNING,
    MONTH_CYCLE,
    SYSTEM
}

@Entity(tableName = "app_notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: NotificationType
)
