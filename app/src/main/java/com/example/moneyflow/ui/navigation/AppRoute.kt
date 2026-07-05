package com.example.moneyflow.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppRoute(
    val route: String,
    val title: String = "",
    val icon: ImageVector = Icons.Default.Home
) {
    object Splash : AppRoute("splash")
    object Onboarding : AppRoute("onboarding")
    object Dashboard : AppRoute("dashboard", "Trang chủ", Icons.Default.Home)
    object Statistics : AppRoute("statistics", "Thống kê", Icons.Default.BarChart)
    object Budget : AppRoute("budget", "Ngân sách", Icons.Default.AccountBalanceWallet)
    object History : AppRoute("history", "Lịch sử", Icons.Default.History)
    object SavingGoal : AppRoute("saving_goal", "Mục tiêu", Icons.Default.Flag)
    object Settings : AppRoute("settings", "Cài đặt", Icons.Default.Settings)
    object Calendar : AppRoute("calendar")
    object Notifications : AppRoute("notifications")
    object CycleSettings : AppRoute("cycle_settings")
    
    object Transaction : AppRoute("transaction?id={id}", "Giao dịch", Icons.Default.AddCard) {
        const val ARG_ID = "id"
        fun createRoute(id: Long = -1L) = "transaction?id=$id"
    }
}
