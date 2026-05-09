package com.bc210421297.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.bc210421297.app.data.model.UserRole
import com.bc210421297.app.ui.navigation.NavGraph
import com.bc210421297.app.ui.navigation.Screen
import com.bc210421297.app.ui.theme.AndroidBasedCrowdsourcedDisasterAlertSafetyAppTheme
import com.bc210421297.app.ui.viewmodel.AuthViewModel
import com.bc210421297.app.util.CloudinaryHelper
import com.bc210421297.app.util.GoogleSignInHelper
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
    
    // Notification permission launcher (for Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            android.util.Log.d("MainActivity", "Notification permission granted")
            // Subscribe to notifications after permission is granted
            subscribeToNotifications()
            getFCMToken()
        } else {
            android.util.Log.w("MainActivity", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Cloudinary
        CloudinaryHelper.init(this)
        
        // Request notification permission for Android 13+ (API 33+)
        requestNotificationPermission()
        
        // Get FCM token for debugging
        getFCMToken()
        
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
                        // If admin, go directly to Admin Panel, otherwise Home
                        if (uiState.userRole == UserRole.ADMIN) {
                            Screen.AdminPanel.route
                        } else {
                            Screen.Home.route
                        }
                    } else {
                        Screen.Login.route
                    }
                    
                    NavGraph(
                        navController = navController,
                        onGoogleSignIn = {
                            signInWithGoogle()
                        },
                        startDestination = startDestination
                    )
                }
            }
        }
    }
    
    /**
     * Request notification permission (Android 13+)
     * Only asks if permission is not already granted
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires runtime permission for notifications
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    android.util.Log.d("MainActivity", "Notification permission already granted")
                    subscribeToNotifications()
                }
                else -> {
                    // Request permission
                    android.util.Log.d("MainActivity", "Requesting notification permission")
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android 12 and below don't need runtime permission
            android.util.Log.d("MainActivity", "Android version < 13, no permission needed")
            subscribeToNotifications()
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
                    android.util.Log.d("FCM", "✅ Successfully subscribed to 'allUsers' topic")
                } else {
                    task.exception?.let {
                        android.util.Log.e("FCM", "❌ Failed to subscribe to notifications topic", it)
                    }
                }
            }
    }
    
    /**
     * Get FCM token for debugging
     * This helps verify FCM is working correctly
     */
    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                android.util.Log.e("FCM", "❌ Failed to get FCM token", task.exception)
                return@addOnCompleteListener
            }
            
            // Get new FCM registration token
            val token = task.result
            android.util.Log.d("FCM", "✅ FCM Token: $token")
            android.util.Log.d("FCM", "📱 Use this token to send test notifications from Firebase Console")
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
