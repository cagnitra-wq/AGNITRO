package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TradingColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = WhitePure,
    primaryContainer = NavyChipBg,
    onPrimaryContainer = NavyDeep,
    secondary = GreenPrimary,
    onSecondary = WhitePure,
    secondaryContainer = GreenBadgeBg,
    onSecondaryContainer = GreenPrimary,
    tertiary = NavySecondary,
    onTertiary = WhitePure,
    background = WhitePure,
    onBackground = NavyDeep,
    surface = WhitePure,
    onSurface = NavyDeep,
    surfaceVariant = SurfaceTinted,
    onSurfaceVariant = NavySecondary,
    outline = NavyLightBorder,
    outlineVariant = DividerLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Strictly white background requested by user
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = TradingColorScheme,
        typography = Typography,
        content = content
    )
}
