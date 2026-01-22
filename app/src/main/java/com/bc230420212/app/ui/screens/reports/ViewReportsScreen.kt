package com.bc230420212.app.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.ui.components.ReportItem
import com.bc230420212.app.ui.theme.*
import com.bc230420212.app.ui.viewmodel.ReportsViewModel

/**
 * VIEW REPORTS SCREEN (LIST VIEW)
 * 
 * This screen displays a list of all disaster reports (active and past).
 * Each item shows: type, time, short description, status, verification info
 * Tapping an item opens the Report Details screen.
 * 
 * @param onNavigateBack - Function to navigate back to home
 * @param onNavigateToDetails - Function to navigate to report details
 * @param viewModel - ViewModel for managing reports state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewReportsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit, // Takes report ID
    viewModel: ReportsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "View Reports",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Filter Tabs (Active/Past) with Version 3 design
                var selectedTab by remember { mutableStateOf(0) }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = SurfaceColor
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { 
                                Text(
                                    "Active Reports",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = if (selectedTab == 0) PrimaryColor else TextSecondary
                                ) 
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { 
                                Text(
                                    "Past Reports",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = if (selectedTab == 1) PrimaryColor else TextSecondary
                                ) 
                            }
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
                                        text = "Oops!",
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
                                        onClick = { viewModel.loadReports() },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ErrorColor
                                        )
                                    ) {
                                        Text(
                                            text = "Try Again",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                    }
                }
                
                else -> {
                    // Filter reports based on selected tab
                    val filteredReports = if (selectedTab == 0) {
                        // Active reports
                        uiState.reports.filter { 
                            it.status == com.bc230420212.app.data.model.ReportStatus.ACTIVE 
                        }
                    } else {
                        // Past reports (RESOLVED or FALSE_ALARM)
                        uiState.reports.filter { 
                            it.status != com.bc230420212.app.data.model.ReportStatus.ACTIVE 
                        }
                    }
                    
                    if (filteredReports.isEmpty()) {
                        // Empty state
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
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
                                                    PrimaryColor.copy(alpha = 0.08f),
                                                    BackgroundColor
                                                )
                                            )
                                        )
                                        .padding(36.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (selectedTab == 0) "No Active Reports" else "No Past Reports",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            modifier = Modifier.padding(bottom = 14.dp)
                                        )
                                        Text(
                                            text = if (selectedTab == 0) 
                                                "There are no active disaster reports at the moment." 
                                            else 
                                                "There are no past disaster reports.",
                                            fontSize = 15.sp,
                                            color = TextSecondary,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // List of reports
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredReports) { report ->
                                ReportItem(
                                    report = report,
                                    onClick = {
                                        onNavigateToDetails(report.id)
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
}
