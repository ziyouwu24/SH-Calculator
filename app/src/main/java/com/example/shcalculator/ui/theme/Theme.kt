package com.example.shcalculator.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun previewColorFor(palette: ColorPalette): Color = when (palette) {
    ColorPalette.MEADOW -> LimeGreen40
    ColorPalette.OCEAN -> Teal40
    ColorPalette.SUNSET -> Sunset40
    ColorPalette.LAVENDER -> Lavender40
}

private fun lightSchemeFor(palette: ColorPalette): ColorScheme = when (palette) {
    ColorPalette.MEADOW -> lightColorScheme(
        primary = LimeGreen40,
        onPrimary = Color.White,

        secondary = Beige80,
        onSecondary = Beige40,
        secondaryContainer = Beige90,

        background = Beige90,
        onBackground = BrownText,
    )

    ColorPalette.OCEAN -> lightColorScheme(
        primary = Teal40,
        onPrimary = Color.White,

        secondary = SkyGray80,
        onSecondary = SkyGray40,
        secondaryContainer = SkyGray90,

        background = SkyGray90,
        onBackground = SlateText,
    )

    ColorPalette.SUNSET -> lightColorScheme(
        primary = Sunset40,
        onPrimary = Color.White,

        secondary = CreamPink80,
        onSecondary = CreamPink40,
        secondaryContainer = CreamPink90,

        background = CreamPink90,
        onBackground = MaroonText,
    )

    ColorPalette.LAVENDER -> lightColorScheme(
        primary = Lavender40,
        onPrimary = Color.White,

        secondary = MistGray80,
        onSecondary = MistGray40,
        secondaryContainer = MistGray90,

        background = MistGray90,
        onBackground = PlumText,
    )
}

@Composable
fun SHCalculatorTheme(
    colorPalette: ColorPalette = AppColorState.colorPalette,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context)
        }
        else -> lightSchemeFor(colorPalette)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}