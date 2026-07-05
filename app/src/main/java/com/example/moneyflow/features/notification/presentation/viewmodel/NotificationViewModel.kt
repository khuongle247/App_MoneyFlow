package com.example.moneyflow.features.notification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyflow.features.notification.domain.usecase.NotificationUseCases
import com.example.moneyflow.features.notification.presentation.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val useCases: NotificationUseCases
) : ViewModel() {

    val screenState = useCases.getUnreadCount().map {
        NotificationScreenState(unreadCount = it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationScreenState())

    val contentState = useCases.getAllNotifications().map {
        NotificationContentState(notifications = it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationContentState())

    fun onEvent(event: NotificationEvent) {
        when (event) {
            is NotificationEvent.OnMarkAsRead -> {
                viewModelScope.launch { useCases.markAsRead(event.id) }
            }
            NotificationEvent.OnMarkAllAsRead -> {
                viewModelScope.launch { useCases.markAsRead.markAllAsRead() }
            }
            NotificationEvent.OnClearAll -> {
                viewModelScope.launch { useCases.deleteAllNotifications() }
            }
        }
    }
}
