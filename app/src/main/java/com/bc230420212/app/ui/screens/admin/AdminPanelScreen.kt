package com.bc230420212.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
                    containerColor = PrimaryColor,
                    titleContentColor = TextOnPrimary,
                    navigationIconContentColor = TextOnPrimary
                )
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
            ) {
            // Success/Error Messages with Version 3 design
            if (uiState.successMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessColor.copy(alpha = 0.18f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        SuccessColor.copy(alpha = 0.2f),
                                        SuccessColor.copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Text(
                            text = uiState.successMessage!!,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessColor
                        )
                    }
                }
            }
            
            if (uiState.errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorColor.copy(alpha = 0.18f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        ErrorColor.copy(alpha = 0.2f),
                                        ErrorColor.copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.errorMessage!!,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
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
            }
            
            // Header with Version 3 Gradient
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceColor
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    PrimaryColor.copy(alpha = 0.18f),
                                    SecondaryColor.copy(alpha = 0.12f),
                                    GradientAccent.copy(alpha = 0.08f)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "Pending Reports",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryColor
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Review and update report status",
                            fontSize = 15.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PrimaryColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Total: ${uiState.pendingReports.size}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryColor,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
            
            // Reports List
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryColor,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                        uiState.pendingReports.isEmpty() -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(18.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(
                                                        SuccessColor.copy(alpha = 0.12f),
                                                        BackgroundColor
                                                    )
                                                )
                                            )
                                            .padding(36.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "No Pending",
                                                tint = SuccessColor,
                                                modifier = Modifier.size(72.dp)
                                            )
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Text(
                                                text = "No Pending Reports",
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "All reports have been reviewed",
                                                fontSize = 15.sp,
                                                color = TextSecondary,
                                                modifier = Modifier.padding(top = 10.dp),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
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
}

/**
 * Admin Report Card with Status Update Buttons
 */
@Composable
fun AdminReportCard(
    report: DisasterReport,
    onUpdateStatus: (ReportStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            getDisasterTypeColor(report.disasterType).copy(alpha = 0.08f),
                            SurfaceColor
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Report Header with Version 3 design
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = report.disasterType.displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = getDisasterTypeColor(report.disasterType)
                    )
                    Surface(
                        color = WarningColor,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = report.status.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
                
                // Description
                Text(
                    text = report.description,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    lineHeight = 22.sp,
                    maxLines = 3
                )
                
                // Report Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTimestamp(report.timestamp),
                        fontSize = 13.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "✓ ${report.confirmations}",
                            fontSize = 13.sp,
                            color = SuccessColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "✗ ${report.dismissals}",
                            fontSize = 13.sp,
                            color = ErrorColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                HorizontalDivider(
                    color = DividerColor.copy(alpha = 0.5f),
                    thickness = 1.dp
                )
                
                // Status Update Buttons with Version 3 design
                Text(
                    text = "Update Status:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Verified Button
                    Button(
                        onClick = { onUpdateStatus(ReportStatus.VERIFIED) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessColor
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verify",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verify", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    // Resolved Button
                    Button(
                        onClick = { onUpdateStatus(ReportStatus.RESOLVED) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryColor
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Resolve",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Resolve", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    // False Alarm Button
                    Button(
                        onClick = { onUpdateStatus(ReportStatus.FALSE_ALARM) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorColor
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "False Alarm",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("False", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
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

