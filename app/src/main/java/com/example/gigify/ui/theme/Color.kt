package com.example.gigify.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Colors from the background image provided
val AppVibrantPurple = Color(0xFF9B30FF) // #9B30FF
val AppBlue = Color(0xFF3D79FF)          // #3D79FF

// Lavender color requested as the "main purple"
val AppLavender = Color(0xFFE6E6FA)      // #E6E6FA

// The gradient exactly matching the provided image
val AppGradient = Brush.horizontalGradient(
    colors = listOf(
        AppVibrantPurple,
        AppBlue
    )
)

// Theme mapping
val AppBackground = AppVibrantPurple
val AppSurface = AppLavender
val AppPrimary = AppLavender // High-contrast accent for the dark background

// Legacy compatibility mappings
val White = Color(0xFFF8F8FF) // Ghost White (very close to white but with a hint of blue/lavender)
val LightPurple = AppLavender
val MainPurple = AppLavender
val DarkPurple = AppVibrantPurple

val StatusGreenBg = Color(0xFFE8F5E9)
val StatusGreenText = Color(0xFF2E7D32)
val StatusOrangeBg = Color(0xFFFFF3E0)
val StatusOrangeText = Color(0xFFE65100)
