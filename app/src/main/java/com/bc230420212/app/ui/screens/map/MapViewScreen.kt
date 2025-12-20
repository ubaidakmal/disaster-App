package com.bc230420212.app.ui.screens.map

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.data.model.DisasterReport
import com.bc230420212.app.data.model.DisasterType
import com.bc230420212.app.ui.theme.*
import com.bc230420212.app.ui.viewmodel.ReportsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * MAP VIEW SCREEN
 * 
 * This screen displays disaster reports on an interactive Google Map.
 * Users can:
 * - See all disaster reports as markers on a map
 * - View report details by clicking markers (info window)
 * - See different colors for different disaster types
 * - Zoom and pan the map
 * 
 * @param onNavigateBack - Function to navigate back to home
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapViewScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReportsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Default camera position (can be set to user's location later)
    val defaultLocation = LatLng(24.8607, 67.0011) // Default to a central location
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }
    
    // Load reports when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadReports()
    }
    
    // Update camera position when reports are loaded
    LaunchedEffect(uiState.reports) {
        if (uiState.reports.isNotEmpty() && !uiState.isLoading) {
            // Find first report with valid coordinates
            val firstValidReport = uiState.reports.firstOrNull { report ->
                report.latitude != 0.0 && report.longitude != 0.0 &&
                report.latitude >= -90 && report.latitude <= 90 &&
                report.longitude >= -180 && report.longitude <= 180
            }
            
            if (firstValidReport != null) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    LatLng(firstValidReport.latitude, firstValidReport.longitude),
                    12f
                )
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Map View",
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
                    contentAlignment = androidx.compose.ui.Alignment.Center
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
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
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
                        onClick = { viewModel.loadReports() }
                    )
                }
            }
            
            uiState.reports.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Reports Available",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "There are no disaster reports to display on the map.",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
            
            else -> {
                // Filter reports with valid coordinates (not 0.0, 0.0)
                val validReports = uiState.reports.filter { report ->
                    report.latitude != 0.0 && report.longitude != 0.0 &&
                    report.latitude >= -90 && report.latitude <= 90 &&
                    report.longitude >= -180 && report.longitude <= 180
                }
                
                if (validReports.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Valid Reports",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = "Reports need valid GPS coordinates to display on the map.",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    // Google Map with markers
                    Box(modifier = Modifier.fillMaxSize()) {
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(
                                mapType = MapType.NORMAL,
                                isMyLocationEnabled = false // Can be enabled if location permission is granted
                            ),
                            uiSettings = MapUiSettings(
                                zoomControlsEnabled = true,
                                myLocationButtonEnabled = false,
                                compassEnabled = true
                            )
                        ) {
                            // Add markers for each valid report
                            validReports.forEach { report ->
                                val reportLocation = LatLng(report.latitude, report.longitude)
                                val markerColor = getMarkerColor(report.disasterType)
                                
                                Marker(
                                    state = MarkerState(position = reportLocation),
                                    title = report.disasterType.displayName,
                                    snippet = formatMarkerSnippet(report),
                                    icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(markerColor)
                                )
                            }
                        }
                        
                        // Debug overlay showing report count
                        Card(
                            modifier = Modifier
                                .align(androidx.compose.ui.Alignment.TopEnd)
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = PrimaryColor.copy(alpha = 0.9f)
                            )
                        ) {
                            Text(
                                text = "Reports: ${validReports.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Format marker snippet with report details
 */
private fun formatMarkerSnippet(report: DisasterReport): String {
    val timeFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val time = timeFormat.format(Date(report.timestamp))
    
    return buildString {
        append("Time: $time\n")
        append("Status: ${report.status.name}\n")
        append("Confirmations: ${report.confirmations} | Dismissals: ${report.dismissals}")
        if (report.description.isNotEmpty()) {
            val shortDesc = if (report.description.length > 50) {
                report.description.take(50) + "..."
            } else {
                report.description
            }
            append("\n$shortDesc")
        }
    }
}

/**
 * Get marker color based on disaster type
 * Returns a hue value for Google Maps markers
 */
private fun getMarkerColor(disasterType: DisasterType): Float {
    return when (disasterType) {
        DisasterType.FLOOD -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE
        DisasterType.FIRE -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
        DisasterType.EARTHQUAKE -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE
        DisasterType.ACCIDENT -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW
        DisasterType.STORM -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_VIOLET
        DisasterType.LANDSLIDE -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_MAGENTA
        else -> com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
    }
}
