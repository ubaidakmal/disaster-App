package com.bc230420212.app.ui.viewmodel

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bc230420212.app.data.model.SOSAlert
import com.bc230420212.app.data.repository.SOSRepository
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * SOS UI STATE
 * 
 * This data class holds the state of the SOS Emergency screen.
 */
data class SOSUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val currentLocation: LatLng? = null,
    val locationAddress: String = "",
    val isLocationLoading: Boolean = false,
    val isSOSActive: Boolean = false
)

/**
 * SOS VIEWMODEL
 * 
 * This ViewModel manages the state and logic for the SOS Emergency screen.
 * It handles:
 * - Live location tracking
 * - Sending SOS alerts to Firebase
 * - Managing SOS alert state
 */
class SOSViewModel : ViewModel() {
    private val sosRepository = SOSRepository()
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    
    private val _uiState = MutableStateFlow(SOSUiState())
    val uiState: StateFlow<SOSUiState> = _uiState.asStateFlow()
    
    /**
     * Initialize location client
     */
    fun initializeLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }
    
    /**
     * Get current location
     */
    fun getCurrentLocation(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLocationLoading = true, errorMessage = null)
            
            try {
                if (fusedLocationClient == null) {
                    initializeLocationClient(context)
                }
                
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    10000L // 10 seconds
                ).build()
                
                val locationSettingsRequest = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .build()
                
                val settingsClient = LocationServices.getSettingsClient(context)
                val settingsResponse = settingsClient.checkLocationSettings(locationSettingsRequest).await()
                
                if (settingsResponse.locationSettingsStates?.isLocationUsable == true) {
                    val location = fusedLocationClient!!.lastLocation.await()
                    
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        _uiState.value = _uiState.value.copy(
                            currentLocation = latLng,
                            isLocationLoading = false
                        )
                        
                        // Get address from coordinates
                        getAddressFromLocation(location)
                    } else {
                        // Request location updates
                        requestLocationUpdates(context)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLocationLoading = false,
                        errorMessage = "Location services are not available"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLocationLoading = false,
                    errorMessage = "Failed to get location: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Request location updates
     */
    private fun requestLocationUpdates(context: Context) {
        if (fusedLocationClient == null) {
            initializeLocationClient(context)
        }
        
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L // 5 seconds
        ).build()
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    _uiState.value = _uiState.value.copy(
                        currentLocation = latLng,
                        isLocationLoading = false
                    )
                    getAddressFromLocation(location)
                    
                    // Stop updates after getting location
                    fusedLocationClient?.removeLocationUpdates(this)
                }
            }
        }
        
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            context.mainLooper
        )
    }
    
    /**
     * Get address from location coordinates
     */
    private fun getAddressFromLocation(location: Location) {
        viewModelScope.launch {
            try {
                // Note: Geocoder requires a Context, so we'll just use coordinates for now
                // In a production app, you'd pass context or use a different approach
                _uiState.value = _uiState.value.copy(
                    locationAddress = "Lat: ${String.format("%.6f", location.latitude)}, Lng: ${String.format("%.6f", location.longitude)}"
                )
            } catch (e: Exception) {
                // Geocoding failed, but location is still available
                _uiState.value = _uiState.value.copy(
                    locationAddress = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                )
            }
        }
    }
    
    /**
     * Send SOS alert
     */
    fun sendSOSAlert(context: Context, message: String = "") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )
            
            // Get current location first
            if (_uiState.value.currentLocation == null) {
                getCurrentLocation(context)
                // Wait a bit for location
                kotlinx.coroutines.delay(2000)
            }
            
            val location = _uiState.value.currentLocation
            if (location == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Unable to get your location. Please try again."
                )
                return@launch
            }
            
            // Create SOS alert
            val alert = SOSAlert(
                latitude = location.latitude,
                longitude = location.longitude,
                address = _uiState.value.locationAddress,
                message = message,
                timestamp = System.currentTimeMillis()
            )
            
            // Send to Firebase
            val result = sosRepository.sendSOSAlert(alert)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "SOS Alert sent successfully! Help is on the way.",
                    isSOSActive = true
                )
                
                // Clear success message after 5 seconds
                kotlinx.coroutines.delay(5000)
                _uiState.value = _uiState.value.copy(successMessage = null)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to send SOS alert"
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
    
    /**
     * Stop location updates
     */
    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient?.removeLocationUpdates(it)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }
}

