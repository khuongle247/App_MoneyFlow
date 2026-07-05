package com.example.moneyflow.features.statistics.presentation.screen

import androidx.compose.ui.text.style.TextOverflow
import com.example.moneyflow.ui.navigation.AppRoute
import java.util.Locale
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneyflow.R
import com.example.moneyflow.features.budget.presentation.model.BudgetUiModel
import com.example.moneyflow.features.statistics.presentation.model.*
import com.example.moneyflow.features.statistics.presentation.viewmodel.StatisticsViewModel
import com.example.moneyflow.ui.theme.*
import com.example.moneyflow.utils.Formatter.formatCurrency
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val filterState by viewModel.filterState.collectAsState()
    val contentState by viewModel.contentState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigate(AppRoute.Settings.route) }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                    Text(
                        text = "MoneyFlow",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = { viewModel.onEvent(StatisticsEvent.OnToggleDatePicker) }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { navController.navigate(AppRoute.Notifications.route) }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
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
                Text(
                    text = "Phân tích & Thống kê",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            item {
                // Month Selector
                Surface(
                    onClick = { viewModel.onEvent(StatisticsEvent.OnToggleDatePicker) },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = filterState.selectedMonthLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                // Chart Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
                            BudgetDonutChart(contentState.budgetList, contentState.spendingMap)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            contentState.budgetList.take(4).forEach { budget ->
                                LegendItem(budget, contentState.spendingMap[budget.id] ?: 0L, contentState.spendingMap.values.sum())
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                // Calendar Section
                Text(
                    text = "Lịch Chi Tiêu",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                CalendarInStats(viewModel, filterState.selectedMonthId)
            }
        }
    }

    if (filterState.isDatePickerVisible) {
        val parts = filterState.selectedMonthId.split("-")
        val currentMonth = if (parts.size == 2) parts[0].toInt() else 1
        val currentYear = if (parts.size == 2) parts[1].toInt() else 2024
        
        FastMonthPickerDialog(
            initialMonth = currentMonth,
            initialYear = currentYear,
            onDismiss = { viewModel.onEvent(StatisticsEvent.OnToggleDatePicker) },
            onConfirm = { m: Int, y: Int -> 
                viewModel.onEvent(StatisticsEvent.OnChangeMonth(String.format(Locale.US, "%02d-%d", m, y)))
            }
        )
    }
}

@Composable
fun FastMonthPickerDialog(initialMonth: Int, initialYear: Int, onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit) {
    var tempYear by remember { mutableIntStateOf(initialYear) }
    val months = listOf("Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12")
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {}, title = { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { tempYear-- }) { Icon(Icons.Default.ChevronLeft, "Giảm năm") }; Text(text = tempYear.toString(), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = PrimaryBlue); IconButton(onClick = { tempYear++ }) { Icon(Icons.Default.ChevronRight, "Tăng năm") } } }, text = { Column(modifier = Modifier.fillMaxWidth()) { val chunks = months.chunked(3); chunks.forEachIndexed { rowIndex, rowMonths -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { rowMonths.forEachIndexed { colIndex, monthName -> val monthValue = rowIndex * 3 + colIndex + 1; val isSelected = monthValue == initialMonth && tempYear == initialYear; Surface(modifier = Modifier.weight(1f).padding(vertical = 4.dp).height(48.dp), shape = RoundedCornerShape(12.dp), color = if (isSelected) PrimaryBlue else Color(0xFFF5F5F5), onClick = { onConfirm(monthValue, tempYear) }) { Box(contentAlignment = Alignment.Center) { Text(text = monthName, color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp) } } } } } } }, shape = RoundedCornerShape(28.dp), containerColor = Color.White)
}

@Composable
fun LegendItem(budget: BudgetUiModel, spent: Long, total: Long) {
    val percent = if (total > 0) (spent.toDouble() / total * 100).toInt() else 0
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color(budget.color), CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(budget.name, fontSize = 11.sp, color = TextGray, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("$percent%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun BudgetDonutChart(budgets: List<BudgetUiModel>, spendingMap: Map<Long, Long>) {
    val totalSpentRaw = spendingMap.values.sum()
    if (budgets.isEmpty() || totalSpentRaw == 0L) {
        Box(contentAlignment = Alignment.Center) {
            Text("No data", color = TextGray)
        }
        return
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        var startAngle = -90f
        budgets.forEach { budget ->
            val spent = spendingMap[budget.id] ?: 0L
            if (spent > 0) {
                val sweepAngle = (spent.toDouble() / totalSpentRaw * 360).toFloat()
                drawArc(
                    color = Color(budget.color),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 40f)
                )
                startAngle += sweepAngle
            }
        }
    }
}

@Composable
fun CalendarInStats(viewModel: StatisticsViewModel, selectedMonthId: String) {
    val parts = selectedMonthId.split("-")
    if (parts.size != 2) return
    
    val month = parts[0].toInt()
    val year = parts[1].toInt()
    
    val calendar = Calendar.getInstance().apply {
        set(Calendar.MONTH, month - 1)
        set(Calendar.YEAR, year)
    }
    
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
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
    
    val dailySummary by viewModel.getDailySummary(startDate, endDate).collectAsState(initial = emptyList())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val weekDays = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
                weekDays.forEach {
                    Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = TextGray, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            // Task: Spreading out the calendar cells and adding background colors to ALL cells
            val firstDayAdj = (firstDayOfWeek + 6) % 7
            val totalCellsNeeded = firstDayAdj + daysInMonth
            val rowsNeeded = (totalCellsNeeded + 6) / 7
            val gridHeight = (rowsNeeded * 55).dp // Calculated height to spread out

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(gridHeight),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(firstDayAdj) { Spacer(modifier = Modifier.fillMaxSize()) }
                items(daysInMonth) { day ->
                    val dayNum = day + 1
                    val summary = dailySummary.find { item ->
                        val cal = Calendar.getInstance().apply { timeInMillis = item.date }
                        cal.get(Calendar.DAY_OF_MONTH) == dayNum
                    }
                    CalendarDayStats(dayNum, summary?.totalExpense ?: 0L)
                }
            }
        }
    }
}

@Composable
fun CalendarDayStats(day: Int, expense: Long) {
    // Task: Every cell has a background color intensity based on spend, like a heatmap
    val bgColor = when {
        expense == 0L -> Color(0xFFF8F9FA) // Very light grey for empty days
        expense < 100000 -> Color(0xFFE8F5E9) // Light Green
        expense < 500000 -> Color(0xFFFFF3E0) // Light Orange
        else -> Color(0xFFFFEBEE) // Light Red
    }
    
    val textColor = when {
        expense == 0L -> MaterialTheme.colorScheme.onSurface
        expense < 100000 -> SuccessGreen
        expense < 500000 -> Color(0xFFFF9800)
        else -> ErrorRed
    }

    Surface(
        modifier = Modifier.aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(day.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            if (expense > 0) {
                Text(
                    "-${formatShortValue(expense)}",
                    color = textColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
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
