package com.example.myapp.ui.objects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ObjectsViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    fun fetchObjects() {
        viewModelScope.launch {
            repository.fetchObjects()
        }
    }
}