package com.bc230420212.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.ui.components.AppButton
import com.bc230420212.app.ui.components.DashboardCard
import com.bc230420212.app.ui.theme.PrimaryColor
import com.bc230420212.app.ui.theme.TextOnPrimary
import com.bc230420212.app.ui.theme.TextPrimary
import com.bc230420212.app.ui.viewmodel.AuthViewModel

/**
 * HOME SCREEN (DASHBOARD)
 * 
 * This is the main navigation screen after user logs in.
 * It displays buttons/cards for all major features:
 * - Report Disaster
 * - View Reports (List)
 * - Map View
 * - SOS
 * - Profile/Settings
 * 
 * @param onSignOut - Function called when user signs out
 * @param onNavigateToReportDisaster - Navigate to Report Disaster screen
 * @param onNavigateToViewReports - Navigate to View Reports screen
 * @param onNavigateToMapView - Navigate to Map View screen
 * @param onNavigateToSOS - Navigate to SOS screen
 * @param onNavigateToProfile - Navigate to Profile/Settings screen
 * @param onNavigateToAdminPanel - Navigate to Admin Panel screen (Admin only)
 * @param viewModel - ViewModel for authentication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSignOut: () -> Unit,
    onNavigateToReportDisaster: () -> Unit,
    onNavigateToViewReports: () -> Unit,
    onNavigateToMapView: () -> Unit,
    onNavigateToSOS: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdminPanel: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    // Get current authentication state
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Disaster Alert",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryColor,
                    titleContentColor = TextOnPrimary
                ),
                actions = {
                    // User role badge
                    Text(
                        text = uiState.userRole.name,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .align(Alignment.CenterVertically),
                        color = TextOnPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Section
            Text(
                text = "Welcome!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Stay safe, stay informed",
                fontSize = 14.sp,
                color = com.bc230420212.app.ui.theme.TextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Dashboard Cards - Main Features
            // Report Disaster Card
            DashboardCard(
                title = "Report Disaster",
                icon = Icons.Default.Warning,
                onClick = onNavigateToReportDisaster,
                modifier = Modifier.fillMaxWidth()
            )

            // View Reports (List) Card
            DashboardCard(
                title = "View Reports",
                icon = Icons.Default.List,
                onClick = onNavigateToViewReports,
                modifier = Modifier.fillMaxWidth()
            )

            // Map View Card
            DashboardCard(
                title = "Map View",
                icon = Icons.Default.LocationOn,
                onClick = onNavigateToMapView,
                modifier = Modifier.fillMaxWidth()
            )

            // SOS Card (Emergency)
            DashboardCard(
                title = "SOS Emergency",
                icon = Icons.Default.Notifications,
                onClick = onNavigateToSOS,
                modifier = Modifier.fillMaxWidth()
            )

            // Profile/Settings Card
            DashboardCard(
                title = "Profile & Settings",
                icon = Icons.Default.Settings,
                onClick = onNavigateToProfile,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Admin Panel (only for ADMIN users)
            if (uiState.userRole == com.bc230420212.app.data.model.UserRole.ADMIN) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = com.bc230420212.app.ui.theme.AccentColor.copy(alpha = 0.1f)
                    ),
                    onClick = onNavigateToAdminPanel
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Admin Panel",
                                tint = com.bc230420212.app.ui.theme.AccentColor,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Admin Panel",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = com.bc230420212.app.ui.theme.TextPrimary
                                )
                                Text(
                                    text = "Manage and verify reports",
                                    fontSize = 14.sp,
                                    color = com.bc230420212.app.ui.theme.TextSecondary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Navigate",
                            tint = com.bc230420212.app.ui.theme.TextSecondary
                        )
                    }
                }
            }

            // Sign Out Button
            Spacer(modifier = Modifier.height(16.dp))
            AppButton(
                text = "Sign Out",
                onClick = {
                    viewModel.signOut()
                    onSignOut()
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
