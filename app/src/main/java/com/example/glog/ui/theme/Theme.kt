package com.example.glog.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
private val GLogDarkColorScheme = darkColorScheme(
    primary = GamerCyan,
    onPrimary = GamerDarkOnPrimary,
    primaryContainer = GamerCianDim,
    onPrimaryContainer = GamerDarkOnBackground,
    secondary = GamerOrange,
    onSecondary = GamerDarkOnSecondary,
    secondaryContainer = GamerDarkSurfaceVariant,
    onSecondaryContainer = GamerDarkOnSurface,
    tertiary = GamerGreen,
    onTertiary = GamerDarkOnTertiary,
    tertiaryContainer = GamerDarkSurfaceContainerHighest,
    onTertiaryContainer = GamerDarkOnSurface,
    error = GamerRed,
    onError = GamerDarkOnPrimary,
    errorContainer = GamerDarkSurfaceVariant,
    onErrorContainer = GamerRed,
    background = GamerDarkBackground,
    onBackground = GamerDarkOnBackground,
    surface = GamerDarkSurface,
    onSurface = GamerDarkOnSurface,
    surfaceVariant = GamerDarkSurfaceVariant,
    onSurfaceVariant = GamerDarkOnSurfaceVariant,
    surfaceContainerHighest = GamerDarkSurfaceContainerHighest,
    outline = GamerDarkOutline,
    outlineVariant = GamerDarkOutlineVariant
)
private val GLogLightColorScheme = lightColorScheme(
    primary = GamerCianLight,
    onPrimary = GamerLightOnPrimary,
    primaryContainer = GamerCianLightDim,
    onPrimaryContainer = GamerLightOnBackground,
    secondary = GamerOrangeLight,
    onSecondary = GamerLightOnSecondary,
    secondaryContainer = GamerLightSurfaceVariant,
    onSecondaryContainer = GamerLightOnSurface,
    tertiary = GamerGreenLight,
    onTertiary = GamerLightOnTertiary,
    tertiaryContainer = GamerLightSurfaceContainerHighest,
    onTertiaryContainer = GamerLightOnSurface,
    error = GamerRedLight,
    onError = GamerLightOnPrimary,
    errorContainer = GamerLightSurfaceVariant,
    onErrorContainer = GamerRedLight,
    background = GamerLightBackground,
    onBackground = GamerLightOnBackground,
    surface = GamerLightSurface,
    onSurface = GamerLightOnSurface,
    surfaceVariant = GamerLightSurfaceVariant,
    onSurfaceVariant = GamerLightOnSurfaceVariant,
    surfaceContainerHighest = GamerLightSurfaceContainerHighest,
    outline = GamerLightOutline,
    outlineVariant = GamerLightOutlineVariant
)

private const val LARGE_TEXT_SCALE = 1.15f

@Composable
fun GLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useLargeText: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> GLogDarkColorScheme
        else -> GLogLightColorScheme
    }

    val typography = if (useLargeText) scaledTypography(LARGE_TEXT_SCALE) else Typography

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = {
            val currentDensity = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = currentDensity.density,
                    fontScale = if (useLargeText) LARGE_TEXT_SCALE else currentDensity.fontScale
                )
            ) {
                content()
            }
        }
    )
}
