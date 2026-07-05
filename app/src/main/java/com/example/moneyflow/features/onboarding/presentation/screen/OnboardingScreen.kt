package com.example.moneyflow.features.onboarding.presentation.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneyflow.ui.navigation.AppRoute
import com.example.moneyflow.ui.theme.PrimaryBlue
import com.example.moneyflow.features.onboarding.presentation.viewmodel.OnboardingViewModel
import com.example.moneyflow.features.onboarding.presentation.model.OnboardingEffect

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    var step by remember { mutableIntStateOf(1) }
    var name by remember { mutableStateOf("") }
    var balanceText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OnboardingEffect.Navigate -> {
                    navController.navigate(effect.route) {
                        popUpTo(AppRoute.Onboarding.route) { inclusive = true }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (step == 1) "Chào mừng bạn! 👋" else "Thiết lập số dư 💰",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (step == 1) "Hãy cho chúng tôi biết tên của bạn để bắt đầu nhé." else "Nhập số tiền hiện có trong ví hoặc tài khoản của bạn.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedContent(targetState = step, label = "OnboardingStep") { targetStep ->
                if (targetStep == 1) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên của bạn") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                } else {
                    OutlinedTextField(
                        value = balanceText,
                        onValueChange = { if (it.all { c -> c.isDigit() }) balanceText = it },
                        label = { Text("Số dư hiện tại") },
                        suffix = { Text("₫") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step == 2) {
                    IconButton(onClick = { step = 1 }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = PrimaryBlue)
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(2) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (step == index + 1) 24.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(if (step == index + 1) PrimaryBlue else Color.LightGray)
                        )
                    }
                }

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (step == 1 && name.isNotBlank()) {
                            step = 2
                        } else if (step == 2 && balanceText.isNotEmpty()) {
                            val amount = balanceText.toDoubleOrNull() ?: 0.0
                            viewModel.completeOnboarding(name, amount)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    enabled = if (step == 1) name.isNotBlank() else balanceText.isNotEmpty()
                ) {
                    if (step == 1) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Tiếp tục")
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Bắt đầu")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.Check, contentDescription = "Hoàn tất")
                        }
                    }
                }
            }
        }
    }
}
