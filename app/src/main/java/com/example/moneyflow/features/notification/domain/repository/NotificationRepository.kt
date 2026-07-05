package com.example.moneyflow.features.notification.domain.repository

import com.example.moneyflow.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    fun getUnreadCount(): Flow<Int>
    suspend fun insertNotification(notification: NotificationEntity)
    suspend fun markAsRead(id: Long)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(notification: NotificationEntity)
    suspend fun deleteAllNotifications()
}
