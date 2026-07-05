package com.example.moneyflow.features.settings.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneyflow.features.settings.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CycleSettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val cycleStartDay by viewModel.cycleStartDay.collectAsState(initial = 1)
    var selectedDay by remember(cycleStartDay) { mutableIntStateOf(cycleStartDay) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chu kỳ tài chính", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Chọn ngày bắt đầu chu kỳ tài chính của bạn hàng tháng.",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ngày bắt đầu: ", fontWeight = FontWeight.Bold)
                
                Box {
                    var expanded by remember { mutableStateOf(false) }
                    TextButton(onClick = { expanded = true }) {
                        Text("Ngày $selectedDay")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        (1..28).forEach { day ->
                            DropdownMenuItem(
                                text = { Text("Ngày $day") },
                                onClick = {
                                    selectedDay = day
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    viewModel.saveCycleStartDay(selectedDay)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Lưu thiết lập", fontWeight = FontWeight.Bold)
            }
        }
    }
}
