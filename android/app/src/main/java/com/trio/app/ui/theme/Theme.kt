package com.trio.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = TG_ActionBar,
    onPrimary = Color.White,
    primaryContainer = TG_ActionBarDark,
    secondary = Color(0xFF5EE7DF),
    onSecondary = Color.Black,
    background = TG_DarkBackground,
    onBackground = Color.White,
    surface = TG_DarkSurface,
    onSurface = Color.White,
    surfaceVariant = TG_DarkSurfaceVariant,
    onSurfaceVariant = TG_DarkTextSecondary,
    outline = TG_DarkSeparator,
    error = TG_UnreadBadge,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = TG_ActionBar,
    onPrimary = Color.White,
    primaryContainer = TG_ActionBarDark,
    secondary = TG_TextLink,
    onSecondary = Color.White,
    background = TG_Background,
    onBackground = TG_Text,
    surface = TG_Surface,
    onSurface = TG_Text,
    surfaceVariant = Color(0xFFF7F7F7),
    onSurfaceVariant = TG_TextSecondary,
    outline = TG_Separator,
    error = TG_UnreadBadge,
    onError = Color.White
)

@Composable
fun TrioTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun isDarkTheme(): Boolean = isSystemInDarkTheme()
