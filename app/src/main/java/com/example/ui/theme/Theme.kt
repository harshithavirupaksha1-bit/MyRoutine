package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = NaturalPrimary,
    secondary = NaturalSecondary,
    tertiary = NaturalHighlightGold,
    background = NaturalBg,
    surface = NaturalSurface,
    onBackground = NaturalOnBg,
    onSurface = NaturalOnSurface,
    primaryContainer = Color(0xFFECEBE4), // Light sage tint
    onPrimaryContainer = NaturalPrimary,
    secondaryContainer = NaturalSecondary,
    onSecondaryContainer = NaturalPrimary,
    surfaceVariant = Color(0xFFF5F4ED),
    onSurfaceVariant = NaturalMuted,
    outline = NaturalTertiary,
    error = NaturalAccentRed
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA6AF90), // Brighter sage for dark mode
    secondary = Color(0xFF2C2F25),
    tertiary = NaturalHighlightGold,
    background = Color(0xFF1B1D17), // Dark olive black
    surface = Color(0xFF24271F), // Dark olive surface
    onBackground = Color(0xFFE6E8E2),
    onSurface = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF3B3F32),
    onPrimaryContainer = Color(0xFFE6E8E2),
    secondaryContainer = Color(0xFF2C2F25),
    onSecondaryContainer = Color(0xFFA6AF90),
    surfaceVariant = Color(0xFF2C2F25),
    onSurfaceVariant = Color(0xFFA3A996),
    outline = Color(0xFF484D3E),
    error = Color(0xFFE57373)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
