package com.example.myapplication.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import com.example.myapplication.repository.video.ReleaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: ReleaseRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(mutableListOf<ReleasesElementResponse>())
    val uiState: StateFlow<MutableList<ReleasesElementResponse>> = _uiState.asStateFlow()

    fun getVideo(){
        viewModelScope.launch {
            //delay(3000)
            repository.getVideo().let {
                _uiState.value = it.toMutableList()
            }
        }
    }
}