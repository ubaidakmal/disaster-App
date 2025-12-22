package com.bc230420212.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.ui.components.AppButton
import com.bc230420212.app.ui.components.SettingsItem
import com.bc230420212.app.ui.theme.*
import com.bc230420212.app.ui.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * PROFILE & SETTINGS SCREEN
 * 
 * This screen displays user profile information and app settings.
 * Users can:
 * - View their profile information (name, email, role)
 * - Change password
 * - Manage notification settings
 * - View app information
 * - Sign out
 * 
 * @param onNavigateBack - Function to navigate back to home
 * @param onSignOut - Function to sign out
 * @param viewModel - ViewModel for authentication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser
    
    // Settings state
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Profile & Settings",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryColor,
                    titleContentColor = TextOnPrimary,
                    navigationIconContentColor = TextOnPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryColor.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Picture Placeholder
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.displayName?.take(1)?.uppercase() ?: "U",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnPrimary
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // User Info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.displayName ?: "User",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = currentUser?.email ?: "",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        // Role Badge
                        Surface(
                            color = if (uiState.userRole == com.bc230420212.app.data.model.UserRole.ADMIN) 
                                AccentColor 
                            else 
                                SuccessColor,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = uiState.userRole.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Account Settings Section
            Text(
                text = "Account",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            SettingsItem(
                title = "Change Password",
                subtitle = "Update your account password",
                icon = Icons.Default.Lock,
                onClick = { showChangePasswordDialog = true }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // App Settings Section
            Text(
                text = "Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            SettingsItem(
                title = "Notifications",
                subtitle = "Enable push notifications for new reports",
                icon = Icons.Default.Notifications,
                onClick = { notificationsEnabled = !notificationsEnabled },
                trailingContent = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            )
            
            SettingsItem(
                title = "Privacy Policy",
                subtitle = "View our privacy policy",
                icon = Icons.Default.Info,
                onClick = {
                    // TODO: Open privacy policy
                }
            )
            
            SettingsItem(
                title = "Terms of Service",
                subtitle = "View terms and conditions",
                icon = Icons.Default.Info,
                onClick = {
                    // TODO: Open terms of service
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // About Section
            Text(
                text = "About",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            SettingsItem(
                title = "App Version",
                subtitle = "Version 1.0.0",
                icon = Icons.Default.Info,
                onClick = { showAboutDialog = true }
            )
            
            SettingsItem(
                title = "Help & Support",
                subtitle = "Get help and contact support",
                icon = Icons.Default.Info,
                onClick = {
                    // TODO: Open help screen
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sign Out Button
            AppButton(
                text = "Sign Out",
                onClick = {
                    viewModel.signOut()
                    onSignOut()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                isSecondary = false
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Change Password Dialog
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { oldPassword, newPassword ->
                viewModel.changePassword(oldPassword, newPassword)
                showChangePasswordDialog = false
            },
            errorMessage = uiState.errorMessage
        )
    }
    
    // About Dialog
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false }
        )
    }
}

/**
 * Change Password Dialog
 */
@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    errorMessage: String? = null
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localErrorMessage by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                com.bc230420212.app.ui.components.AppTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = "Current Password",
                    isPassword = true
                )
                
                com.bc230420212.app.ui.components.AppTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "New Password",
                    isPassword = true
                )
                
                com.bc230420212.app.ui.components.AppTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm New Password",
                    isPassword = true
                )
                
                (errorMessage ?: localErrorMessage)?.let {
                    Text(
                        text = it,
                        color = ErrorColor,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newPassword != confirmPassword) {
                        localErrorMessage = "Passwords do not match"
                    } else if (newPassword.length < 6) {
                        localErrorMessage = "Password must be at least 6 characters"
                    } else {
                        localErrorMessage = null
                        onConfirm(oldPassword, newPassword)
                    }
                }
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * About Dialog
 */
@Composable
private fun AboutDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("About Disaster Alert", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Version: 1.0.0", fontWeight = FontWeight.Medium)
                Text("A crowdsourced disaster alert and safety application.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Features:", fontWeight = FontWeight.Bold)
                Text("• Real-time disaster reporting")
                Text("• Location-based alerts")
                Text("• Community verification")
                Text("• Emergency SOS functionality")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
