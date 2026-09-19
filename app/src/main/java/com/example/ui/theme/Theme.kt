package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ShortDramaColorScheme = darkColorScheme(
    primary = DramaRed,
    onPrimary = Color.White,
    primaryContainer = DramaCardElevated,
    onPrimaryContainer = Color.White,
    secondary = DramaAmber,
    onSecondary = Color.Black,
    secondaryContainer = DramaCard,
    onSecondaryContainer = DramaGold,
    background = DramaBlack,
    onBackground = TextPrimary,
    surface = DramaSurface,
    onSurface = TextPrimary,
    surfaceVariant = DramaCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = StatusBanned,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent cinematic dark palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ShortDramaColorScheme,
        typography = Typography,
        content = content
    )
}
