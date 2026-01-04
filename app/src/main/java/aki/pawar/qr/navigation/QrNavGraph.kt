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
                    navController.navigate(Screen.Scanner.route)
                },
                onNavigateToGenerator = {
                    navController.navigate(Screen.Generator.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }
        
        composable(Screen.Scanner.route) {
            ScannerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Generator.route) {
            GeneratorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}


