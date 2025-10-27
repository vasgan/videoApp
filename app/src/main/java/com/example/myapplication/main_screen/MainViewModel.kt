package com.example.myapplication.main_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.example.myapplication.repository.dreamcast.DreamCastRepository
import com.example.myapplication.repository.video.ReleaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: ReleaseRepository,
    private val dreamCastRepository: DreamCastRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(mutableListOf<ReleasesElementResponse>())
    val uiState: StateFlow<MutableList<ReleasesElementResponse>> = _uiState.asStateFlow()
    private val _uiStateDreamCast = MutableStateFlow(mutableListOf<DreamCastReleaseElement>())
    val uiDreamCastState: StateFlow<MutableList<DreamCastReleaseElement>> = _uiStateDreamCast.asStateFlow()

    fun getDreamCastUrls(url: String) {
        viewModelScope.launch {
            dreamCastRepository.getVideoUrls(url)
        }
    }

    fun getDreamCastReleases(){
        viewModelScope.launch {
            dreamCastRepository.searchOnDreamCast("").let {
                _uiStateDreamCast.value = it.toMutableList()
            }
        }
    }

    fun getAnilibertyLastReleases(){
        viewModelScope.launch {
            repository.getReleases().let {
                _uiState.value = it.toMutableList()
            }
        }
    }
}