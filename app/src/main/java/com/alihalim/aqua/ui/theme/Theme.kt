package com.alihalim.aqua.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.alihalim.aqua.data.ThemeMode

@Composable
fun AquaTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentHex: String = "#FF8A3D",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val accent = parseHexColor(accentHex)
    val onAccent = onAccentColor(accent)

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = accent,
            onPrimary = onAccent,
            primaryContainer = accent.copy(alpha = 0.18f),
            onPrimaryContainer = accent,
            secondary = accent,
            onSecondary = onAccent,
            background = AquaBlack,
            onBackground = AquaOnDark,
            surface = AquaSurfaceDark,
            onSurface = AquaOnDark,
            surfaceVariant = AquaSurfaceVariantDark,
            onSurfaceVariant = AquaMutedDark,
            outline = Color(0xFF2A2A2A),
            outlineVariant = Color(0xFF1F1F1F)
        )
    } else {
        lightColorScheme(
            primary = accent,
            onPrimary = onAccent,
            primaryContainer = accent.copy(alpha = 0.14f),
            onPrimaryContainer = accent,
            secondary = accent,
            onSecondary = onAccent,
            background = AquaWhite,
            onBackground = AquaOnLight,
            surface = AquaSurfaceLight,
            onSurface = AquaOnLight,
            surfaceVariant = AquaSurfaceVariantLight,
            onSurfaceVariant = AquaMutedLight,
            outline = Color(0xFFD8D8DE),
            outlineVariant = Color(0xFFE6E6EA)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AquaTypography,
        content = content
    )
}
