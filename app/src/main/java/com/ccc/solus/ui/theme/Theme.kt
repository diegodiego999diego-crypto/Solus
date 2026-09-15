package com.ccc.solus.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SolusLightColors = lightColorScheme(
    primary = Blue40,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Blue80,
    onPrimaryContainer = Blue40,
    secondary = BlueGrey40,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = BlueGrey80,
    onSecondaryContainer = BlueGrey40,
    tertiary = Cyan40,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = Cyan80,
    onTertiaryContainer = Cyan40,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceLight
)

private val SolusDarkColors = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue40,
    primaryContainer = Blue40,
    onPrimaryContainer = Blue80,
    secondary = BlueGrey80,
    onSecondary = BlueGrey40,
    secondaryContainer = BlueGrey40,
    onSecondaryContainer = BlueGrey80,
    tertiary = Cyan80,
    onTertiary = Cyan40,
    tertiaryContainer = Cyan40,
    onTertiaryContainer = Cyan80,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceDark
)

@Composable
fun SolusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SolusDarkColors else SolusLightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SolusTypography,
        content = content
    )
}
