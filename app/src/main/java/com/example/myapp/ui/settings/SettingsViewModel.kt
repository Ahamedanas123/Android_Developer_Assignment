package com.example.myapp.ui.settings


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    // Change to LiveData
    val isDarkTheme: LiveData<Boolean> = repository.isDarkTheme.asLiveData()

    fun setDarkThemeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkThemeEnabled(enabled)
        }
    }
}