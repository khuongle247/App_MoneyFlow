package com.example.moneyflow.features.notification.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moneyflow.R
import com.example.moneyflow.data.local.entity.NotificationEntity
import com.example.moneyflow.data.local.entity.NotificationType
import com.example.moneyflow.features.dashboard.domain.usecase.DashboardUseCases
import com.example.moneyflow.features.notification.domain.repository.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val dashboardUseCases: DashboardUseCases,
    private val notificationRepository: NotificationRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val monthId = SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(Date())
        val summary = dashboardUseCases.getSummary(monthId).first()
        
        // 1. Check total budget warning (< 20% remaining)
        if (summary.totalBudgetAllocated > 0) {
            val remainingPercent = (summary.availableToBudget.toDouble() / summary.totalBudgetAllocated) * 100
            if (remainingPercent < 20) {
                sendNotification(
                    "⚠️ Cảnh báo ngân sách",
                    "Ngân sách còn dưới 20%, hãy cân nhắc các khoản chi tiếp theo.",
                    NotificationType.SYSTEM
                )
            }
        }
        
        return Result.success()
    }

    private suspend fun sendNotification(title: String, content: String, type: NotificationType) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "moneyflow_alerts"
        
        val channel = NotificationChannel(channelId, "MoneyFlow Alerts", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        
        // Save to DB for Notification Center
        notificationRepository.insertNotification(
            NotificationEntity(
                title = title,
                content = content,
                type = type
            )
        )
    }
}
