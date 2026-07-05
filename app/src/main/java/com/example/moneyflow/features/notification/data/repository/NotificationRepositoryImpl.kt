package com.example.moneyflow.features.notification.data.repository

import com.example.moneyflow.data.local.dao.NotificationDao
import com.example.moneyflow.data.local.entity.NotificationEntity
import com.example.moneyflow.features.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {
    override fun getAllNotifications(): Flow<List<NotificationEntity>> = 
        notificationDao.getAllNotifications()

    override fun getUnreadCount(): Flow<Int> = notificationDao.getUnreadCount()

    override suspend fun insertNotification(notification: NotificationEntity) = 
        notificationDao.insertNotification(notification)

    override suspend fun markAsRead(id: Long) = notificationDao.markAsRead(id)

    override suspend fun markAllAsRead() = notificationDao.markAllAsRead()

    override suspend fun deleteNotification(notification: NotificationEntity) = 
        notificationDao.deleteNotification(notification)

    override suspend fun deleteAllNotifications() = notificationDao.deleteAllNotifications()
}
