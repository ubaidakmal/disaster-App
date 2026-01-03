package com.bc230420212.app.ui.screens.report

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
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
    val activity = context as? Activity
    
    // Location permissions
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // Camera and storage permissions for photo/video
    val cameraPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    )
    
    // Image picker launcher - Using GetMultipleContents for image selection
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        android.util.Log.d("ReportDisaster", "Image picker returned ${uris.size} images")
        if (uris.isNotEmpty()) {
            val imagePaths = uris.mapNotNull { uri ->
                try {
                    android.util.Log.d("ReportDisaster", "Processing URI: $uri")
                    // Convert URI to file path
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val fileName = uri.lastPathSegment ?: "image_${System.currentTimeMillis()}"
                    val tempFile = java.io.File(context.cacheDir, "temp_${System.currentTimeMillis()}_$fileName")
                    inputStream?.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    android.util.Log.d("ReportDisaster", "Image saved to: ${tempFile.absolutePath}")
                    tempFile.absolutePath
                } catch (e: Exception) {
                    android.util.Log.e("ReportDisaster", "Error copying image: ${e.message}", e)
                    null
                }
            }
            if (imagePaths.isNotEmpty()) {
                android.util.Log.d("ReportDisaster", "Adding ${imagePaths.size} images to viewModel")
                viewModel.addSelectedImages(imagePaths)
            }
        } else {
            android.util.Log.d("ReportDisaster", "No images selected")
        }
    }
    
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Selected Images Preview
                    if (uiState.selectedImagePaths.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(uiState.selectedImagePaths.size) { index ->
                                val imagePath = uiState.selectedImagePaths[index]
                                Box(modifier = Modifier.size(100.dp)) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(LocalContext.current)
                                                .data(imagePath)
                                                .build()
                                        ),
                                        contentDescription = "Selected image ${index + 1}",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { viewModel.removeSelectedImage(imagePath) },
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = com.bc230420212.app.ui.theme.ErrorColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Upload Button
                    AppButton(
                        text = if (uiState.selectedImagePaths.isEmpty()) "Select Images" else "Add More Images",
                        onClick = {
                            android.util.Log.d("ReportDisaster", "Select Images button clicked")
                            try {
                                // Launch image picker
                                // GetMultipleContents uses the system picker and doesn't require explicit permissions
                                val result = imagePickerLauncher.launch("image/*")
                                android.util.Log.d("ReportDisaster", "Image picker launcher invoked, result: $result")
                            } catch (e: Exception) {
                                android.util.Log.e("ReportDisaster", "Error launching image picker: ${e.message}", e)
                                // Fallback: Try using Intent directly
                                try {
                                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                                        type = "image/*"
                                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                                        addCategory(Intent.CATEGORY_OPENABLE)
                                    }
                                    val pickIntent = Intent.createChooser(intent, "Select Images")
                                    activity?.startActivityForResult(pickIntent, 100)
                                } catch (e2: Exception) {
                                    android.util.Log.e("ReportDisaster", "Fallback intent also failed: ${e2.message}", e2)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isSecondary = uiState.selectedImagePaths.isNotEmpty()
                    )
                    
                    if (uiState.isUploadingImages && uiState.uploadProgress != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = uiState.uploadProgress!!,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
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
