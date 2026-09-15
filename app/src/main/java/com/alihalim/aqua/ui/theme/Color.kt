package com.alihalim.aqua.ui.theme

import androidx.compose.ui.graphics.Color

// Core palette
val AquaBlack = Color(0xFF000000)
val AquaSurfaceDark = Color(0xFF0E0E0E)
val AquaSurfaceVariantDark = Color(0xFF1A1A1A)
val AquaOnDark = Color(0xFFEDEDED)
val AquaMutedDark = Color(0xFF8A8A8A)

val AquaWhite = Color(0xFFFFFFFF)
val AquaSurfaceLight = Color(0xFFF7F7F8)
val AquaSurfaceVariantLight = Color(0xFFEDEDF0)
val AquaOnLight = Color(0xFF101012)
val AquaMutedLight = Color(0xFF6B6B70)

val AquaOrange = Color(0xFFFF8A3D)

fun parseHexColor(hex: String, fallback: Color = AquaOrange): Color {
    return runCatching {
        val clean = hex.removePrefix("#")
        val value = clean.toLong(16)
        when (clean.length) {
            6 -> Color(0xFF000000 or value)
            8 -> Color(value)
            else -> fallback
        }
    }.getOrDefault(fallback)
}

/** Picks black or white text depending on how bright the accent is. */
fun onAccentColor(accent: Color): Color {
    val luminance = 0.299 * accent.red + 0.587 * accent.green + 0.114 * accent.blue
    return if (luminance > 0.6) Color(0xFF101012) else Color(0xFFFFFFFF)
}
