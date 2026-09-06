package com.example.shcalculator.ui.theme

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

private val LightColors = lightColorScheme(
    primary = LimeGreen40,//
    onPrimary = Color.White,//
    primaryContainer = LimeGreen80,
    onPrimaryContainer = LimeGreenDark,

    secondary = Beige80,//
    onSecondary = Beige40,//
    secondaryContainer = Beige90,//
    onSecondaryContainer = BrownText,

    background = Beige90,//
    onBackground = BrownText,//

    surface = Beige90,
    onSurface = BrownText,
    surfaceVariant = Beige80,
    onSurfaceVariant = BrownText,

    tertiary = PortalPurple,
    onTertiary = Color.White,

    error = ErrorRed,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = LimeGreen80,
    onPrimary = LimeGreenDark,
    primaryContainer = LimeGreen40,
    onPrimaryContainer = Color.White,

    secondary = Beige80,
    onSecondary = BrownText,
    secondaryContainer = Beige40,
    onSecondaryContainer = Color.White,

    background = Color(0xFF211D14),
    onBackground = Beige90,

    surface = Color(0xFF2B2618),
    onSurface = Beige90,
    surfaceVariant = Color(0xFF3A331F),
    onSurfaceVariant = Beige80,

    tertiary = PortalPurple,
    onTertiary = Color.White,

    error = ErrorRed,
    onError = Color.White,
)

@Composable
fun SHCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}