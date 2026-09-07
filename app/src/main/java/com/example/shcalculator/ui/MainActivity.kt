package com.example.shcalculator.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.shcalculator.ui.navigation.AppNavGraph
import com.example.shcalculator.ui.components.AppBottomBar
import com.example.shcalculator.ui.components.AppTopBar
import com.example.shcalculator.ui.theme.AppColorState
import com.example.shcalculator.ui.theme.SHCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()

    SHCalculatorTheme(
        colorPalette = AppColorState.colorPalette
    ) {
        Scaffold(
            topBar = { AppTopBar(title = "") },
            bottomBar = { AppBottomBar(navController = navController) }
        ) { innerPadding ->
            AppNavGraph(
                navController = navController,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            )
        }
    }
}