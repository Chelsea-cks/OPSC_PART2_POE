package com.example.poe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poe.data.User
import com.example.poe.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = repository.login(username, password)
            if (user != null) {
                _currentUser.value = user
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Invalid username or password")
            }
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.registerUser(user)
                _authState.value = AuthState.RegisterSuccess
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Registration failed: ${e.message}")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object RegisterSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}
