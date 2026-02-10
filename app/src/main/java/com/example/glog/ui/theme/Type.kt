package com.example.glog.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

private fun TextStyle.scale(factor: Float) = copy(
    fontSize = (fontSize.value * factor).sp
)

fun scaledTypography(scale: Float): Typography = Typography(
    displayLarge = Typography.displayLarge.scale(scale),
    displayMedium = Typography.displayMedium.scale(scale),
    displaySmall = Typography.displaySmall.scale(scale),
    headlineLarge = Typography.headlineLarge.scale(scale),
    headlineMedium = Typography.headlineMedium.scale(scale),
    headlineSmall = Typography.headlineSmall.scale(scale),
    titleLarge = Typography.titleLarge.scale(scale),
    titleMedium = Typography.titleMedium.scale(scale),
    titleSmall = Typography.titleSmall.scale(scale),
    bodyLarge = Typography.bodyLarge.scale(scale),
    bodyMedium = Typography.bodyMedium.scale(scale),
    bodySmall = Typography.bodySmall.scale(scale),
    labelLarge = Typography.labelLarge.scale(scale),
    labelMedium = Typography.labelMedium.scale(scale),
    labelSmall = Typography.labelSmall.scale(scale)
)