package com.example.project_uts_rentaloan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_uts_rentaloan.data.local.entity.UserEntity
import com.example.project_uts_rentaloan.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState

    init {
        restoreSession()
    }

    private fun restoreSession() {
        viewModelScope.launch {
            val email = authRepository.getSessionEmail().first()
            if (email != null) {
                val user = authRepository.getUserByEmail(email)
                _currentUser.value = user
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            val trimmedEmail = email.trim()
            val trimmedPassword = password.trim()
            
            val result = authRepository.login(trimmedEmail, trimmedPassword)
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _authState.value = AuthUiState.Success
            } else {
                _authState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(user: UserEntity) {
        if (user.email.isBlank() || user.password.isBlank() || user.nama.isBlank()) {
            _authState.value = AuthUiState.Error("Please fill all required fields")
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user.email).matches()) {
            _authState.value = AuthUiState.Error("Invalid email format")
            return
        }

        if (user.password.length < 6) {
            _authState.value = AuthUiState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            val sanitizedUser = user.copy(
                email = user.email.trim(),
                password = user.password.trim(),
                nama = user.nama.trim(),
                kontak = user.kontak.trim()
            )
            val result = authRepository.register(sanitizedUser)
            if (result.isSuccess) {
                _authState.value = AuthUiState.RegisterSuccess
            } else {
                _authState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
            _authState.value = AuthUiState.Idle
        }
    }

    fun resetState() {
        _authState.value = AuthUiState.Idle
    }
}

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    object Success : AuthUiState
    object RegisterSuccess : AuthUiState
    data class Error(val message: String) : AuthUiState
}
