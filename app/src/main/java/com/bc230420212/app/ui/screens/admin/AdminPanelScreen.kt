package com.bc230420212.app.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.data.model.ReportStatus
import com.bc230420212.app.ui.components.ReportItem
import com.bc230420212.app.ui.theme.*
import com.bc230420212.app.ui.viewmodel.ReportsViewModel
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
 * @param viewModel - ViewModel for managing reports
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReportsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedReportId by remember { mutableStateOf<String?>(null) }
    var showStatusDialog by remember { mutableStateOf(false) }
    
    // Load pending reports when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadPendingReports()
    }
    
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
            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AccentColor.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = "Pending Reports",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Update status for disaster reports",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
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
                
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = uiState.errorMessage!!,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        com.bc230420212.app.ui.components.AppButton(
                            text = "Retry",
                            onClick = { viewModel.loadPendingReports() }
                        )
                    }
                }
                
                uiState.reports.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Pending Reports",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "All reports have been processed.",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                
                else -> {
                    // List of pending reports
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.reports) { report ->
                            AdminReportItem(
                                report = report,
                                onUpdateStatus = { reportId ->
                                    selectedReportId = reportId
                                    showStatusDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Status Update Dialog
    if (showStatusDialog && selectedReportId != null) {
        StatusUpdateDialog(
            reportId = selectedReportId!!,
            onDismiss = {
                showStatusDialog = false
                selectedReportId = null
            },
            onStatusSelected = { status ->
                viewModel.updateReportStatus(selectedReportId!!, status)
                showStatusDialog = false
                selectedReportId = null
            }
        )
    }
}

/**
 * Admin Report Item with Update Status button
 */
@Composable
private fun AdminReportItem(
    report: com.bc230420212.app.data.model.DisasterReport,
    onUpdateStatus: (String) -> Unit
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
            // Report Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = report.disasterType.displayName,
                    fontSize = 16.sp,
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
            
            Text(
                text = report.description,
                fontSize = 14.sp,
                color = TextPrimary,
                maxLines = 2
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTimestamp(report.timestamp),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "✓ ${report.confirmations}",
                        fontSize = 12.sp,
                        color = SuccessColor,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "✗ ${report.dismissals}",
                        fontSize = 12.sp,
                        color = ErrorColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            HorizontalDivider()
            
            // Update Status Button
            com.bc230420212.app.ui.components.AppButton(
                text = "Update Status",
                onClick = { onUpdateStatus(report.id) },
                modifier = Modifier.fillMaxWidth(),
                isSecondary = false
            )
        }
    }
}

/**
 * Status Update Dialog
 */
@Composable
private fun StatusUpdateDialog(
    reportId: String,
    onDismiss: () -> Unit,
    onStatusSelected: (ReportStatus) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "Update Report Status",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select new status for this report:",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // VERIFIED option
                StatusOption(
                    status = ReportStatus.VERIFIED,
                    description = "Verify the report as accurate",
                    onClick = { onStatusSelected(ReportStatus.VERIFIED) }
                )
                
                // RESOLVED option
                StatusOption(
                    status = ReportStatus.RESOLVED,
                    description = "Mark report as resolved",
                    onClick = { onStatusSelected(ReportStatus.RESOLVED) }
                )
                
                // FALSE_ALARM option
                StatusOption(
                    status = ReportStatus.FALSE_ALARM,
                    description = "Mark as false alarm",
                    onClick = { onStatusSelected(ReportStatus.FALSE_ALARM) }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Status Option Button
 */
@Composable
private fun StatusOption(
    status: ReportStatus,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = getStatusColor(status).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = status.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = getStatusColor(status)
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Get color for disaster type
 */
@Composable
private fun getDisasterTypeColor(type: com.bc230420212.app.data.model.DisasterType): androidx.compose.ui.graphics.Color = when (type) {
    com.bc230420212.app.data.model.DisasterType.FLOOD -> FloodColor
    com.bc230420212.app.data.model.DisasterType.FIRE -> FireColor
    com.bc230420212.app.data.model.DisasterType.EARTHQUAKE -> EarthquakeColor
    com.bc230420212.app.data.model.DisasterType.ACCIDENT -> AccidentColor
    else -> OtherDisasterColor
}

/**
 * Get color for report status
 */
@Composable
private fun getStatusColor(status: ReportStatus): androidx.compose.ui.graphics.Color = when (status) {
    ReportStatus.ACTIVE -> WarningColor
    ReportStatus.VERIFIED -> SuccessColor
    ReportStatus.RESOLVED -> SuccessColor
    ReportStatus.FALSE_ALARM -> ErrorColor
}

/**
 * Format timestamp to readable date/time
 */
private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}

