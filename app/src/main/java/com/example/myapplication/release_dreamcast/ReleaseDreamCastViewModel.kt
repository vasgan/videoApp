package com.example.myapplication.release_dreamcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.parse.dreamcast.DreamCastDescriptionAndSeries
import com.example.myapplication.repository.dreamcast.DreamCastRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReleaseDreamCastViewModel @Inject constructor(val repository: DreamCastRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<DreamCastDescriptionAndSeries?> = MutableStateFlow(null)
    val uiState: StateFlow<DreamCastDescriptionAndSeries?> = _uiState.asStateFlow()

    fun getSeriesList(url: String) {
        viewModelScope.launch {
            _uiState.value = repository.getVideoUrls(url)
        }
    }
}