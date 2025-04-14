package com.example.myapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.local.entity.UserEntity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun handleGoogleSignInResult(account: GoogleSignInAccount) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val user = authRepository.authenticateWithGoogle(account)
                _authState.value = AuthState.Success(user)
            } catch (e: Exception) {
                // Allow continuation even on failure
                _authState.value = AuthState.Error(
                    message = e.message ?: "Authentication failed",
                    allowContinue = true
                )
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserEntity) : AuthState()
    data class Error(val message: String, val allowContinue: Boolean = false) : AuthState() // Add flag
}