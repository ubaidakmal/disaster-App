package com.bc230420212.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bc230420212.app.data.model.DisasterReport
import com.bc230420212.app.data.model.DisasterType
import com.bc230420212.app.data.model.ReportStatus
import com.bc230420212.app.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * REPORT UI STATE
 * 
 * This data class holds the state of the Report Disaster screen.
 */
data class ReportUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val selectedDisasterType: DisasterType = DisasterType.OTHER,
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val mediaUrls: List<String> = emptyList()
)

/**
 * REPORT VIEWMODEL
 * 
 * This ViewModel manages the state and logic for the Report Disaster screen.
 * It handles:
 * - Form state management
 * - GPS location
 * - Submitting reports to Firestore
 */
class ReportViewModel : ViewModel() {
    private val reportRepository = ReportRepository()
    
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()
    
    /**
     * Update the selected disaster type
     */
    fun updateDisasterType(type: DisasterType) {
        _uiState.value = _uiState.value.copy(selectedDisasterType = type)
    }
    
    /**
     * Update the description text
     */
    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }
    
    /**
     * Update GPS location
     */
    fun updateLocation(latitude: Double, longitude: Double, address: String = "") {
        _uiState.value = _uiState.value.copy(
            latitude = latitude,
            longitude = longitude,
            address = address
        )
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Clear success state
     */
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
    
    /**
     * Submit the disaster report to Firestore
     */
    fun submitReport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // Validation
            if (_uiState.value.description.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Please enter a description"
                )
                return@launch
            }
            
            if (_uiState.value.latitude == 0.0 || _uiState.value.longitude == 0.0) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Please capture your location"
                )
                return@launch
            }
            
            // Create report object
            val report = DisasterReport(
                disasterType = _uiState.value.selectedDisasterType,
                description = _uiState.value.description,
                latitude = _uiState.value.latitude,
                longitude = _uiState.value.longitude,
                address = _uiState.value.address,
                mediaUrls = _uiState.value.mediaUrls,
                status = ReportStatus.ACTIVE
            )
            
            // Save to Firestore
            val result = reportRepository.saveReport(report)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to submit report"
                )
            }
        }
    }
    
    /**
     * Reset form after successful submission
     */
    fun resetForm() {
        _uiState.value = ReportUiState()
    }
}

