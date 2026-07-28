package com.example.myapplication.history_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.history.History
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.repository.history.HistoryRepository
import com.example.myapplication.repository.video.ReleaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val releaseRepository: ReleaseRepository,
    private val historyRepository: HistoryRepository
) : ViewModel()  {
    private val _uiState: MutableStateFlow<Release?> = MutableStateFlow(null)
    private val _uiHistory: MutableStateFlow<List<History>?> = MutableStateFlow(null)
    val uiState: StateFlow<Release?> = _uiState.asStateFlow()
    val historyUiState: StateFlow<List<History>?> = _uiHistory.asStateFlow()

    init {
        viewModelScope.launch {
            val histories = historyRepository.getHistory()
            _uiHistory.value = histories
        }
    }
}