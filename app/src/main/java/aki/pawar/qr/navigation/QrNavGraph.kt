package aki.pawar.qr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import aki.pawar.qr.presentation.generator.GeneratorScreen
import aki.pawar.qr.presentation.history.HistoryScreen
import aki.pawar.qr.presentation.home.HomeScreen
import aki.pawar.qr.presentation.scanner.ScannerScreen

/**
 * Navigation destinations for the app
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Scanner : Screen("scanner")
    data object Generator : Screen("generator")
    data object History : Screen("history")
}

/**
 * Safe navigation extension to prevent multiple clicks causing issues
 */
private fun NavHostController.safeNavigateBack() {
    if (currentBackStackEntry != null && previousBackStackEntry != null) {
        popBackStack()
    }
}

/**
 * Safe navigation to home - clears back stack and goes to home
 */
private fun NavHostController.safeNavigateToHome() {
    navigate(Screen.Home.route) {
        popUpTo(Screen.Home.route) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

/**
 * Safe navigation to a screen - prevents duplicate entries
 */
private fun NavHostController.safeNavigate(route: String) {
    val currentRoute = currentBackStackEntry?.destination?.route
    if (currentRoute != route) {
        navigate(route) {
            launchSingleTop = true
        }
    }
}

/**
 * Main navigation graph for the QR app
 */
@Composable
fun QrNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToScanner = {
                    navController.safeNavigate(Screen.Scanner.route)
                },
                onNavigateToGenerator = {
                    navController.safeNavigate(Screen.Generator.route)
                },
                onNavigateToHistory = {
                    navController.safeNavigate(Screen.History.route)
                }
            )
        }
        
        composable(Screen.Scanner.route) {
            ScannerScreen(
                onNavigateBack = {
                    navController.safeNavigateToHome()
                }
            )
        }
        
        composable(Screen.Generator.route) {
            GeneratorScreen(
                onNavigateBack = {
                    navController.safeNavigateToHome()
                }
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = {
                    navController.safeNavigateToHome()
                }
            )
        }
    }
}


