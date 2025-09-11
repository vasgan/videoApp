package com.example.myapplication.release_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.video.VideoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReleaseScreenViewModel @Inject constructor(
    private val repository: VideoRepository,
): ViewModel() {
    private val _uiState : MutableStateFlow<FullReleaseResponse?> = MutableStateFlow(null)
    val uiState: StateFlow<FullReleaseResponse?> = _uiState.asStateFlow()

    fun getCurrentRelease(id: String){
        viewModelScope.launch {
            delay(3000)
            repository.getRelease(id).let {
                _uiState.value = it
            }
        }
    }
}