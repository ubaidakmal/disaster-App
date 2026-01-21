package com.bc230420212.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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

@Composable
fun RegisterScreen(
    onRegisterSuccess: (com.bc230420212.app.data.model.UserRole) -> Unit,
    onNavigateToLogin: () -> Unit,
    onGoogleSignIn: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isAuthenticated, uiState.userRole) {
        if (uiState.isAuthenticated) {
            onRegisterSuccess(uiState.userRole)  // Navigate based on user role
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            emailError = uiState.errorMessage!!.contains("email", ignoreCase = true)
            passwordError = uiState.errorMessage!!.contains("password", ignoreCase = true) ||
                           uiState.errorMessage!!.contains("weak", ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Logo/Icon Section with Gradient
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Register Icon",
                    modifier = Modifier.size(48.dp),
                    tint = TextOnPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App Title
            Text(
                text = "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Join our safety community",
                fontSize = 16.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Name Field
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AppTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        nameError = false
                        viewModel.clearError()
                    },
                    label = "Full Name",
                    isError = nameError,
                    errorMessage = if (nameError) "Name is required" else "",
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Email Field
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AppTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        emailError = false
                        viewModel.clearError()
                    },
                    label = "Email Address",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                    isError = emailError,
                    errorMessage = if (emailError) uiState.errorMessage ?: "" else "",
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Password Field
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AppTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        passwordError = false
                        confirmPasswordError = false
                        viewModel.clearError()
                    },
                    label = "Password",
                    isPassword = true,
                    isError = passwordError,
                    errorMessage = if (passwordError) uiState.errorMessage ?: "" else "",
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Confirm Password Field
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                AppTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        confirmPasswordError = false
                        viewModel.clearError()
                    },
                    label = "Confirm Password",
                    isPassword = true,
                    isError = confirmPasswordError,
                    errorMessage = if (confirmPasswordError) "Passwords do not match" else "",
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Error Message
            if (uiState.errorMessage != null && !emailError && !passwordError) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorColor.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = ErrorColor,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Register Button
            AppButton(
                text = "Create Account",
                onClick = {
                    // Validation
                    nameError = name.isBlank()
                    emailError = email.isBlank()
                    passwordError = password.isBlank() || password.length < 6
                    confirmPasswordError = confirmPassword != password

                    if (!nameError && !emailError && !passwordError && !confirmPasswordError) {
                        viewModel.signUpWithEmail(email.trim(), password, name.trim())
                    }
                },
                enabled = !uiState.isLoading && name.isNotBlank() && email.isNotBlank() && 
                         password.isNotBlank() && confirmPassword.isNotBlank(),
                modifier = Modifier.padding(bottom = 24.dp)
            )

        // Google Sign In Button
//        AppButton(
//            text = "Sign in with Google",
//            onClick = onGoogleSignIn,
//            enabled = !uiState.isLoading,
//            isSecondary = true,
//            modifier = Modifier.padding(bottom = 24.dp)
//        )

            // Login Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Sign In",
                        color = PrimaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Loading Indicator
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(
                    color = PrimaryColor,
                    strokeWidth = 3.dp
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

