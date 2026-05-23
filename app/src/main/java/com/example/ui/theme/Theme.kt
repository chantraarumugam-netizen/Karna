package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkCyberColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = LaserPurple,
    tertiary = TechWhite,
    background = DarkBackground,
    surface = DarkCardSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = TechWhite,
    onSurface = TechWhite,
    outline = DarkBorderLaser
)

private val LightCyberColorScheme = lightColorScheme(
    primary = LightCyberBlue,
    secondary = AccentOrange,
    tertiary = Color.Black,
    background = LightGridBackground,
    surface = LightCardSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    outline = Color.LightGray
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // We default to true as the main premium futuristic UI
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkCyberColorScheme else LightCyberColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
