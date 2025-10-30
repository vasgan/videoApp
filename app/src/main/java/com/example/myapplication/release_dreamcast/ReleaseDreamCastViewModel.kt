package com.example.myapplication.release_dreamcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.history.History
import com.example.myapplication.data.parse.dreamcast.DreamCastDescriptionAndSeries
import com.example.myapplication.repository.dreamcast.DreamCastRepository
import com.example.myapplication.repository.history.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReleaseDreamCastViewModel @Inject constructor(
    val repository: DreamCastRepository,
    private val watchHistoryRepository: HistoryRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<DreamCastDescriptionAndSeries?> = MutableStateFlow(null)
    val uiState: StateFlow<DreamCastDescriptionAndSeries?> = _uiState.asStateFlow()

    private val _uiStateHistory: MutableStateFlow<History?> = MutableStateFlow(null)
    val uiStateHistory: StateFlow<History?> = _uiStateHistory.asStateFlow()

    fun getSeriesList(url: String) {
        viewModelScope.launch {
            _uiState.value = repository.getVideoUrls(url)
        }
    }

    fun getHistory(id: Int){
        viewModelScope.launch {
            _uiStateHistory.value = watchHistoryRepository.getHistoryRelease(id)
        }
    }
}