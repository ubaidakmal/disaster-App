package com.bc230420212.app.ui.screens.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
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

/**
 * Image Card Component
 * Displays a single image from Cloudinary URL
 */
@Composable
private fun ImageCard(imageUrl: String) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(200.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = "Report image",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryColor,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(40.dp)
                        )
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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorColor.copy(alpha = 0.12f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Error",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ErrorColor,
                                    modifier = Modifier.padding(bottom = 14.dp)
                                )
                                Text(
                                    text = uiState.errorMessage!!,
                                    fontSize = 15.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 24.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Button(
                                    onClick = onNavigateBack,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ErrorColor
                                    )
                                ) {
                                    Text(
                                        text = "Go Back",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
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
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Disaster Type and Status Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            getDisasterTypeColor(report.disasterType).copy(alpha = 0.12f),
                                            BackgroundColor
                                        )
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = report.disasterType.displayName,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = getDisasterTypeColor(report.disasterType)
                                )
                                
                                // Status Badge
                                Surface(
                                    color = getStatusColor(report.status),
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
                        }
                    }
                    
                    // Description Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp)
                        ) {
                            Text(
                                text = "Description",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Text(
                                text = report.description,
                                fontSize = 15.sp,
                                color = TextPrimary,
                                lineHeight = 22.sp
                            )
                        }
                    }
                    
                    // Location Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp)
                        ) {
                            Text(
                                text = "Location",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = PrimaryColor,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    if (report.address.isNotEmpty()) {
                                        Text(
                                            text = report.address,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                    }
                                    Text(
                                        text = "Lat: ${String.format("%.6f", report.latitude)}, Lng: ${String.format("%.6f", report.longitude)}",
                                        fontSize = 13.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                            
                            // Map Preview Placeholder
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                color = BackgroundColor,
                                shape = RoundedCornerShape(16.dp)
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
                                            fontSize = 15.sp,
                                            color = TextSecondary,
                                            fontWeight = FontWeight.Medium
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
                    
                    // Media Section (if exists)
                    if (report.mediaUrls.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(22.dp)
                            ) {
                                Text(
                                    text = "Media (${report.mediaUrls.size})",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    items(report.mediaUrls) { imageUrl ->
                                        ImageCard(imageUrl = imageUrl)
                                    }
                                }
                            }
                        }
                    }
                    
                    // Report Information
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp)
                        ) {
                            Text(
                                text = "Report Information",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            InfoRow("Time", formatTimestamp(report.timestamp))
                            Spacer(modifier = Modifier.height(8.dp))
                            InfoRow("Confirmations", "${report.confirmations}")
                            Spacer(modifier = Modifier.height(8.dp))
                            InfoRow("Dismissals", "${report.dismissals}")
                        }
                    }
                
                    // Show error message if exists
                    if (uiState.errorMessage != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ErrorColor.copy(alpha = 0.12f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = uiState.errorMessage!!,
                                fontSize = 14.sp,
                                color = ErrorColor,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(18.dp)
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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(22.dp)
                            ) {
                                Text(
                                    text = "Verification",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                
                                // Show voting status if user has already voted
                                if (uiState.hasUserConfirmed || uiState.hasUserDismissed) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (uiState.hasUserConfirmed) 
                                            SuccessColor.copy(alpha = 0.15f) 
                                        else 
                                            ErrorColor.copy(alpha = 0.15f)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(18.dp),
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
                                                modifier = Modifier.size(26.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = if (uiState.hasUserConfirmed) 
                                                    "You have confirmed this report" 
                                                else 
                                                    "You have dismissed this report",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
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
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.confirmReport(report.id)
                                        },
                                        modifier = Modifier.weight(1f),
                                        enabled = !uiState.hasUserConfirmed && !uiState.hasUserDismissed,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SuccessColor
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text(
                                            text = if (uiState.hasUserConfirmed) "Confirmed ✓" else "Confirm",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.dismissReport(report.id)
                                        },
                                        modifier = Modifier.weight(1f),
                                        enabled = !uiState.hasUserConfirmed && !uiState.hasUserDismissed,
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = ErrorColor
                                        ),
                                        border = BorderStroke(2.dp, ErrorColor)
                                    ) {
                                        Text(
                                            text = if (uiState.hasUserDismissed) "Dismissed ✗" else "Dismiss",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                // Info text
                                Text(
                                    text = "You can only vote once per report",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 12.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    }
}

