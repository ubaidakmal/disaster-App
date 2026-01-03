package com.bc230420212.app.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ExitToApp
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
import com.bc230420212.app.data.model.DisasterType
import com.bc230420212.app.data.model.ReportStatus
import com.bc230420212.app.ui.components.ReportItem
import com.bc230420212.app.ui.theme.*
import com.bc230420212.app.ui.theme.AccidentColor
import com.bc230420212.app.ui.theme.EarthquakeColor
import com.bc230420212.app.ui.theme.FireColor
import com.bc230420212.app.ui.theme.FloodColor
import com.bc230420212.app.ui.theme.OtherDisasterColor
import com.bc230420212.app.ui.viewmodel.AdminViewModel
import com.bc230420212.app.ui.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ADMIN PANEL SCREEN
 * 
 * This screen is only accessible to ADMIN users.
 * It allows admins to:
 * - View pending reports (ACTIVE status)
 * - Update report status (VERIFIED, RESOLVED, FALSE_ALARM)
 * - Manage disaster reports
 * 
 * @param onNavigateBack - Function to navigate back to home
 * @param onSignOut - Function to sign out and navigate to login
 * @param viewModel - ViewModel for admin operations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit = {},
    viewModel: AdminViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
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
                actions = {
                    IconButton(
                        onClick = {
                            authViewModel.signOut()
                            onSignOut()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = TextOnPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AccentColor,
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
        ) {
            // Success/Error Messages
            if (uiState.successMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessColor.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = uiState.successMessage!!,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SuccessColor,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            if (uiState.errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorColor.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = ErrorColor,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = ErrorColor
                            )
                        }
                    }
                }
            }
            
            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AccentColor.copy(alpha = 0.1f)
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
                        text = "Review and update report status",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Total: ${uiState.pendingReports.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentColor,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            // Reports List
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AccentColor)
                    }
                }
                
                uiState.pendingReports.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "No Pending",
                                tint = SuccessColor,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Pending Reports",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "All reports have been reviewed",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.pendingReports) { report ->
                            AdminReportCard(
                                report = report,
                                onUpdateStatus = { status ->
                                    viewModel.updateReportStatus(report.id, status)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Admin Report Card with Status Update Buttons
 */
@Composable
private fun AdminReportCard(
    report: DisasterReport,
    onUpdateStatus: (ReportStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Report Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = report.disasterType.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = getDisasterTypeColor(report.disasterType)
                )
                Surface(
                    color = WarningColor,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = report.status.name,
                        fontSize = 12.sp,
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
            
            // Report Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTimestamp(report.timestamp),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "✓ ${report.confirmations}",
                        fontSize = 12.sp,
                        color = SuccessColor
                    )
                    Text(
                        text = "✗ ${report.dismissals}",
                        fontSize = 12.sp,
                        color = ErrorColor
                    )
                }
            }
            
            HorizontalDivider()
            
            // Status Update Buttons
            Text(
                text = "Update Status:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Verified Button
                Button(
                    onClick = { onUpdateStatus(ReportStatus.VERIFIED) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verify",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verify", fontSize = 12.sp)
                }
                
                // Resolved Button
                Button(
                    onClick = { onUpdateStatus(ReportStatus.RESOLVED) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Resolve",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Resolve", fontSize = 12.sp)
                }
                
                // False Alarm Button
                Button(
                    onClick = { onUpdateStatus(ReportStatus.FALSE_ALARM) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "False Alarm",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("False", fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * Get color for disaster type
 */
@Composable
private fun getDisasterTypeColor(type: DisasterType): androidx.compose.ui.graphics.Color {
    return when (type) {
        DisasterType.FLOOD -> FloodColor
        DisasterType.FIRE -> FireColor
        DisasterType.EARTHQUAKE -> EarthquakeColor
        DisasterType.ACCIDENT -> AccidentColor
        else -> OtherDisasterColor
    }
}

/**
 * Format timestamp to readable date/time
 */
private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}

