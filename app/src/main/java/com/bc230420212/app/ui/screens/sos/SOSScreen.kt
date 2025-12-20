package com.bc230420212.app.ui.screens.sos

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bc230420212.app.ui.components.AppButton
import com.bc230420212.app.ui.theme.AccentColor
import com.bc230420212.app.ui.theme.PrimaryColor
import com.bc230420212.app.ui.theme.TextOnPrimary
import com.bc230420212.app.ui.theme.TextPrimary

/**
 * SOS EMERGENCY SCREEN
 * 
 * This screen allows users to send emergency SOS alerts.
 * Users can:
 * - Send instant emergency broadcast alerts
 * - Share live location with saved contacts
 * - Send alert to Firebase for authorities
 * - Quick access emergency feature
 * 
 * @param onNavigateBack - Function to navigate back to home
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(
    onNavigateBack: () -> Unit
) {
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
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = "SOS Emergency",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AccentColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Send emergency alert with your live location",
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Large SOS Button
            AppButton(
                text = "SEND SOS ALERT",
                onClick = {
                    // TODO: Implement SOS alert functionality
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                isSecondary = false
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Features to implement:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "• Capture live GPS location\n• Send alert to saved contacts\n• Send alert to Firebase\n• Push notification to authorities\n• Emergency contact management",
                fontSize = 14.sp,
                color = com.bc230420212.app.ui.theme.TextSecondary
            )
        }
    }
}

