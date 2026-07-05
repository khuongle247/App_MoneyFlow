package com.example.moneyflow.features.goal.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneyflow.R
import com.example.moneyflow.ui.theme.*
import com.example.moneyflow.features.goal.presentation.viewmodel.GoalViewModel
import com.example.moneyflow.features.goal.presentation.model.SavingGoalUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingGoalScreen(
    navController: NavController,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val goals by viewModel.savingGoals.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingGoalId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(color = PrimaryBlue, shadowElevation = 0.dp) {
                Column(modifier = Modifier.statusBarsPadding()) {
                    TopAppBar(
                        title = { Text(stringResource(R.string.saving_goals), fontWeight = FontWeight.Bold, color = Color.White) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue, titleContentColor = Color.White)
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(color = PrimaryBlue, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(40.dp)) }
                items(goals) { goal ->
                    GoalItem(
                        goal = goal, 
                        onDelete = { },
                        onEdit = { editingGoalId = goal.id; showDialog = true }
                    )
                }
                if (goals.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.no_goals), color = Color.Gray)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showDialog) {
        GoalDialog(
            isEdit = editingGoalId != null,
            onDismiss = { showDialog = false },
            onConfirm = { name, target, current ->
                if (editingGoalId == null) {
                    viewModel.addSavingGoal(name, target, current)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun GoalDialog(isEdit: Boolean, onDismiss: () -> Unit, onConfirm: (String, Double, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var current by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (!isEdit) stringResource(R.string.new_goal) else stringResource(R.string.edit_goal)) }, text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.goal_name)) }, shape = RoundedCornerShape(12.dp)); OutlinedTextField(value = target, onValueChange = { if (it.all { c -> c.isDigit() }) target = it }, label = { Text(stringResource(R.string.target_amount)) }, shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)); OutlinedTextField(value = current, onValueChange = { if (it.all { c -> c.isDigit() }) current = it }, label = { Text(stringResource(R.string.current_amount)) }, shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) } }, confirmButton = { Button(onClick = { onConfirm(name, target.toDoubleOrNull() ?: 0.0, current.toDoubleOrNull() ?: 0.0) }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue), enabled = name.isNotBlank() && target.isNotBlank()) { Text(if (!isEdit) stringResource(R.string.add) else stringResource(R.string.save_btn)) } }, dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_btn)) } } )
}

@Composable
fun GoalItem(goal: SavingGoalUiModel, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onEdit() }, shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(goal.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit), tint = PrimaryBlue, modifier = Modifier.size(20.dp)) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = ErrorRed, modifier = Modifier.size(20.dp)) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(progress = { goal.progress }, modifier = Modifier.fillMaxWidth().height(10.dp), color = PrimaryBlue, trackColor = PrimaryBlue.copy(alpha = 0.1f), strokeCap = androidx.compose.ui.graphics.StrokeCap.Round)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text("${goal.progressPercent}%", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue); Text(stringResource(R.string.completed), style = MaterialTheme.typography.labelSmall, color = Color.Gray) }
                Column(horizontalAlignment = Alignment.End) { Text(goal.remainingText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface); Text(stringResource(R.string.missing), style = MaterialTheme.typography.labelSmall, color = Color.Gray) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("${goal.currentText} / ${goal.targetText}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
