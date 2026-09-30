package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EscapeRed,
    onPrimary = Color.White,
    primaryContainer = EscapeRedContainer,
    onPrimaryContainer = EscapeRedOnContainer,
    secondary = EscapeRed,
    onSecondary = Color.White,
    secondaryContainer = EscapeSurfaceElevated,
    onSecondaryContainer = EscapeTextPrimary,
    tertiary = EscapeInfo,
    onTertiary = Color.White,
    background = EscapeBackground,
    onBackground = EscapeTextPrimary,
    surface = EscapeSurface,
    onSurface = EscapeTextPrimary,
    surfaceVariant = EscapeSurfaceVariant,
    onSurfaceVariant = EscapeTextSecondary,
    outline = EscapeBorder,
    outlineVariant = EscapeBorder,
    error = EscapeError,
    onError = Color.White,
    errorContainer = EscapeErrorContainer,
    onErrorContainer = Color(0xFFFFDAD6)
)

private val LightColorScheme = DarkColorScheme // Escape Clothing primary direction is Dark/Charcoal POS theme

@Composable
fun EscapeClothingTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

// Keep alias for backwards compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = EscapeClothingTheme(darkTheme = true, content = content)
