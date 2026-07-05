package com.example.moneyflow.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// True Black Design System
val BlackBackground = Color(0xFF000000)
val BlackSurface = Color(0xFF121212)
val BlackContainer = Color(0xFF1E1E1E)
val WhiteText = Color(0xFFFFFFFF)

// New Design System based on reference image
val PrimaryBlue = Color(0xFF5C9DFF) // Lighter blue from image
val SecondaryBlue = Color(0xFF42A5F5)
val AppBackground = Color(0xFFF5F7FA) // Light grey background
val CardWhite = Color(0xFFFFFFFF)

val SuccessGreen = Color(0xFF4CAF50)
val WarningOrange = Color(0xFFFF9800)
val ErrorRed = Color(0xFFF44336)
val TextDark = Color(0xFF1A1C1E)
val TextGray = Color(0xFF74777F)

val BlueGradientHorizontal = Brush.horizontalGradient(
    colors = listOf(PrimaryBlue, SecondaryBlue)
)

// Legacy compatibility
val BluePrimary = PrimaryBlue
val BlueDark = Color(0xFF0D47A1)
val BlueBackground = AppBackground
val EmeraldGreen = SuccessGreen
val CoralExpense = ErrorRed
val SurfaceGray = AppBackground
val PremiumGradient = BlueGradientHorizontal

// Standard Material 3 legacy
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
