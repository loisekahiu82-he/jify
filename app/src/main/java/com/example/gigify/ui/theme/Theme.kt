package com.example.gigify.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AppPrimary,
    secondary = AppSurface,
    tertiary = AppPrimary,
    background = AppBackground,
    surface = AppSurface,
    onPrimary = AppBackground,
    onSecondary = AppPrimary,
    onBackground = AppPrimary,
    onSurface = AppPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = AppPrimary,
    secondary = AppSurface,
    tertiary = AppPrimary,
    background = AppBackground,
    surface = AppSurface,
    onPrimary = AppBackground,
    onSecondary = AppPrimary,
    onBackground = AppPrimary,
    onSurface = AppPrimary
)

@Composable
fun GigifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Both themes use the same 3 colors as requested
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
