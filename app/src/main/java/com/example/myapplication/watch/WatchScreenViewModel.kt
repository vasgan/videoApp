package com.example.myapplication.watch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.watch_list.WatchListItem
import com.example.myapplication.repository.watch_list.WatchListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class WatchScreenViewModel @Inject constructor(
    private val watchRepository: WatchListRepository
) : ViewModel() {
    private val _uiStateWatchList: MutableStateFlow<List<WatchListItem>?> = MutableStateFlow(null)
    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val watchListUiState: StateFlow<List<WatchListItem>?> = _uiStateWatchList.asStateFlow()
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadWatchList(showRefreshing = false)
    }

    fun refresh() {
        loadWatchList(showRefreshing = true)
    }

    private fun loadWatchList(showRefreshing: Boolean) {
        viewModelScope.launch {
            if (showRefreshing) _isRefreshing.value = true
            try {
                val watchItems = watchRepository.getWatchItems()
                _uiStateWatchList.value = watchItems
                if (showRefreshing) delay(400)
            } finally {
                if (showRefreshing) _isRefreshing.value = false
            }
        }
    }
}
