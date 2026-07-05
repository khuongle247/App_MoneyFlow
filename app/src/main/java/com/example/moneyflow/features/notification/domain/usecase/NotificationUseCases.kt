package com.example.moneyflow.features.notification.domain.usecase

import com.example.moneyflow.data.local.entity.NotificationEntity
import com.example.moneyflow.features.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class NotificationUseCases @Inject constructor(
    val getAllNotifications: GetAllNotificationsUseCase,
    val getUnreadCount: GetUnreadCountUseCase,
    val markAsRead: MarkAsReadUseCase,
    val deleteAllNotifications: DeleteAllNotificationsUseCase
)

class GetAllNotificationsUseCase @Inject constructor(private val repository: NotificationRepository) {
    operator fun invoke(): Flow<List<NotificationEntity>> = repository.getAllNotifications()
}

class GetUnreadCountUseCase @Inject constructor(private val repository: NotificationRepository) {
    operator fun invoke(): Flow<Int> = repository.getUnreadCount()
}

class MarkAsReadUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(id: Long) = repository.markAsRead(id)
    suspend fun markAllAsRead() = repository.markAllAsRead()
}

class DeleteAllNotificationsUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke() = repository.deleteAllNotifications()
}
