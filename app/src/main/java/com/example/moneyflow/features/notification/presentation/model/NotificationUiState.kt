package com.example.moneyflow.features.notification.presentation.model

import com.example.moneyflow.data.local.entity.NotificationEntity

data class NotificationScreenState(
    val isLoading: Boolean = false,
    val unreadCount: Int = 0
)

data class NotificationContentState(
    val notifications: List<NotificationEntity> = emptyList()
)

data class NotificationUiState(
    val screen: NotificationScreenState = NotificationScreenState(),
    val content: NotificationContentState = NotificationContentState()
)

sealed interface NotificationEvent {
    data class OnMarkAsRead(val id: Long) : NotificationEvent
    object OnMarkAllAsRead : NotificationEvent
    object OnClearAll : NotificationEvent
}
