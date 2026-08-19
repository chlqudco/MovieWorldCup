package com.chlqudco.movieworldcup.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MovieColorScheme = darkColorScheme(
    primary = CinemaRed,
    onPrimary = CinemaWhite,
    primaryContainer = CinemaRedDark,
    onPrimaryContainer = CinemaWhite,
    secondary = CinemaGold,
    onSecondary = CinemaBlack,
    background = CinemaBlack,
    onBackground = CinemaWhite,
    surface = CinemaSurface,
    onSurface = CinemaWhite,
    surfaceVariant = CinemaSurfaceHigh,
    onSurfaceVariant = CinemaMuted,
    outline = CinemaOutline
)

@Composable
fun MovieWorldCupTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MovieColorScheme,
        typography = Typography,
        content = content
    )
}
