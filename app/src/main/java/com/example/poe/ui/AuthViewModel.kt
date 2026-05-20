package com.example.poe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poe.data.User
import com.example.poe.data.UserRepository
import com.example.poe.util.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling user authentication (Login and Registration).
 * manages the current user state and provides updates to the UI via AuthState flows.
 * demonstrates interaction between UI, Repository, and Data layers.
 */
class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    // Holds the currently logged-in user object
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // Holds the current state of the authentication process (Loading, Success, Error, etc.)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    /**
     * Attempts to log in a user with the provided credentials.
     * Updates _authState and _currentUser upon success or failure.
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            AppLogger.i("Login attempt for user: $username", "Auth")
            _authState.value = AuthState.Loading
            val user = repository.login(username, password)
            if (user != null) {
                _currentUser.value = user
                _authState.value = AuthState.Success
                AppLogger.i("Login SUCCESS for user ID: ${user.id}", "Auth")
            } else {
                _authState.value = AuthState.Error("Invalid username or password")
                AppLogger.e("Login FAILED for username: $username", subTag = "Auth")
            }
        }
    }

    /**
     * Registers a new user into the system.
     */
    fun register(user: User) {
        viewModelScope.launch {
            AppLogger.i("Registration attempt for username: ${user.username}", "Auth")
            _authState.value = AuthState.Loading
            try {
                repository.registerUser(user)
                _authState.value = AuthState.RegisterSuccess
                AppLogger.i("Registration SUCCESS for username: ${user.username}", "Auth")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Registration failed: ${e.message}")
                AppLogger.e("Registration FAILED", e, "Auth")
            }
        }
    }

    /**
     * Updates the profile information (like monthly goals) for the current user.
     */
    fun updateUser(user: User) {
        viewModelScope.launch {
            AppLogger.i("Updating user goals for user ID: ${user.id}", "Auth")
            repository.updateUser(user)
            _currentUser.value = user
            AppLogger.i("User goals updated successfully", "Auth")
        }
    }

    /**
     * Clears the current user and resets the auth state.
     */
    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    /**
     * Resets the authentication state to Idle (e.g., after showing an error toast).
     */
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

/**
 * Represents the various states of the authentication flow.
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object RegisterSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}
