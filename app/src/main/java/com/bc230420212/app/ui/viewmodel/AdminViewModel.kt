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
 * ADMIN UI STATE
 * 
 * This data class holds the state of the Admin Panel screen.
 */
data class AdminUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val pendingReports: List<DisasterReport> = emptyList()
)

/**
 * ADMIN VIEWMODEL
 * 
 * This ViewModel manages the state and logic for the Admin Panel screen.
 * It handles:
 * - Fetching pending reports
 * - Updating report status
 * - Managing admin operations
 */
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
                    pendingReports = result.getOrNull() ?: emptyList(),
                    errorMessage = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load pending reports"
                )
            }
        }
    }
    
    /**
     * Update report status
     */
    fun updateReportStatus(reportId: String, newStatus: ReportStatus) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            
            val result = reportRepository.updateReportStatus(reportId, newStatus)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Report status updated to ${newStatus.name}",
                    errorMessage = null
                )
                
                // Reload pending reports
                loadPendingReports()
                
                // Clear success message after 3 seconds
                kotlinx.coroutines.delay(3000)
                _uiState.value = _uiState.value.copy(successMessage = null)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to update report status"
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
     * Clear success message
     */
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}

