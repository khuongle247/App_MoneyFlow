package com.example.moneyflow.features.transaction.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneyflow.R
import com.example.moneyflow.data.local.entity.TransactionType
import com.example.moneyflow.ui.theme.*
import com.example.moneyflow.utils.IconHelper
import com.example.moneyflow.features.transaction.presentation.viewmodel.TransactionViewModel
import com.example.moneyflow.features.transaction.presentation.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavController,
    viewModel: TransactionViewModel = hiltViewModel(),
    transactionId: Long = -1L
) {
    val context = LocalContext.current
    
    // 3-Level State Targeted Collection
    val filterState by viewModel.filterState.collectAsState()
    val contentState by viewModel.contentState.collectAsState()
    val screenState by viewModel.screenState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TransactionEffect.NavigateBack -> navController.popBackStack()
                is TransactionEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Prefill data if Editing
    LaunchedEffect(transactionId) {
        if (transactionId > 0) {
            val tx = viewModel.getTransactionById(transactionId)
            tx?.let {
                viewModel.onEvent(TransactionEvent.OnTitleChanged(it.title))
                viewModel.onEvent(TransactionEvent.OnAmountChanged(it.amount.toString()))
                viewModel.onEvent(TransactionEvent.OnTypeChanged(it.type))
                viewModel.onEvent(TransactionEvent.OnBudgetSelected(it.budgetId))
                viewModel.onEvent(TransactionEvent.OnIconSelected(it.icon))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (transactionId < 0) stringResource(R.string.add_transaction) else stringResource(R.string.edit_transaction), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (transactionId > 0) {
                        IconButton(onClick = { viewModel.onEvent(TransactionEvent.OnDeleteTransaction(transactionId)) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Amount Input bound to onEvent
            OutlinedTextField(
                value = filterState.amountText,
                onValueChange = { viewModel.onEvent(TransactionEvent.OnAmountChanged(it)) },
                label = { Text(stringResource(R.string.income_amount)) },
                suffix = { Text("₫", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                shape = RoundedCornerShape(16.dp)
            )

            // Title Input bound to onEvent
            OutlinedTextField(
                value = filterState.title,
                onValueChange = { viewModel.onEvent(TransactionEvent.OnTitleChanged(it)) },
                label = { Text(stringResource(R.string.transaction_content)) },
                placeholder = { Text("Ăn sáng, Mua sắm...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            // Type Selector
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TypeButton(
                    text = stringResource(R.string.expense),
                    isSelected = filterState.selectedType == TransactionType.EXPENSE,
                    color = ErrorRed,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onEvent(TransactionEvent.OnTypeChanged(TransactionType.EXPENSE)) }
                )
                TypeButton(
                    text = stringResource(R.string.income),
                    isSelected = filterState.selectedType == TransactionType.INCOME,
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onEvent(TransactionEvent.OnTypeChanged(TransactionType.INCOME)) }
                )
            }

            // Budget Selector (Only for Expense)
            if (filterState.selectedType == TransactionType.EXPENSE) {
                Text(stringResource(R.string.budget), fontWeight = FontWeight.Bold)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(contentState.budgets) { budget ->
                        BudgetChip(
                            name = budget.name,
                            isSelected = filterState.selectedBudgetId == budget.id,
                            onClick = { viewModel.onEvent(TransactionEvent.OnBudgetSelected(if (filterState.selectedBudgetId == budget.id) null else budget.id)) }
                        )
                    }
                }
            }

            // Icon Picker
            Text("Biểu tượng", fontWeight = FontWeight.Bold)
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(IconHelper.getAllIcons()) { (name, icon) ->
                    IconChip(
                        icon = icon,
                        isSelected = filterState.selectedIcon == name,
                        onClick = { viewModel.onEvent(TransactionEvent.OnIconSelected(name)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.onEvent(TransactionEvent.OnSaveTransaction) },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = filterState.title.isNotBlank() && filterState.amountText.isNotEmpty()
            ) {
                Text(if (transactionId < 0) stringResource(R.string.add) else stringResource(R.string.save_btn), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TypeButton(text: String, isSelected: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(48.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color else color.copy(alpha = 0.1f),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, color = if (isSelected) Color.White else color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BudgetChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.height(40.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) PrimaryBlue else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryBlue else Color.LightGray)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(name, color = if (isSelected) Color.White else Color.Gray, fontSize = 12.sp, maxLines = 1)
        }
    }
}

@Composable
fun IconChip(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(48.dp).clickable { onClick() },
        shape = CircleShape,
        color = if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else Color.Transparent,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, PrimaryBlue) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = if (isSelected) PrimaryBlue else Color.Gray)
        }
    }
}
