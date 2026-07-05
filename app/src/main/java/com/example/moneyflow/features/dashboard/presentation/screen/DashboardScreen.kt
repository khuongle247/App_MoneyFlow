package com.example.moneyflow.features.dashboard.presentation.screen

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moneyflow.R
import com.example.moneyflow.features.dashboard.domain.usecase.FinancialCycleStatus
import com.example.moneyflow.features.dashboard.presentation.model.*
import com.example.moneyflow.features.dashboard.presentation.viewmodel.DashboardViewModel
import com.example.moneyflow.ui.navigation.AppRoute
import com.example.moneyflow.ui.theme.*
import com.example.moneyflow.utils.Formatter
import com.example.moneyflow.utils.IconHelper
import java.util.*

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val screenState by viewModel.screenState.collectAsState()
    val contentState by viewModel.contentState.collectAsState()
    val filterState by viewModel.filterState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DashboardEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                DashboardEffect.NavigateToHistory -> {
                    navController.navigate(AppRoute.History.route)
                }
                DashboardEffect.NavigateToCalendar -> {
                    navController.navigate(AppRoute.Calendar.route)
                }
                DashboardEffect.NavigateToNotifications -> {
                    navController.navigate(AppRoute.Notifications.route)
                }
                is DashboardEffect.NavigateToTransaction -> {
                    navController.navigate(AppRoute.Transaction.createRoute(effect.id))
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Reduced top padding as requested
            HomeHeader(
                userName = contentState.userName,
                avatarUri = contentState.avatarUri,
                onNotificationClick = { viewModel.onEvent(DashboardEvent.OnNotificationClick) },
                onCalendarClick = { viewModel.onEvent(DashboardEvent.OnCalendarClick) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(DashboardEvent.OnTransactionClick()) },
                containerColor = Color(0xFF5C9DFF), // Matching the lighter blue button
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_transaction), modifier = Modifier.size(28.dp))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                // Task 1: Dashboard Date Chip (Reduced spacer)
                Surface(
                    onClick = { viewModel.onEvent(DashboardEvent.OnCalendarClick) },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color(0xFF5C9DFF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = contentState.todayLabel,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Main Balance Card - Task: Lighter blue from image
                MainBalanceCard(contentState.totalAssetText)
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                // Grid of 4 Cards
                SummaryGrid(
                    spentText = contentState.totalSpentText,
                    incomeText = contentState.totalIncomeText,
                    budgetText = contentState.totalBudgetText,
                    remainingText = contentState.availableText,
                    onSpentClick = { navController.navigate(AppRoute.History.route) },
                    onIncomeClick = { navController.navigate(AppRoute.History.route) },
                    onBudgetClick = { navController.navigate(AppRoute.Budget.route) }
                )
            }

            item {
                RecentHeader(onSeeAll = { viewModel.onEvent(DashboardEvent.OnSeeAllClick) })
            }

            items(contentState.recentTransactions) { transaction ->
                ModernTransactionItem(transaction) {
                    viewModel.onEvent(DashboardEvent.OnTransactionClick(transaction.id))
                }
            }
        }
    }

    if (screenState.showInitialBalanceDialog) {
        InitialBalanceDialog(
            monthLabel = filterState.selectedMonthId.replace("-", "/"),
            onConfirm = { viewModel.onEvent(DashboardEvent.OnSetInitialBalance(it)) },
            onDismiss = { viewModel.onEvent(DashboardEvent.OnDismissBalanceDialog) }
        )
    }

    screenState.cycleStatus?.let { status ->
        when (status) {
            is FinancialCycleStatus.CycleEnded -> {
                FinancialCycleDialog(
                    lastCycle = status.lastCycle,
                    onConfirm = { viewModel.onEvent(DashboardEvent.OnStartNewCycle(it)) },
                    onDismiss = { viewModel.onEvent(DashboardEvent.OnDismissBalanceDialog) }
                )
            }
            FinancialCycleStatus.NeedsInitialSetup -> { }
            else -> {}
        }
    }
}

