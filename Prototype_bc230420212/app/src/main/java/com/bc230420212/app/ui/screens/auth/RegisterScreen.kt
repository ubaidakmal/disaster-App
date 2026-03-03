package com.bc230420212.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
                        SecondaryColor.copy(alpha = 0.06f),
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
            Spacer(modifier = Modifier.height(16.dp))
            
            // Logo/Icon Section with Circular Gradient
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                SecondaryColor,
                                GradientAccent,
                                PrimaryColor
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Register Icon",
                    modifier = Modifier.size(56.dp),
                    tint = TextOnPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // App Title
            Text(
                text = "Get Started",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SecondaryColor,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Create your account to stay safe",
                fontSize = 15.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 52.dp)
            )

            // Name Field
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceColor,
                shadowElevation = 4.dp
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
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Email Field
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
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

            // Password Field
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceColor,
                shadowElevation = 4.dp
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
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Confirm Password Field
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(18.dp),
                color = SurfaceColor,
                shadowElevation = 4.dp
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
                    border = BorderStroke(1.dp, ErrorColor.copy(alpha = 0.3f))
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

            // Register Button with gradient
            Button(
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(bottom = 28.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryColor
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text = "Register Now",
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

            // Login Link with new styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already registered? ",
                    fontSize = 15.sp,
                    color = TextSecondary
                )
                TextButton(
                    onClick = onNavigateToLogin,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = SecondaryColor
                    )
                ) {
                    Text(
                        text = "Sign In",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // Loading Indicator
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(28.dp))
                CircularProgressIndicator(
                    color = SecondaryColor,
                    strokeWidth = 3.5.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

