package com.ltsw.animo.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material 3 Expressive Shapes
 *
 * Provides a more expressive and playful visual style with varied corner radiuses.
 * Perfect for a pet care app that should feel friendly and approachable.
 */
val AnimoShapes = Shapes(
    // Extra small - Subtle rounding for toggles, chips, small UI elements
    extraSmall = RoundedCornerShape(4.dp),

    // Small - Noticeable rounding for buttons, badges
    small = RoundedCornerShape(12.dp),

    // Medium - Expressive rounding for cards, input fields
    medium = RoundedCornerShape(20.dp),

    // Large - Dramatic rounding for dialogs, sheets, large cards
    large = RoundedCornerShape(28.dp),

    // Extra large - Hero rounding for prominent UI elements
    extraLarge = RoundedCornerShape(36.dp)
)
