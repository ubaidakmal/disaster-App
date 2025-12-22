package com.bc230420212.app.ui.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.data.model.ReportStatus
import com.bc230420212.app.ui.components.AppButton
import com.bc230420212.app.ui.theme.*
import com.bc230420212.app.ui.viewmodel.ReportsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * REPORT DETAILS SCREEN
 * 
 * This screen shows full details of a disaster report.
 * Displays:
 * - Full report details
 * - Map preview or address
 * - Media preview if exists
 * - Confirm and Dismiss buttons for users
 * 
 * @param reportId - ID of the report to display
 * @param onNavigateBack - Function to navigate back
 * @param viewModel - ViewModel for managing report state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailsScreen(
    reportId: String,
    onNavigateBack: () -> Unit,
    viewModel: ReportsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Load report when screen opens
    LaunchedEffect(reportId) {
        viewModel.loadReportById(reportId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Report Details",
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
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            }
            
            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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
                    AppButton(
                        text = "Go Back",
                        onClick = onNavigateBack
                    )
                }
            }
            
            uiState.selectedReport == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Report not found",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            }
            
            else -> {
                val report = uiState.selectedReport!!
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Disaster Type and Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = report.disasterType.displayName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = getDisasterTypeColor(report.disasterType)
                        )
                        
                        // Status Badge
                        Surface(
                            color = getStatusColor(report.status),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = report.status.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // Description Section
                    Text(
                        text = "Description",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = report.description,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        lineHeight = 20.sp
                    )
                    
                    HorizontalDivider()
                    
                    // Location Section
                    Text(
                        text = "Location",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceColor
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = PrimaryColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    if (report.address.isNotEmpty()) {
                                        Text(
                                            text = report.address,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary
                                        )
                                    }
                                    Text(
                                        text = "Lat: ${String.format("%.6f", report.latitude)}, Lng: ${String.format("%.6f", report.longitude)}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            
                            // Map Preview Placeholder
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                color = BackgroundColor,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = "Map",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Map Preview",
                                            fontSize = 14.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "Google Maps integration will be added here",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // Media Section (if exists)
                    if (report.mediaUrls.isNotEmpty()) {
                        Text(
                            text = "Media",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceColor
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📷 ${report.mediaUrls.size} media file(s)",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Media preview will be implemented here",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                        
                        HorizontalDivider()
                    }
                    
                    // Report Information
                    Text(
                        text = "Report Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    InfoRow("Time", formatTimestamp(report.timestamp))
                    InfoRow("Confirmations", "${report.confirmations}")
                    InfoRow("Dismissals", "${report.dismissals}")
                    
                    HorizontalDivider()
                    
                    // Show error message if exists
                    if (uiState.errorMessage != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorColor.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = uiState.errorMessage!!,
                                fontSize = 14.sp,
                                color = ErrorColor,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        // Auto-clear error after 3 seconds
                        LaunchedEffect(uiState.errorMessage) {
                            kotlinx.coroutines.delay(3000)
                            viewModel.clearError()
                        }
                    }
                    
                    // Verification Buttons (only for active reports)
                    if (report.status == ReportStatus.ACTIVE) {
                        Text(
                            text = "Verification",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // Show voting status if user has already voted
                        if (uiState.hasUserConfirmed || uiState.hasUserDismissed) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (uiState.hasUserConfirmed) 
                                        SuccessColor.copy(alpha = 0.1f) 
                                    else 
                                        ErrorColor.copy(alpha = 0.1f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (uiState.hasUserConfirmed) 
                                            Icons.Default.CheckCircle 
                                        else 
                                            Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (uiState.hasUserConfirmed) 
                                            SuccessColor 
                                        else 
                                            ErrorColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (uiState.hasUserConfirmed) 
                                            "You have confirmed this report" 
                                        else 
                                            "You have dismissed this report",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (uiState.hasUserConfirmed) 
                                            SuccessColor 
                                        else 
                                            ErrorColor
                                    )
                                }
                            }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AppButton(
                                text = if (uiState.hasUserConfirmed) "Confirmed ✓" else "Confirm",
                                onClick = {
                                    viewModel.confirmReport(report.id)
                                },
                                modifier = Modifier.weight(1f),
                                isSecondary = false,
                                enabled = !uiState.hasUserConfirmed && !uiState.hasUserDismissed
                            )
                            
                            AppButton(
                                text = if (uiState.hasUserDismissed) "Dismissed ✗" else "Dismiss",
                                onClick = {
                                    viewModel.dismissReport(report.id)
                                },
                                modifier = Modifier.weight(1f),
                                isSecondary = true,
                                enabled = !uiState.hasUserConfirmed && !uiState.hasUserDismissed
                            )
                        }
                        
                        // Info text
                        Text(
                            text = "You can only vote once per report",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper function to display info row
 */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

/**
 * Get color for disaster type
 */
@Composable
private fun getDisasterTypeColor(type: com.bc230420212.app.data.model.DisasterType): androidx.compose.ui.graphics.Color {
    return when (type) {
        com.bc230420212.app.data.model.DisasterType.FLOOD -> FloodColor
        com.bc230420212.app.data.model.DisasterType.FIRE -> FireColor
        com.bc230420212.app.data.model.DisasterType.EARTHQUAKE -> EarthquakeColor
        com.bc230420212.app.data.model.DisasterType.ACCIDENT -> AccidentColor
        else -> OtherDisasterColor
    }
}

/**
 * Get color for report status
 */
@Composable
private fun getStatusColor(status: ReportStatus): androidx.compose.ui.graphics.Color {
    return when (status) {
        ReportStatus.ACTIVE -> WarningColor
        ReportStatus.VERIFIED -> SuccessColor
        ReportStatus.RESOLVED -> SuccessColor
        ReportStatus.FALSE_ALARM -> ErrorColor
    }
}

/**
 * Format timestamp to readable date/time
 */
private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
    return format.format(date)
}

