package com.example.moneyflow.features.notification.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneyflow.data.local.entity.NotificationEntity
import com.example.moneyflow.features.notification.presentation.model.NotificationEvent
import com.example.moneyflow.features.notification.presentation.viewmodel.NotificationViewModel
import com.example.moneyflow.ui.theme.PrimaryBlue
import com.example.moneyflow.ui.theme.TextGray
import com.example.moneyflow.utils.Formatter

import com.example.moneyflow.data.local.entity.NotificationType
import com.example.moneyflow.ui.theme.*
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.animation.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val contentState by viewModel.contentState.collectAsState()
    
    // Task 4: Local Tab Filter Logic
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tất cả", "Chưa đọc", "Đã đọc")
    
    val filteredNotifications = remember(contentState.notifications, selectedTabIndex) {
        when (selectedTabIndex) {
            1 -> contentState.notifications.filter { !it.isRead }
            2 -> contentState.notifications.filter { it.isRead }
            else -> contentState.notifications
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Thông báo", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    },
                    actions = {
                        if (contentState.notifications.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onEvent(NotificationEvent.OnMarkAllAsRead) }) {
                                Icon(Icons.Default.DoneAll, contentDescription = "Đọc hết")
                            }
                        }
                    }
                )
                
                // Task 4: TabRow UI
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = PrimaryBlue,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = PrimaryBlue
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (filteredNotifications.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.NotificationsActive, null, modifier = Modifier.size(64.dp), tint = TextGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chưa có thông báo nào trong mục này.", color = TextGray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(filteredNotifications, key = { it.id }) { notification ->
                    NotificationItem(notification) {
                        viewModel.onEvent(NotificationEvent.OnMarkAsRead(notification.id))
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationEntity, onClick: () -> Unit) {
    // Task 5: Dynamic Icons based on Type
    val icon = when (notification.type) {
        NotificationType.BUDGET_WARNING -> Icons.Default.Warning
        NotificationType.CATEGORY_WARNING -> Icons.Default.ErrorOutline
        NotificationType.MONTH_CYCLE -> Icons.Default.Stars
        NotificationType.SYSTEM -> Icons.Default.Info
    }
    
    val color = when (notification.type) {
        NotificationType.BUDGET_WARNING -> Color(0xFFFF9800)
        NotificationType.CATEGORY_WARNING -> ErrorRed
        NotificationType.MONTH_CYCLE -> SuccessGreen
        NotificationType.SYSTEM -> PrimaryBlue
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .animateContentSize(),
        color = if (notification.isRead) Color.Transparent else color.copy(alpha = 0.03f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp), 
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title, 
                    fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.ExtraBold, 
                    fontSize = 15.sp,
                    color = if (notification.isRead) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.content, 
                    color = if (notification.isRead) TextGray else MaterialTheme.colorScheme.onSurface, 
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = Formatter.formatDate(notification.createdAt), 
                    color = TextGray, 
                    fontSize = 11.sp
                )
            }
            
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(8.dp)
                        .background(color, CircleShape)
                )
            }
        }
    }
}
