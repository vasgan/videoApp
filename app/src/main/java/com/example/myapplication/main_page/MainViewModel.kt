package com.example.myapplication.main_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.video.VideoData
import com.example.myapplication.video.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: VideoRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(mutableListOf<VideoData>())
    val uiState: StateFlow<MutableList<VideoData>> = _uiState.asStateFlow()

    fun getVideo(){
        viewModelScope.launch {
            repository.getVideo().let {
                val value = it.toMutableList()
                _uiState.value = value
                var i = 0
                i = i + 1
            }
        }
    }
}