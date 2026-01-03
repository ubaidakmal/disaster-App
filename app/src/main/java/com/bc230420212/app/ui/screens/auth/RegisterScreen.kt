package com.bc230420212.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.ui.components.AppButton
import com.bc230420212.app.ui.components.AppTextField
import com.bc230420212.app.ui.theme.PrimaryColor
import com.bc230420212.app.ui.theme.TextPrimary
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
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Join our safety community",
            fontSize = 16.sp,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Name Field
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
            modifier = Modifier.padding(bottom = 16.dp)
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
                confirmPasswordError = false
                viewModel.clearError()
            },
            label = "Password",
            isPassword = true,
            isError = passwordError,
            errorMessage = if (passwordError) uiState.errorMessage ?: "" else "",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Confirm Password Field
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

        // Register Button
        AppButton(
            text = "Register",
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

        // Login Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                fontSize = 14.sp,
                color = TextPrimary
            )
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Login",
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

