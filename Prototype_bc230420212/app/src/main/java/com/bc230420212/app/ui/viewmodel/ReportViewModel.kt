package com.bc230420212.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bc230420212.app.data.model.DisasterReport
import com.bc230420212.app.data.model.DisasterType
import com.bc230420212.app.data.model.ReportStatus
import com.bc230420212.app.data.repository.ReportRepository
import com.bc230420212.app.util.CloudinaryHelper
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
    val mediaUrls: List<String> = emptyList(),
    val selectedImagePaths: List<String> = emptyList(), // Local file paths
    val isUploadingImages: Boolean = false,
    val uploadProgress: String? = null
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
     * Add selected image paths
     */
    fun addSelectedImages(imagePaths: List<String>) {
        _uiState.value = _uiState.value.copy(
            selectedImagePaths = _uiState.value.selectedImagePaths + imagePaths
        )
    }
    
    /**
     * Remove selected image
     */
    fun removeSelectedImage(imagePath: String) {
        _uiState.value = _uiState.value.copy(
            selectedImagePaths = _uiState.value.selectedImagePaths.filter { it != imagePath }
        )
    }
    
    /**
     * Clear all selected images
     */
    fun clearSelectedImages() {
        _uiState.value = _uiState.value.copy(selectedImagePaths = emptyList())
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
     * First uploads images to Cloudinary, then saves report with image URLs
     */
    fun submitReport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isUploadingImages = false,
                errorMessage = null,
                uploadProgress = null
            )
            
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
            
            // Upload images to Cloudinary if any are selected
            val uploadedUrls = mutableListOf<String>()
            
            if (_uiState.value.selectedImagePaths.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isUploadingImages = true,
                    uploadProgress = "Uploading images..."
                )
                
                for ((index, imagePath) in _uiState.value.selectedImagePaths.withIndex()) {
                    _uiState.value = _uiState.value.copy(
                        uploadProgress = "Uploading image ${index + 1} of ${_uiState.value.selectedImagePaths.size}..."
                    )
                    
                    val uploadResult = CloudinaryHelper.uploadImage(imagePath)
                    
                    if (uploadResult.isSuccess) {
                        uploadedUrls.add(uploadResult.getOrNull()!!)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isUploadingImages = false,
                            errorMessage = "Failed to upload image: ${uploadResult.exceptionOrNull()?.message}",
                            uploadProgress = null
                        )
                        return@launch
                    }
                }
            }
            
            _uiState.value = _uiState.value.copy(
                isUploadingImages = false,
                uploadProgress = "Saving report..."
            )
            
            // Create report object with uploaded image URLs
            val report = DisasterReport(
                disasterType = _uiState.value.selectedDisasterType,
                description = _uiState.value.description,
                latitude = _uiState.value.latitude,
                longitude = _uiState.value.longitude,
                address = _uiState.value.address,
                mediaUrls = uploadedUrls,
                status = ReportStatus.ACTIVE
            )
            
            // Save to Firestore
            val result = reportRepository.saveReport(report)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessage = null,
                    uploadProgress = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to submit report",
                    uploadProgress = null
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

