package com.example.moneyflow.features.transaction.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneyflow.R
import com.example.moneyflow.ui.navigation.AppRoute
import com.example.moneyflow.features.dashboard.presentation.screen.ModernTransactionItem
import com.example.moneyflow.ui.theme.*
import com.example.moneyflow.features.transaction.presentation.viewmodel.TransactionViewModel
import com.example.moneyflow.features.transaction.presentation.model.TransactionEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    // 3-Level State Targeted Collection
    val filterState by viewModel.filterState.collectAsState()
    val contentState by viewModel.contentState.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(color = PrimaryBlue, shadowElevation = 0.dp) {
                Column(modifier = Modifier.statusBarsPadding()) {
                    TopAppBar(
                        title = { Text(stringResource(R.string.history), fontWeight = FontWeight.Bold, color = Color.White) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue, titleContentColor = Color.White)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppRoute.Transaction.createRoute()) },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = androidx.compose.foundation.shape.CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_transaction))
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.fillMaxWidth().height(24.dp).background(color = PrimaryBlue, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)))
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(10.dp))
                
                // Search Input bound to onEvent
                OutlinedTextField(
                    value = filterState.searchQuery,
                    onValueChange = { viewModel.onEvent(TransactionEvent.OnSearchChanged(it)) },
                    placeholder = { Text(stringResource(R.string.history) + "...", color = TextGray) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryBlue) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f), focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(contentState.transactions, key = { it.id }) { transaction ->
                        val dismissState = rememberSwipeToDismissBoxState()
                        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                            LaunchedEffect(transaction) {
                                viewModel.onEvent(TransactionEvent.OnDeleteTransaction(transaction.id))
                                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                            }
                        }
                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.EndToStart -> ErrorRed
                                    else -> Color.Transparent
                                }
                                Box(modifier = Modifier.fillMaxSize().background(color, RoundedCornerShape(18.dp)).padding(horizontal = 24.dp), contentAlignment = Alignment.CenterEnd) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                }
                            }
                        ) {
                            ModernTransactionItem(transaction) {
                                navController.navigate(AppRoute.Transaction.createRoute(transaction.id))
                            }
                        }
                    }
                    if (contentState.transactions.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.empty), color = TextGray)
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}
