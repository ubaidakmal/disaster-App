package com.bc230420212.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bc230420212.app.data.model.DisasterReport
import com.bc230420212.app.data.model.ReportStatus
import com.bc230420212.app.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ADMIN VIEW MODEL
 * 
 * Manages state and logic for the Admin Panel screen.
 * Handles fetching pending reports and updating report status.
 */
data class AdminUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val pendingReports: List<DisasterReport> = emptyList(),
    val isUpdatingStatus: Boolean = false
)

class AdminViewModel : ViewModel() {
    private val reportRepository = ReportRepository()
    
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()
    
    init {
        loadPendingReports()
    }
    
    /**
     * Load all pending reports (ACTIVE status)
     */
    fun loadPendingReports() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val result = reportRepository.getPendingReports()
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    pendingReports = result.getOrNull() ?: emptyList()
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
     * Update report status (Admin action)
     * 
     * @param reportId - ID of the report to update
     * @param status - New status (VERIFIED, RESOLVED, FALSE_ALARM)
     */
    fun updateReportStatus(reportId: String, status: ReportStatus) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingStatus = true, errorMessage = null)
            
            val result = reportRepository.updateReportStatus(reportId, status)
            
            if (result.isSuccess) {
                // Remove the updated report from pending list
                _uiState.value = _uiState.value.copy(
                    isUpdatingStatus = false,
                    pendingReports = _uiState.value.pendingReports.filter { it.id != reportId }
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isUpdatingStatus = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to update status"
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
}

