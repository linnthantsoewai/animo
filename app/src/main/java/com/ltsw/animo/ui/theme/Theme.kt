package com.ltsw.animo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light theme - Clean and modern like Google apps
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1967D2), // Google Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2E3FC), // Light blue container
    onPrimaryContainer = Color(0xFF041E49),

    secondary = Color(0xFF5F6368), // Google Gray
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8EAED),
    onSecondaryContainer = Color(0xFF1A1C1E),

    tertiary = Color(0xFF0B8043), // Google Green
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC2F0D1),
    onTertiaryContainer = Color(0xFF002109),

    error = Color(0xFFD93025), // Google Red
    onError = Color.White,
    errorContainer = Color(0xFFFDE7E9),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFF5F5F5), // Light gray background for contrast
    onBackground = Color(0xFF202124), // Almost black

    surface = Color.White, // Pure white cards
    onSurface = Color(0xFF202124),

    surfaceVariant = Color(0xFFF5F5F5), // Light gray variant
    onSurfaceVariant = Color(0xFF5F6368),

    outline = Color(0xFFDADCE0), // Visible borders
    outlineVariant = Color(0xFFE8EAED),

    inverseSurface = Color(0xFF2E3134),
    inverseOnSurface = Color(0xFFF1F3F4),
    inversePrimary = Color(0xFF8AB4F8),

    surfaceTint = Color(0xFF1967D2),
    scrim = Color(0xFF000000),
)

// Dark theme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8AB4F8), // Lighter blue for dark mode
    onPrimary = Color(0xFF0A2E5C),
    primaryContainer = Color(0xFF0B57D0), // Deeper blue
    onPrimaryContainer = Color(0xFFD2E3FC),

    secondary = Color(0xFFBDC1C6), // Light gray for text
    onSecondary = Color(0xFF2E3134),
    secondaryContainer = Color(0xFF3C4043), // Gray containers
    onSecondaryContainer = Color(0xFFE8EAED),

    tertiary = Color(0xFF81C995), // Light green
    onTertiary = Color(0xFF003919),
    tertiaryContainer = Color(0xFF0B8043),
    onTertiaryContainer = Color(0xFFC2F0D1),

    error = Color(0xFFF28B82), // Light red
    onError = Color(0xFF601410),
    errorContainer = Color(0xFFD93025),
    onErrorContainer = Color(0xFFFDE7E9),

    background = Color(0xFF000000), // Pitch black for true OLED dark mode
    onBackground = Color(0xFFE8EAED), // Light gray text

    surface = Color(0xFF1A1A1A), // Very dark gray for elevated surfaces
    onSurface = Color(0xFFE8EAED),

    surfaceVariant = Color(0xFF2A2A2A), // Slightly lighter for variants
    onSurfaceVariant = Color(0xFFBDC1C6),

    outline = Color(0xFF5F6368), // Subtle borders in dark
    outlineVariant = Color(0xFF3C4043),

    inverseSurface = Color(0xFFE8EAED),
    inverseOnSurface = Color(0xFF000000),
    inversePrimary = Color(0xFF1967D2),

    surfaceTint = Color(0xFF8AB4F8),
    scrim = Color(0xFF000000),

    // Surface brightness levels for elevation - all very dark for OLED
    surfaceBright = Color(0xFF2A2A2A),
    surfaceDim = Color(0xFF0A0A0A),
    surfaceContainer = Color(0xFF1A1A1A),
    surfaceContainerHigh = Color(0xFF252525),
    surfaceContainerHighest = Color(0xFF303030),
    surfaceContainerLow = Color(0xFF121212),
    surfaceContainerLowest = Color(0xFF000000),
)

@Composable
fun AnimoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}