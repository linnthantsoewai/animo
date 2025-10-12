package com.example.animo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Defines the color palette for the app in light mode.
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),      // A strong blue for primary actions
    background = Color.White,         // Standard white background
    surface = Color(0xFFF0F2F5),      // A light grey for card backgrounds and surfaces
    onBackground = Color(0xFF1A202C),  // Dark text color for readability
    onSurface = Color(0xFF1A202C)      // Dark text color for readability
)

// The main theme composable for the Animo app.
@Composable
fun AnimoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(), // Using default typography for now
        content = content
    )
}
