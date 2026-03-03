package com.bc230420212.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bc230420212.app.data.model.UserRole
import com.bc230420212.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val userRole: UserRole = UserRole.USER
)

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            viewModelScope.launch {
                val role = authRepository.getUserRole(currentUser.uid)
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true,
                    userRole = role
                )
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val result = authRepository.signInWithEmail(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                val role = authRepository.getUserRole(user.uid)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    userRole = role
                )
            } else {
                val exception = result.exceptionOrNull()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception?.message ?: "Sign in failed"
                )
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val result = authRepository.signUpWithEmail(email, password, displayName)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    userRole = UserRole.USER
                )
            } else {
                val exception = result.exceptionOrNull()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception?.message ?: "Sign up failed"
                )
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val result = authRepository.signInWithGoogle(idToken)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                val role = authRepository.getUserRole(user.uid)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    userRole = role
                )
            } else {
                val exception = result.exceptionOrNull()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception?.message ?: "Google sign in failed"
                )
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Change user password
     */
    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val result = authRepository.changePassword(currentPassword, newPassword)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to change password"
                )
            }
        }
    }
}

