package com.example.schedlue.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.schedlue.getColorScheme

val DarkColorScheme = darkColorScheme(
    primary = backgroundTopBarDark,
    secondary = background,
    tertiary = backgroundBarsDark,
    background = buttonsDark,
    surface = textDark
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = Color.White,
    tertiary = Color(0xFFe8e8e8),
    background = Color.White,
    surface = Color.Black

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun SchedlueTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
