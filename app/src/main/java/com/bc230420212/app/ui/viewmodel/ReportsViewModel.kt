package com.bc230420212.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bc230420212.app.data.model.DisasterReport
import com.bc230420212.app.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * REPORTS UI STATE
 * 
 * This data class holds the state of the View Reports screen.
 */
data class ReportsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val reports: List<DisasterReport> = emptyList(),
    val selectedReport: DisasterReport? = null,
    val hasUserConfirmed: Boolean = false,
    val hasUserDismissed: Boolean = false
)

/**
 * REPORTS VIEWMODEL
 * 
 * This ViewModel manages the state and logic for the View Reports screen.
 * It handles:
 * - Fetching reports from Firestore
 * - Filtering reports (active/past)
 * - Confirming/dismissing reports
 */
class ReportsViewModel : ViewModel() {
    private val reportRepository = ReportRepository()
    
    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()
    
    init {
        loadReports()
    }
    
    /**
     * Load all reports from Firestore
     */
    fun loadReports() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val result = reportRepository.getAllReports()
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    reports = result.getOrNull() ?: emptyList(),
                    errorMessage = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load reports"
                )
            }
        }
    }
    
    /**
     * Load a specific report by ID
     */
    fun loadReportById(reportId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val result = reportRepository.getReportById(reportId)
            
            if (result.isSuccess) {
                val report = result.getOrNull()
                // Check if current user has already voted
                val hasConfirmed = reportRepository.hasUserConfirmed(reportId)
                val hasDismissed = reportRepository.hasUserDismissed(reportId)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedReport = report,
                    hasUserConfirmed = hasConfirmed,
                    hasUserDismissed = hasDismissed,
                    errorMessage = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load report"
                )
            }
        }
    }
    
    /**
     * Confirm a report (one-time per user)
     */
    fun confirmReport(reportId: String) {
        viewModelScope.launch {
            // Check if user has already voted
            if (_uiState.value.hasUserConfirmed || _uiState.value.hasUserDismissed) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "You have already voted on this report"
                )
                return@launch
            }
            
            val result = reportRepository.confirmReport(reportId)
            
            if (result.isSuccess) {
                // Reload reports to get updated counts
                loadReports()
                // Reload selected report if it's the same one
                _uiState.value.selectedReport?.let {
                    if (it.id == reportId) {
                        loadReportById(reportId)
                    }
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to confirm report"
                )
            }
        }
    }
    
    /**
     * Dismiss a report (one-time per user)
     */
    fun dismissReport(reportId: String) {
        viewModelScope.launch {
            // Check if user has already voted
            if (_uiState.value.hasUserConfirmed || _uiState.value.hasUserDismissed) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "You have already voted on this report"
                )
                return@launch
            }
            
            val result = reportRepository.dismissReport(reportId)
            
            if (result.isSuccess) {
                // Reload reports to get updated counts
                loadReports()
                // Reload selected report if it's the same one
                _uiState.value.selectedReport?.let {
                    if (it.id == reportId) {
                        loadReportById(reportId)
                    }
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to dismiss report"
                )
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Clear selected report
     */
    fun clearSelectedReport() {
        _uiState.value = _uiState.value.copy(selectedReport = null)
    }
}

