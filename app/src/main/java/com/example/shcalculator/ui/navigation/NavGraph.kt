package com.example.shcalculator.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shcalculator.ui.screens.CalculatorScreen
import com.example.shcalculator.ui.screens.SettingsScreen
import com.example.shcalculator.ui.screens.TutorialScreen

private fun routeIndex(route: String?): Int =
    bottomNavItems.indexOfFirst { it.route == route }.let { if (it == -1) 0 else it }

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Calculator.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.Tutorial.route,
            enterTransition = { directionalEnter() },
            exitTransition = { directionalExit() },
            popEnterTransition = { directionalEnter() },
            popExitTransition = { directionalExit() }
        ) { TutorialScreen(navController) }

        composable(
            route = Screen.Calculator.route,
            enterTransition = { directionalEnter() },
            exitTransition = { directionalExit() },
            popEnterTransition = { directionalEnter() },
            popExitTransition = { directionalExit() }
        ) { CalculatorScreen() }

        composable(
            route = Screen.Settings.route,
            enterTransition = { directionalEnter() },
            exitTransition = { directionalExit() },
            popEnterTransition = { directionalEnter() },
            popExitTransition = { directionalExit() }
        ) { SettingsScreen() }
    }
}

// NOTE: concrete type parameter <NavBackStackEntry>, not <*>
private fun AnimatedContentTransitionScope<NavBackStackEntry>.directionalEnter(): EnterTransition {
    val initialIndex = routeIndex(initialState.destination.route)
    val targetIndex = routeIndex(targetState.destination.route)
    val direction = if (targetIndex >= initialIndex) {
        AnimatedContentTransitionScope.SlideDirection.Start
    } else {
        AnimatedContentTransitionScope.SlideDirection.End
    }
    return fadeIn(tween(300)) + slideIntoContainer(direction, tween(300))
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.directionalExit(): ExitTransition {
    val initialIndex = routeIndex(initialState.destination.route)
    val targetIndex = routeIndex(targetState.destination.route)
    val direction = if (targetIndex >= initialIndex) {
        AnimatedContentTransitionScope.SlideDirection.Start
    } else {
        AnimatedContentTransitionScope.SlideDirection.End
    }
    return fadeOut(tween(300)) + slideOutOfContainer(direction, tween(300))
}