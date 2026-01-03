package com.bc230420212.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.ui.components.AppButton
import com.bc230420212.app.ui.components.AppTextField
import com.bc230420212.app.ui.theme.PrimaryColor
import com.bc230420212.app.ui.theme.TextPrimary
import com.bc230420212.app.ui.viewmodel.AuthViewModel

/**
 * LOGIN SCREEN
 * 
 * This screen allows users to sign in to their account.
 * Users can login using:
 * 1. Email and Password
 * 2. Google Sign-In
 * 
 * @param onLoginSuccess - Function called when login is successful (receives user role, navigates accordingly)
 * @param onNavigateToRegister - Function to navigate to registration screen
 * @param onGoogleSignIn - Function to start Google sign-in process
 * @param viewModel - ViewModel that handles authentication logic
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (com.bc230420212.app.data.model.UserRole) -> Unit,
    onNavigateToRegister: () -> Unit,
    onGoogleSignIn: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    // State variables to store user input
    // 'remember' keeps the value even when screen recomposes (redraws)
    // 'mutableStateOf' creates a state that can change
    var email by remember { mutableStateOf("") }  // Stores email input
    var password by remember { mutableStateOf("") }  // Stores password input
    var emailError by remember { mutableStateOf(false) }  // True if email has error
    var passwordError by remember { mutableStateOf(false) }  // True if password has error

    // Get the current authentication state from ViewModel
    // 'collectAsState()' automatically updates UI when state changes
    val uiState by viewModel.uiState.collectAsState()

    // LaunchedEffect runs when the value inside changes
    // This effect watches for successful authentication
    // When user is authenticated, automatically navigate based on role
    LaunchedEffect(uiState.isAuthenticated, uiState.userRole) {
        if (uiState.isAuthenticated) {
            onLoginSuccess(uiState.userRole)  // Navigate based on user role
        }
    }

    // This effect watches for error messages
    // When error occurs, check if it's related to email or password
    // and set the appropriate error flag
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            // Check if error message contains "email" (case-insensitive)
            emailError = uiState.errorMessage!!.contains("email", ignoreCase = true)
            // Check if error message contains "password" (case-insensitive)
            passwordError = uiState.errorMessage!!.contains("password", ignoreCase = true)
        }
    }

    // Column is a layout that arranges items vertically (top to bottom)
    // fillMaxSize() - takes full screen width and height
    // padding(24.dp) - adds 24dp space around all edges
    // verticalScroll - allows scrolling if content is too long
    // CenterHorizontally - centers all items horizontally
    // Center - centers all items vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Title
        Text(
            text = "Disaster Alert",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Stay Safe, Stay Informed",
            fontSize = 16.sp,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Email Field
        AppTextField(
            value = email,
            onValueChange = { 
                email = it
                emailError = false
                viewModel.clearError()
            },
            label = "Email",
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
            isError = emailError,
            errorMessage = if (emailError) uiState.errorMessage ?: "" else "",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Password Field
        AppTextField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = false
                viewModel.clearError()
            },
            label = "Password",
            isPassword = true,
            isError = passwordError,
            errorMessage = if (passwordError) uiState.errorMessage ?: "" else "",
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Error Message
        if (uiState.errorMessage != null && !emailError && !passwordError) {
            Text(
                text = uiState.errorMessage!!,
                color = com.bc230420212.app.ui.theme.ErrorColor,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Login Button
        // When clicked, validates inputs and calls ViewModel to sign in
        AppButton(
            text = "Login",
            onClick = {
                // Validation: Check if email or password is empty
                // If empty, don't do anything (return early)
                if (email.isBlank() || password.isBlank()) {
                    return@AppButton  // Exit function early
                }
                // Call ViewModel function to sign in with Firebase
                // trim() removes spaces from start and end
                viewModel.signInWithEmail(email.trim(), password)
            },
            // Button is enabled only if:
            // 1. Not currently loading (!uiState.isLoading)
            // 2. Email is not empty (email.isNotBlank())
            // 3. Password is not empty (password.isNotBlank())
            enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Google Sign In Button
        AppButton(
            text = "Sign in with Google",
            onClick = onGoogleSignIn,
            enabled = !uiState.isLoading,
            isSecondary = true,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Register Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                fontSize = 14.sp,
                color = TextPrimary
            )
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Register",
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Loading Indicator
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 16.dp),
                color = PrimaryColor
            )
        }
    }
}

