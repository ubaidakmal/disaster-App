package com.bc230420212.app.ui.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bc230420212.app.ui.components.ReportItem
import com.bc230420212.app.ui.theme.PrimaryColor
import com.bc230420212.app.ui.theme.TextOnPrimary
import com.bc230420212.app.ui.theme.TextPrimary
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Tabs (Active/Past)
            var selectedTab by remember { mutableStateOf(0) }
            
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = com.bc230420212.app.ui.theme.SurfaceColor
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Active") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Past") }
                )
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
                        CircularProgressIndicator(color = PrimaryColor)
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
                            color = com.bc230420212.app.ui.theme.ErrorColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = uiState.errorMessage!!,
                            fontSize = 14.sp,
                            color = com.bc230420212.app.ui.theme.TextSecondary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        com.bc230420212.app.ui.components.AppButton(
                            text = "Retry",
                            onClick = { viewModel.loadReports() }
                        )
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
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (selectedTab == 0) "No Active Reports" else "No Past Reports",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = if (selectedTab == 0) 
                                        "There are no active disaster reports at the moment." 
                                    else 
                                        "There are no past disaster reports.",
                                    fontSize = 14.sp,
                                    color = com.bc230420212.app.ui.theme.TextSecondary
                                )
                            }
                        }
                    } else {
                        // List of reports
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
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
