package com.example.moneyflow.features.budget.presentation.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.toArgb
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneyflow.R
import com.example.moneyflow.features.budget.presentation.model.*
import com.example.moneyflow.features.budget.presentation.viewmodel.BudgetViewModel
import com.example.moneyflow.ui.theme.*
import com.example.moneyflow.utils.IconHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    navController: NavController,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val contentState by viewModel.contentState.collectAsState()
    val screenState by viewModel.screenState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Quản lý Ngân Sách", fontWeight = FontWeight.ExtraBold) 
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(BudgetEvent.OnShowAddDialog) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Cycle Selector
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { /* Previous Cycle logic */ }) { 
                        Icon(Icons.Default.ChevronLeft, null, tint = PrimaryBlue) 
                    }
                    Text("25/10 - 24/11", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    IconButton(onClick = { /* Next Cycle logic */ }) { 
                        Icon(Icons.Default.ChevronRight, null, tint = PrimaryBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add Budget Button
            Button(
                onClick = { viewModel.onEvent(BudgetEvent.OnShowAddDialog) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Thêm Ngân Sách Mới", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(contentState.budgetList, key = { it.id }) { budget ->
                    PremiumBudgetCard(budget) {
                        viewModel.onEvent(BudgetEvent.OnDeleteBudget(budget.id))
                    }
                }
            }
        }
    }

    if (screenState.showAddBudgetDialog) {
        com.example.moneyflow.features.budget.presentation.screen.AddBudgetFullDialog(
            availableText = contentState.remainingText,
            usedColors = contentState.budgetList.map { it.color },
            onDismiss = { viewModel.onEvent(BudgetEvent.OnDismissAddDialog) },
            onConfirm = { name, amount, color, icon ->
                viewModel.onEvent(BudgetEvent.OnAddBudget(name, amount, color, icon))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetFullDialog(
    availableText: String,
    usedColors: List<Int>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    val allAvailableColors = listOf(
        Color(0xFF0066FF), Color(0xFF4CAF50), Color(0xFFFF9800), 
        Color(0xFF9C27B0), Color(0xFFE91E63), Color(0xFF00BCD4),
        Color(0xFFFF5722), Color(0xFFFFC107), Color(0xFFCDDC39), 
        Color(0xFF8BC34A), Color(0xFF009688), Color(0xFF3F51B5)
    )
    val filteredColors = allAvailableColors.filter { it.toArgb() !in usedColors }
    var selectedColor by remember { mutableStateOf(filteredColors.firstOrNull() ?: allAvailableColors.first()) }
    var selectedIcon by remember { mutableStateOf("wallet") }
    var showIconPicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(color = PrimaryBlue, modifier = Modifier.fillMaxWidth().height(70.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text("Thêm Ngân Sách", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên ngân sách") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
                OutlinedTextField(value = amountText, onValueChange = { if (it.all { c -> c.isDigit() }) amountText = it }, label = { Text("Số tiền dự kiến") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number))
                
                Button(onClick = { onConfirm(name, amountText.toDoubleOrNull() ?: 0.0, selectedColor.toArgb(), selectedIcon) }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue), enabled = name.isNotBlank() && amountText.isNotEmpty()) {
                    Text("Lưu Ngân Sách", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PremiumBudgetCard(budget: BudgetUiModel, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { /* Detail or Edit */ },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(budget.color).copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = IconHelper.getIcon(budget.iconName),
                            contentDescription = null,
                            tint = Color(budget.color),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(budget.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${budget.spentText} / ${budget.amountText}", fontSize = 12.sp, color = TextGray)
                }
                
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, null, tint = ErrorRed.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { budget.progress.coerceAtMost(1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = budget.progressColor,
                trackColor = budget.progressColor.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
