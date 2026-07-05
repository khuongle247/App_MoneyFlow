package com.example.moneyflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moneyflow.ui.navigation.BottomNavigationBar
import com.example.moneyflow.ui.navigation.AppRoute
import com.example.moneyflow.ui.theme.MoneyFlowTheme
import com.example.moneyflow.features.dashboard.presentation.screen.DashboardScreen
import com.example.moneyflow.features.transaction.presentation.screen.TransactionScreen
import com.example.moneyflow.features.transaction.presentation.screen.HistoryScreen
import com.example.moneyflow.features.budget.presentation.screen.BudgetScreen
import com.example.moneyflow.features.goal.presentation.screen.SavingGoalScreen
import com.example.moneyflow.features.statistics.presentation.screen.StatisticsScreen
import com.example.moneyflow.features.settings.presentation.screen.SettingsScreen
import com.example.moneyflow.features.onboarding.presentation.screen.OnboardingScreen
import com.example.moneyflow.features.onboarding.presentation.screen.SplashScreen
import com.example.moneyflow.features.dashboard.presentation.screen.CalendarScreen
import com.example.moneyflow.features.notification.data.worker.NotificationWorker
import androidx.work.*
import com.example.moneyflow.features.notification.presentation.screen.NotificationScreen
import com.example.moneyflow.features.settings.presentation.screen.CycleSettingsScreen
import com.example.moneyflow.features.settings.presentation.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            val language by settingsViewModel.language.collectAsState()

            LaunchedEffect(language) {
                if (language.isNotEmpty()) {
                    val locale = Locale(language)
                    if (resources.configuration.locales[0].language != locale.language) {
                        Locale.setDefault(locale)
                        val config = resources.configuration
                        config.setLocale(locale)
                        resources.updateConfiguration(config, resources.displayMetrics)
                    }
                }
            }

            MoneyFlowTheme(darkTheme = isDarkMode ?: false) {
                val navController = rememberNavController()
                
                // Initialize WorkManager safely in background
                LaunchedEffect(Unit) {
                    try {
                        val request = PeriodicWorkRequestBuilder<NotificationWorker>(4, java.util.concurrent.TimeUnit.HOURS)
                            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build())
                            .build()
                        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                            "budget_check",
                            ExistingPeriodicWorkPolicy.KEEP,
                            request
                        )
                    } catch (e: Exception) { e.printStackTrace() }
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute in listOf(
                    AppRoute.Dashboard.route,
                    AppRoute.Statistics.route,
                    AppRoute.Budget.route,
                    AppRoute.Settings.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { if (showBottomBar) BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppRoute.Splash.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(AppRoute.Splash.route) { SplashScreen(navController) }
                        composable(AppRoute.Onboarding.route) { OnboardingScreen(navController) }
                        composable(AppRoute.Dashboard.route) { DashboardScreen(navController) }
                        composable(AppRoute.Transaction.route) { backStackEntry ->
                            val txId = backStackEntry.arguments?.getString(AppRoute.Transaction.ARG_ID)?.toLongOrNull() ?: -1L
                            TransactionScreen(navController, transactionId = txId)
                        }
                        composable(AppRoute.Statistics.route) { StatisticsScreen(navController) }
                        composable(AppRoute.History.route) { HistoryScreen(navController) }
                        composable(AppRoute.Budget.route) { BudgetScreen(navController) }
                        composable(AppRoute.SavingGoal.route) { SavingGoalScreen(navController) }
                        composable(AppRoute.Settings.route) { SettingsScreen(navController) }
                        composable(AppRoute.Calendar.route) { CalendarScreen(navController) }
                        composable(AppRoute.Notifications.route) { NotificationScreen(navController) }
                        composable(AppRoute.CycleSettings.route) { CycleSettingsScreen(navController) }
                    }
                }
            }
        }
    }
}
