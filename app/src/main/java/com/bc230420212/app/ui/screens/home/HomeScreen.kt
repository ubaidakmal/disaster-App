package com.bc230420212.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.ui.components.AppButton
import com.bc230420212.app.ui.components.DashboardCard
import com.bc230420212.app.ui.theme.*
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
 * @param onNavigateToAdminPanel - Navigate to Admin Panel screen (admin only)
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryColor,
                    titleContentColor = TextOnPrimary
                ),
                actions = {
                    // User role badge
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .align(Alignment.CenterVertically),
                        shape = RoundedCornerShape(12.dp),
                        color = TextOnPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = uiState.userRole.name,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = TextOnPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Welcome Section with Gradient
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        GradientStart.copy(alpha = 0.1f),
                                        GradientEnd.copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Text(
                                text = "Welcome Back!",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Stay informed, stay safe",
                                fontSize = 16.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Main Features - 3 in a row
                Text(
                    text = "Quick Actions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                
                // Row 1: Report, View Reports, Map
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardCard(
                        title = "Report",
                        icon = Icons.Default.Warning,
                        onClick = onNavigateToReportDisaster,
                        modifier = Modifier.weight(1f),
                        iconColor = AccentColor
                    )
                    
                    DashboardCard(
                        title = "View Reports",
                        icon = Icons.Default.List,
                        onClick = onNavigateToViewReports,
                        modifier = Modifier.weight(1f),
                        iconColor = PrimaryColor
                    )
                    
                    DashboardCard(
                        title = "Map View",
                        icon = Icons.Default.LocationOn,
                        onClick = onNavigateToMapView,
                        modifier = Modifier.weight(1f),
                        iconColor = SecondaryColor
                    )
                }
                
                // Row 2: SOS, Profile, (empty or admin)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardCard(
                        title = "SOS Emergency",
                        icon = Icons.Default.Notifications,
                        onClick = onNavigateToSOS,
                        modifier = Modifier.weight(1f),
                        iconColor = ErrorColor
                    )
                    
                    DashboardCard(
                        title = "Profile",
                        icon = Icons.Default.Settings,
                        onClick = onNavigateToProfile,
                        modifier = Modifier.weight(1f),
                        iconColor = InfoColor
                    )
                    
                    // Admin Panel Card (Only for ADMIN users)
                    if (uiState.userRole == com.bc230420212.app.data.model.UserRole.ADMIN) {
                        DashboardCard(
                            title = "Admin",
                            icon = Icons.Default.Settings,
                            onClick = onNavigateToAdminPanel,
                            modifier = Modifier.weight(1f),
                            iconColor = WarningColor
                        )
                    } else {
                        // Empty space to maintain layout
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                // Sign Out Button
                Spacer(modifier = Modifier.height(8.dp))
                AppButton(
                    text = "Sign Out",
                    onClick = {
                        viewModel.signOut()
                        onSignOut()
                    },
                    isSecondary = true,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
