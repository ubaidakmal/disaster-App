package com.bc230420212.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.bc230420212.app.data.model.UserRole
import com.bc230420212.app.ui.navigation.NavGraph
import com.bc230420212.app.ui.navigation.Screen
import com.bc230420212.app.ui.theme.AndroidBasedCrowdsourcedDisasterAlertSafetyAppTheme
import com.bc230420212.app.ui.viewmodel.AuthViewModel
import com.bc230420212.app.util.GoogleSignInHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val googleSignInClient by lazy { GoogleSignInHelper.getGoogleSignInClient(this) }
    
    // Activity Result Launcher for Google Sign-In (modern way, replaces deprecated startActivityForResult)
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // This is called when Google Sign-In activity returns
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                lifecycleScope.launch {
                    authViewModel.signInWithGoogle(idToken)
                }
            }
        } catch (e: ApiException) {
            // Handle error
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Subscribe to FCM topic for receiving notifications
        subscribeToNotifications()
        
        setContent {
            AndroidBasedCrowdsourcedDisasterAlertSafetyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val uiState by authViewModel.uiState.collectAsState()
                    
                    // Determine start destination based on auth state and user role
                    val startDestination = if (uiState.isAuthenticated) {
                        // If user is admin, show Admin Panel; otherwise show Home Screen
                        if (uiState.userRole == UserRole.ADMIN) {
                            Screen.AdminPanel.route
                        } else {
                            Screen.Home.route
                        }
                    } else {
                        Screen.Login.route
                    }
                    
                    // Navigate to correct screen when role changes (e.g., after async role load)
                    // This ensures admins are redirected to Admin Panel even if they initially land on Home
                    LaunchedEffect(uiState.isAuthenticated, uiState.userRole) {
                        if (uiState.isAuthenticated) {
                            // Small delay to ensure navigation is ready
                            kotlinx.coroutines.delay(100)
                            
                            val currentRoute = navController.currentDestination?.route
                            val targetRoute = if (uiState.userRole == UserRole.ADMIN) {
                                Screen.AdminPanel.route
                            } else {
                                Screen.Home.route
                            }
                            
                            // Navigate if we're on the wrong screen
                            // Allow navigation from Login/Register, but also from Home if user is admin
                            if (currentRoute != targetRoute) {
                                // If admin is on Home screen, redirect to Admin Panel
                                if (uiState.userRole == UserRole.ADMIN && currentRoute == Screen.Home.route) {
                                    navController.navigate(Screen.AdminPanel.route) {
                                        // Replace Home with Admin Panel
                                        popUpTo(Screen.Home.route) { inclusive = true }
                                    }
                                } else if (currentRoute == Screen.Login.route || currentRoute == Screen.Register.route) {
                                    // Normal navigation from login/register
                                    navController.navigate(targetRoute) {
                                        popUpTo(if (currentRoute == Screen.Login.route) Screen.Login.route else Screen.Register.route) { 
                                            inclusive = true 
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    NavGraph(
                        navController = navController,
                        onGoogleSignIn = {
                            signInWithGoogle()
                        },
                        startDestination = startDestination,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
    
    /**
     * Subscribe to FCM topic to receive push notifications
     * All users subscribe to "allUsers" topic to receive notifications about new reports
     */
    private fun subscribeToNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("allUsers")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    android.util.Log.d("FCM", "Subscribed to notifications topic")
                } else {
                    task.exception?.let {
                        android.util.Log.e("FCM", "Failed to subscribe to notifications topic", it)
                    }
                }
            }
    }

    /**
     * Starts Google Sign-In process
     * Uses modern Activity Result API instead of deprecated startActivityForResult
     */
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}
