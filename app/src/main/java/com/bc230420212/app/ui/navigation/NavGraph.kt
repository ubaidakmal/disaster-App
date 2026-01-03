package com.bc230420212.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bc230420212.app.ui.screens.auth.LoginScreen
import com.bc230420212.app.ui.screens.auth.RegisterScreen
import com.bc230420212.app.ui.screens.home.HomeScreen
import com.bc230420212.app.ui.screens.map.MapViewScreen
import com.bc230420212.app.ui.screens.profile.ProfileScreen
import com.bc230420212.app.ui.screens.report.ReportDisasterScreen
import com.bc230420212.app.ui.screens.admin.AdminPanelScreen
import com.bc230420212.app.ui.screens.reports.ReportDetailsScreen
import com.bc230420212.app.ui.screens.reports.ViewReportsScreen
import com.bc230420212.app.ui.screens.sos.SOSScreen

/**
 * NAVIGATION GRAPH
 * 
 * This file defines all the screens/routes in the app and how to navigate between them.
 * Each screen has a route (like a URL) that can be used to navigate to it.
 */
sealed class Screen(val route: String) {
    // Authentication Screens
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Main Screens
    object Home : Screen("home")
    object ReportDisaster : Screen("report_disaster")
    object ViewReports : Screen("view_reports")
    object ReportDetails : Screen("report_details/{reportId}") {
        fun createRoute(reportId: String) = "report_details/$reportId"
    }
    object MapView : Screen("map_view")
    object SOS : Screen("sos")
    object Profile : Screen("profile")
    object AdminPanel : Screen("admin_panel")
}

/**
 * Main Navigation Graph
 * 
 * This function sets up all the navigation routes in the app.
 * When a screen wants to navigate to another, it uses the route name.
 * 
 * @param navController - Controls navigation between screens
 * @param onGoogleSignIn - Function to handle Google sign-in
 * @param startDestination - Which screen to show first (default: Login)
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    onGoogleSignIn: () -> Unit,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication Screens
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onGoogleSignIn = onGoogleSignIn
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onGoogleSignIn = onGoogleSignIn
            )
        }
        
        // Main Dashboard (Home Screen)
        composable(Screen.Home.route) {
            HomeScreen(
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToReportDisaster = {
                    navController.navigate(Screen.ReportDisaster.route)
                },
                onNavigateToViewReports = {
                    navController.navigate(Screen.ViewReports.route)
                },
                onNavigateToMapView = {
                    navController.navigate(Screen.MapView.route)
                },
                onNavigateToSOS = {
                    navController.navigate(Screen.SOS.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToAdminPanel = {
                    navController.navigate(Screen.AdminPanel.route)
                }
            )
        }
        
        // Report Disaster Screen
        composable(Screen.ReportDisaster.route) {
            ReportDisasterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // View Reports Screen
        composable(Screen.ViewReports.route) {
            ViewReportsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetails = { reportId ->
                    navController.navigate(Screen.ReportDetails.createRoute(reportId))
                }
            )
        }
        
        // Report Details Screen
        composable(
            route = Screen.ReportDetails.route,
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            ReportDetailsScreen(
                reportId = reportId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Map View Screen
        composable(Screen.MapView.route) {
            MapViewScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // SOS Screen
        composable(Screen.SOS.route) {
            SOSScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Profile Screen
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Admin Panel Screen
        composable(Screen.AdminPanel.route) {
            AdminPanelScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
