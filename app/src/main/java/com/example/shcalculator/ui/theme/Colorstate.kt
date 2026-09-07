package com.example.shcalculator.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
enum class ColorPalette(val displayName: String) {
    MEADOW("Meadow"),
    OCEAN("Ocean"),
    SUNSET("Sunset"),
    LAVENDER("Lavender")
}

object AppColorState {
    var colorPalette by mutableStateOf(ColorPalette.MEADOW)
}