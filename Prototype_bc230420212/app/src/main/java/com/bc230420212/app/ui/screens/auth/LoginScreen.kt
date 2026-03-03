package com.bc230420212.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.ui.components.AppButton
import com.bc230420212.app.ui.components.AppTextField
import com.bc230420212.app.ui.theme.*
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryColor.copy(alpha = 0.05f),
                        BackgroundColor,
                        SurfaceColor
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Logo/Icon Section with Circular Gradient
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GradientStart,
                                GradientEnd,
                                PrimaryColor
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Safety Icon",
                    modifier = Modifier.size(56.dp),
                    tint = TextOnPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // App Title with different styling
            Text(
                text = "Safety First",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryColor,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Access your disaster alert account",
                fontSize = 15.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 56.dp)
            )

            // Email Field with new design
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceColor,
                shadowElevation = 4.dp
            ) {
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
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Password Field with new design
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceColor,
                shadowElevation = 4.dp
            ) {
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
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Error Message
            if (uiState.errorMessage != null && !emailError && !passwordError) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = ErrorColor.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = ErrorColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(18.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Login Button with gradient
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        return@Button
                    }
                    viewModel.signInWithEmail(email.trim(), password)
                },
                enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(bottom = 28.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = "Continue",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnPrimary
                )
            }

        // Google Sign In Button
//        AppButton(
//            text = "Sign in with Google",
//            onClick = onGoogleSignIn,
//            enabled = !uiState.isLoading,
//            isSecondary = true,
//            modifier = Modifier.padding(bottom = 24.dp)
//        )

            // Register Link with new styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 15.sp,
                    color = TextSecondary
                )
                TextButton(
                    onClick = onNavigateToRegister,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = PrimaryColor
                    )
                ) {
                    Text(
                        text = "Sign Up",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // Loading Indicator
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(28.dp))
                CircularProgressIndicator(
                    color = PrimaryColor,
                    strokeWidth = 3.5.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

