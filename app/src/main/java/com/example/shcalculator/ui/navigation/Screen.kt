package com.example.shcalculator.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Tutorial : Screen("tutorial", "Tutorial", Icons.Default.Add)
    object Calculator : Screen("calculator", "Calculator", Icons.Default.Add)
    object Settings : Screen("settings", "Settings", Icons.Default.Add)
}

val bottomNavItems = listOf(Screen.Tutorial, Screen.Calculator, Screen.Settings)