package com.bc230420212.app.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.bc230420212.app.data.model.DisasterReport
import com.bc230420212.app.data.model.ReportStatus
import com.bc230420212.app.ui.components.AppButton
import com.bc230420212.app.ui.theme.*
import com.bc230420212.app.ui.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * ADMIN PANEL SCREEN
 * 
 * This screen is only accessible to users with ADMIN role.
 * Admins can:
 * - View all pending reports (ACTIVE status)
 * - Update report status to VERIFIED, RESOLVED, or FALSE_ALARM
 * - See report details before making a decision
 * 
 * @param onNavigateBack - Function to navigate back
 * @param onSignOut - Function to sign out
 * @param viewModel - ViewModel for admin operations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Admin Panel",
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
                ),
                actions = {
                    // Sign Out Button
                    TextButton(onClick = {
                        onSignOut()
                    }) {
                        Text(
                            text = "Sign Out",
                            color = TextOnPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = InfoColor.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Pending Reports",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${uiState.pendingReports.size} reports awaiting review",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Error Message
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorColor.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = error,
                        color = ErrorColor,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Loading Indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.pendingReports.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No Pending Reports",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Text(
                            text = "All reports have been reviewed",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                // Reports List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.pendingReports) { report ->
                        AdminReportCard(
                            report = report,
                            onUpdateStatus = { status ->
                                viewModel.updateReportStatus(report.id, status)
                            },
                            isUpdating = uiState.isUpdatingStatus
                        )
                    }
                }
            }
        }
    }
}

/**
 * Admin Report Card
 * 
 * Displays a report with action buttons for updating status
 */
@Composable
private fun AdminReportCard(
    report: DisasterReport,
    onUpdateStatus: (ReportStatus) -> Unit,
    isUpdating: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Report Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Disaster Type Badge
                Surface(
                    color = when (report.disasterType) {
                        com.bc230420212.app.data.model.DisasterType.FLOOD -> FloodColor
                        com.bc230420212.app.data.model.DisasterType.FIRE -> FireColor
                        com.bc230420212.app.data.model.DisasterType.EARTHQUAKE -> EarthquakeColor
                        com.bc230420212.app.data.model.DisasterType.ACCIDENT -> AccidentColor
                        com.bc230420212.app.data.model.DisasterType.STORM -> OtherDisasterColor
                        com.bc230420212.app.data.model.DisasterType.LANDSLIDE -> OtherDisasterColor
                        else -> OtherDisasterColor
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = report.disasterType.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                // Status Badge
                Surface(
                    color = WarningColor,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "PENDING",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Description
            Text(
                text = report.description,
                fontSize = 14.sp,
                color = TextPrimary,
                maxLines = 3
            )
            
            // Location and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = report.address.ifEmpty { 
                        "${String.format("%.4f", report.latitude)}, ${String.format("%.4f", report.longitude)}" 
                    },
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        .format(Date(report.timestamp)),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            
            // Verification Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "✓ ${report.confirmations} confirmations",
                    fontSize = 12.sp,
                    color = SuccessColor
                )
                Text(
                    text = "✗ ${report.dismissals} dismissals",
                    fontSize = 12.sp,
                    color = ErrorColor
                )
            }
            
            HorizontalDivider()
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Verify Button
                AppButton(
                    text = "Verify",
                    onClick = { onUpdateStatus(ReportStatus.VERIFIED) },
                    modifier = Modifier.weight(1f),
                    isSecondary = false,
                    enabled = !isUpdating
                )
                
                // Resolve Button
                AppButton(
                    text = "Resolve",
                    onClick = { onUpdateStatus(ReportStatus.RESOLVED) },
                    modifier = Modifier.weight(1f),
                    isSecondary = true,
                    enabled = !isUpdating
                )
                
                // False Alarm Button
                AppButton(
                    text = "False Alarm",
                    onClick = { onUpdateStatus(ReportStatus.FALSE_ALARM) },
                    modifier = Modifier.weight(1f),
                    isSecondary = true,
                    enabled = !isUpdating
                )
            }
        }
    }
}

