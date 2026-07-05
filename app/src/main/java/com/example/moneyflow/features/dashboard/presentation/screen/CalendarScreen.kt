package com.example.moneyflow.features.dashboard.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneyflow.features.dashboard.presentation.viewmodel.DashboardViewModel
import com.example.moneyflow.ui.theme.*
import com.example.moneyflow.utils.Formatter
import java.util.*

import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    // First day of month to start the grid
    val firstDayOfMonthCalendar = (calendar.clone() as Calendar).apply { 
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    val firstDayOfWeek = firstDayOfMonthCalendar.get(Calendar.DAY_OF_WEEK) - 1
    
    val lastDayOfMonthCalendar = (calendar.clone() as Calendar).apply { 
        set(Calendar.DAY_OF_MONTH, daysInMonth)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
    }
    
    val startDate = firstDayOfMonthCalendar.timeInMillis
    val endDate = lastDayOfMonthCalendar.timeInMillis
    
    // Fix: Remember the flow to prevent recomposition loop
    val dailySummaryFlow = remember(startDate, endDate) {
        viewModel.getDailySummary(startDate, endDate)
    }
    val dailySummary by dailySummaryFlow.collectAsState(initial = emptyList())
    
    // Today comparison
    val today = Calendar.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch chi tiêu", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Task 3: Month Header with Better Spacing
            Surface(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { calendar = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, -1) } }) {
                        Icon(Icons.Default.ChevronLeft, null, tint = PrimaryBlue)
                    }
                    Text(
                        text = java.text.SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("vi-VN")).format(calendar.time),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    IconButton(onClick = { calendar = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, 1) } }) {
                        Icon(Icons.Default.ChevronRight, null, tint = PrimaryBlue)
                    }
                }
            }

            // Weekdays (Standardized)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {
                val weekDays = listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")
                weekDays.forEach {
                    Text(
                        text = it, 
                        modifier = Modifier.weight(1f), 
                        textAlign = TextAlign.Center, 
                        fontWeight = FontWeight.Bold, 
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
            }

            // Calendar Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(firstDayOfWeek) { Spacer(modifier = Modifier.fillMaxSize()) }
                items(daysInMonth) { day ->
                    val dayNum = day + 1
                    val isToday = today.get(Calendar.DATE) == dayNum && 
                                  today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && 
                                  today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    
                    val summary = dailySummary.find { item ->
                        val cal = Calendar.getInstance().apply { timeInMillis = item.date }
                        cal.get(Calendar.DAY_OF_MONTH) == dayNum
                    }
                    
                    CalendarDayItem(dayNum, summary?.totalExpense ?: 0L, isToday)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("— Số tiền chi tiêu trong ngày (đơn vị nghìn đồng)", fontSize = 11.sp, color = TextGray)
            }
        }
    }
}

@Composable
fun CalendarDayItem(day: Int, expense: Long, isToday: Boolean) {
    val bgColor = when {
        expense == 0L -> Color.Transparent
        expense < 100000 -> SuccessGreen.copy(alpha = 0.08f)
        expense < 500000 -> Color(0xFFFFF3E0)
        else -> ErrorRed.copy(alpha = 0.08f)
    }
    
    val textColor = when {
        expense == 0L -> MaterialTheme.colorScheme.onSurface
        expense < 100000 -> SuccessGreen
        expense < 500000 -> Color(0xFFFF9800)
        else -> ErrorRed
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .border(
                width = if (isToday) 2.dp else 0.dp,
                color = if (isToday) PrimaryBlue else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isToday) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = day.toString(), 
                fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Bold, 
                fontSize = 14.sp,
                color = if (isToday) PrimaryBlue else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            if (expense > 0) {
                Surface(
                    color = textColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(bottom = 4.dp, start = 2.dp, end = 2.dp)
                ) {
                    Text(
                        text = "-${formatShortValue(expense)}",
                        color = textColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

fun formatShortValue(value: Long): String {
    return when {
        value >= 1000000 -> "${value / 1000000}M"
        value >= 1000 -> "${value / 1000}K"
        else -> value.toString()
    }
}
