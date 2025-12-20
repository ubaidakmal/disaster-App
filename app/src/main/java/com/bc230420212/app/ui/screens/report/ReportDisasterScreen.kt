package com.bc230420212.app.ui.screens.report

import android.Manifest
import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.data.model.DisasterType
import com.bc230420212.app.ui.components.AppButton
import com.bc230420212.app.ui.components.AppDropdown
import com.bc230420212.app.ui.components.AppTextArea
import com.bc230420212.app.ui.theme.PrimaryColor
import com.bc230420212.app.ui.theme.TextOnPrimary
import com.bc230420212.app.ui.theme.TextPrimary
import com.bc230420212.app.ui.theme.TextSecondary
import com.bc230420212.app.ui.viewmodel.ReportViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import java.util.Locale

/**
 * REPORT DISASTER SCREEN
 * 
 * This screen allows users to report a disaster with:
 * - Disaster type selection (dropdown)
 * - Description text box
 * - GPS location capture
 * - Optional photo/video upload
 * - Submit button to save to Firestore
 * 
 * @param onNavigateBack - Function to navigate back to home
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ReportDisasterScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReportViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Location permissions
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // Camera permissions for photo/video
    val cameraPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )
    
    // Show success dialog
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            // Reset form after showing success
            viewModel.resetForm()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Report Disaster",
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Disaster Type Dropdown
            Text(
                text = "Disaster Type *",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            AppDropdown(
                label = "Select Disaster Type",
                options = DisasterType.values().map { it.displayName },
                selectedOption = uiState.selectedDisasterType.displayName,
                onOptionSelected = { selectedName ->
                    val selectedType = DisasterType.values().find { it.displayName == selectedName }
                    selectedType?.let { viewModel.updateDisasterType(it) }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description Text Area
            Text(
                text = "Description *",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            AppTextArea(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = "Describe the disaster situation",
                isError = uiState.errorMessage != null && uiState.description.isBlank(),
                errorMessage = if (uiState.description.isBlank()) "Description is required" else ""
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Location Section
            Text(
                text = "Location (GPS) *",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            // Location Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = com.bc230420212.app.ui.theme.SurfaceColor
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiState.latitude != 0.0 && uiState.longitude != 0.0) {
                        // Show captured location
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = PrimaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Location Captured",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                if (uiState.address.isNotEmpty()) {
                                    Text(
                                        text = uiState.address,
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                } else {
                                    Text(
                                        text = "Lat: ${String.format("%.6f", uiState.latitude)}, Lng: ${String.format("%.6f", uiState.longitude)}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No location captured",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                    
                    // Capture Location Button
                    AppButton(
                        text = if (uiState.latitude != 0.0 && uiState.longitude != 0.0) "Update Location" else "Capture Location",
                        onClick = {
                            if (locationPermissionsState.allPermissionsGranted) {
                                captureLocation(context, viewModel)
                            } else {
                                locationPermissionsState.launchMultiplePermissionRequest()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Photo/Video Upload Section (Optional)
            Text(
                text = "Media (Optional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = com.bc230420212.app.ui.theme.SurfaceColor
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📷",
                        fontSize = 48.sp
                    )
                    Text(
                        text = "Photo/Video Upload",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "This feature will be implemented to allow uploading photos/videos to Firebase Storage",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    AppButton(
                        text = "Upload Media (Coming Soon)",
                        onClick = {
                            // TODO: Implement photo/video upload
                            if (cameraPermissionsState.allPermissionsGranted) {
                                // Open camera/gallery
                            } else {
                                cameraPermissionsState.launchMultiplePermissionRequest()
                            }
                        },
                        enabled = false, // Disabled until implemented
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Error Message
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = com.bc230420212.app.ui.theme.ErrorColor,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Submit Button
            AppButton(
                text = "Submit Report",
                onClick = { viewModel.submitReport() },
                enabled = !uiState.isLoading && 
                         uiState.description.isNotBlank() && 
                         uiState.latitude != 0.0 && 
                         uiState.longitude != 0.0,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Loading Indicator
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    color = PrimaryColor
                )
            }
        }
    }
    
    // Success Dialog
    if (uiState.isSuccess) {
        AlertDialog(
            onDismissRequest = { 
                viewModel.clearSuccess()
                onNavigateBack()
            },
            title = { Text("Report Submitted!") },
            text = { Text("Your disaster report has been submitted successfully.") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.clearSuccess()
                    onNavigateBack()
                }) {
                    Text("OK")
                }
            }
        )
    }
}

/**
 * Capture GPS location using Fused Location Provider
 * 
 * @param context - Android context
 * @param viewModel - ReportViewModel to update location
 */
private fun captureLocation(context: Context, viewModel: ReportViewModel) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    
    // Request current location
    fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        CancellationTokenSource().token
    ).addOnSuccessListener { location ->
        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude
            
            // Try to get address from coordinates
            val geocoder = Geocoder(context, Locale.getDefault())
            var address = ""
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    address = addresses[0].getAddressLine(0) ?: ""
                }
            } catch (e: Exception) {
                // If geocoding fails, just use coordinates
                address = ""
            }
            
            viewModel.updateLocation(latitude, longitude, address)
        }
    }.addOnFailureListener {
        // Handle error
    }
}
