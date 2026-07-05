package com.example.moneyflow.features.onboarding.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneyflow.R
import com.example.moneyflow.ui.navigation.AppRoute
import com.example.moneyflow.ui.theme.PrimaryBlue
import com.example.moneyflow.features.onboarding.presentation.viewmodel.OnboardingViewModel
import com.example.moneyflow.features.onboarding.presentation.model.OnboardingEffect
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OnboardingEffect.Navigate -> {
                    navController.navigate(effect.route) {
                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(1500)
        viewModel.startApp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "MoneyFlow",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
