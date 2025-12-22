package com.bc230420212.app.ui.screens.sos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.ui.components.AppButton
import com.bc230420212.app.ui.theme.*
import com.bc230420212.app.ui.viewmodel.SOSViewModel

/**
 * SOS EMERGENCY SCREEN
 * 
 * This screen allows users to send emergency SOS alerts.
 * Features:
 * - Big SOS button for quick access
 * - Shows live location
 * - Sends SOS alert to Firebase system
 * - Ready for contacts integration
 * 
 * @param onNavigateBack - Function to navigate back to home
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(
    onNavigateBack: () -> Unit,
    viewModel: SOSViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Initialize location client and get location when screen opens
    LaunchedEffect(Unit) {
        viewModel.initializeLocationClient(context)
        viewModel.getCurrentLocation(context)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "SOS Emergency",
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
                    containerColor = ErrorColor,
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Warning message
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = WarningColor.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = "⚠️ Use this only in case of a real emergency!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Live Location Display
            if (uiState.isLocationLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PrimaryColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Getting your location...",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            } else if (uiState.currentLocation != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessColor.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = SuccessColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live Location",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessColor
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.locationAddress.ifEmpty { 
                                "Lat: ${String.format("%.6f", uiState.currentLocation!!.latitude)}, " +
                                "Lng: ${String.format("%.6f", uiState.currentLocation!!.longitude)}"
                            },
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = TextSecondary.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = "Location not available",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Big SOS Button
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(ErrorColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "SOS",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnPrimary
                    )
                    Text(
                        text = "EMERGENCY",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnPrimary
                    )
                }
            }
            
            // SOS Button (clickable)
            Button(
                onClick = {
                    viewModel.sendSOSAlert(context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorColor
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = !uiState.isLoading && uiState.currentLocation != null
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TextOnPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Sending SOS Alert...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnPrimary
                    )
                } else {
                    Text(
                        text = "SEND SOS ALERT",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnPrimary
                    )
                }
            }
            
            // Success/Error Messages
            if (uiState.successMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessColor.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = uiState.successMessage!!,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SuccessColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            if (uiState.errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorColor.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ErrorColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Info text
            Text(
                text = "This will send your location to emergency services and saved contacts",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
