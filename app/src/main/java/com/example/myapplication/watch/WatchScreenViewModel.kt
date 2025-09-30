package com.example.myapplication.watch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.watch_list.WatchListItem
import com.example.myapplication.repository.watch_list.WatchListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class WatchScreenViewModel @Inject constructor(
    private val watchRepository: WatchListRepository
) : ViewModel() {
    private val _uiStateWatchList: MutableStateFlow<List<WatchListItem>?> = MutableStateFlow(null)
    val watchListUiState: StateFlow<List<WatchListItem>?> = _uiStateWatchList.asStateFlow()

    fun getWatchRelease() {
        viewModelScope.launch {
            val watchItems = watchRepository.getWatchItems()
            _uiStateWatchList.value = watchItems
        }
    }
}