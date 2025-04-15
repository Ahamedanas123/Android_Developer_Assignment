package com.example.myapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.local.entity.UserEntity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.getCurrentUser()
                if (user != null && user.isLoggedIn) {
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Idle
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    message = e.message ?: "Failed to check user state",
                    allowContinue = false
                )
            }
        }
    }

    // In AuthViewModel.kt
    fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                if (account.idToken.isNullOrEmpty()) {
                    throw IllegalArgumentException("Invalid Google account: missing ID token")
                }

                // Proceed with authentication
                val user = authRepository.authenticateWithGoogle(account)
                _authState.postValue(AuthState.Success(user))

            } catch (e: Exception) {
                _authState.postValue(AuthState.Error(
                    "Authentication failed: ${e.message ?: "Unknown error"}"
                ))
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserEntity) : AuthState()
    data class Error(val message: String, val allowContinue: Boolean = false) : AuthState()
}