@Composable
fun HomeHeader(
    userName: String,
    avatarUri: String?,
    onNotificationClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp), // Task: Reduced vertical padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Menu */ }) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onSurface)
        }
        Text(
            text = "MoneyFlow",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = onCalendarClick) { // Task: Made clickable
            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurface)
        }
        IconButton(onClick = onNotificationClick) { // Task: Already clickable
            BadgedBox(badge = { Badge { Text("1") } }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun MainBalanceCard(totalAssetText: String) {
    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .height(140.dp), // Reduced height slightly
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5C9DFF)), // Task: Vibrant Lighter Blue
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TỔNG TÀI SẢN",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = totalAssetText,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun SummaryGrid(
    spentText: String, 
    incomeText: String, 
    budgetText: String, 
    remainingText: String,
    onSpentClick: () -> Unit,
    onIncomeClick: () -> Unit,
    onBudgetClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(
                label = "Tổng Chi",
                value = "-$spentText",
                icon = Icons.Default.ArrowDownward,
                iconColor = ErrorRed,
                modifier = Modifier.weight(1f),
                onClick = onSpentClick
            )
            SummaryCard(
                label = "Tổng Thu",
                value = "+$incomeText",
                icon = Icons.Default.ArrowUpward,
                iconColor = SuccessGreen,
                modifier = Modifier.weight(1f),
                onClick = onIncomeClick
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SummaryCard(
                label = "Ngân Sách Đã Cấp",
                value = budgetText,
                icon = Icons.Default.Savings,
                iconColor = PrimaryBlue,
                modifier = Modifier.weight(1f),
                onClick = onBudgetClick
            )
            SummaryCard(
                label = "Số Tiền Còn Lại",
                value = remainingText,
                icon = Icons.Default.AccountBalanceWallet,
                iconColor = Color.Gray,
                modifier = Modifier.weight(1f),
                onClick = {}
            )
        }
    }
}

@Composable
fun SummaryCard(label: String, value: String, icon: ImageVector, iconColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(110.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(label, color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun RecentHeader(onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Lịch sử chi tiêu",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            stringResource(R.string.see_all),
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.clickable { onSeeAll() }
        )
    }
}

@Composable
fun ModernTransactionItem(transaction: TransactionUiModel, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (transaction.isExpense) ErrorRed.copy(alpha = 0.1f) else SuccessGreen.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = IconHelper.getIcon(transaction.iconName),
                        contentDescription = null,
                        tint = if (transaction.isExpense) ErrorRed else SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(transaction.dateText, color = TextGray, fontSize = 12.sp)
            }
            Text(
                text = (if (transaction.isExpense) "-" else "+") + transaction.amountText,
                fontWeight = FontWeight.ExtraBold,
                color = if (transaction.isExpense) Color.Black else SuccessGreen,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun FinancialCycleDialog(
    lastCycle: com.example.moneyflow.data.local.entity.FinancialCycleEntity,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var balanceText by remember { mutableStateOf("") }
    var animateTrigger by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { animateTrigger = true }

    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(
            visible = animateTrigger,
            enter = scaleIn(initialScale = 0.8f) + fadeIn(),
            exit = scaleOut(targetScale = 0.8f) + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(24.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle, 
                                contentDescription = null, 
                                tint = SuccessGreen,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Chào mừng tháng mới! 🎉",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Bạn đã kết thúc chu kỳ cũ mượt mà. Hãy nhập số dư để bắt đầu chu kỳ tiếp theo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CycleMiniStat(
                            label = "Đã chi", 
                            value = Formatter.formatCurrency(lastCycle.totalExpense),
                            color = ErrorRed,
                            modifier = Modifier.weight(1f)
                        )
                        CycleMiniStat(
                            label = "Còn lại", 
                            value = Formatter.formatCurrency(lastCycle.remainingBalance),
                            color = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = balanceText,
                        onValueChange = { if (it.all { c -> c.isDigit() }) balanceText = it },
                        label = { Text("Số dư tháng mới") },
                        suffix = { Text("₫", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = { onConfirm(balanceText.toLongOrNull() ?: 0L) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = balanceText.isNotEmpty()
                    ) {
                        Text("Xác nhận & Bắt đầu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CycleMiniStat(label: String, value: String, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 11.sp, color = TextGray)
            Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 13.sp)
        }
    }
}

@Composable
fun InitialBalanceDialog(monthLabel: String, onConfirm: (Double) -> Unit, onDismiss: () -> Unit) {
    var balanceText by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chào tháng $monthLabel! 👋") },
        text = {
            Column {
                Text("Hãy nhập số tiền hiện có của bạn để bắt đầu tháng này:")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { if (it.all { c -> c.isDigit() }) balanceText = it },
                    label = { Text("Số dư đầu kỳ") },
                    suffix = { Text("₫") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(balanceText.toDoubleOrNull() ?: 0.0) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = balanceText.isNotEmpty()
            ) { Text("Bắt đầu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Để sau") }
        }
    )
}
