package com.example.myapplication.search_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import com.example.myapplication.repository.dreamcast.DreamCastRepository
import com.example.myapplication.repository.video.ReleaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchScreenViewModel @Inject constructor(
    private val repository: ReleaseRepository,
    private val dreamCastRepository: DreamCastRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(mutableListOf<ReleasesElementResponse>())
    val uiState: StateFlow<MutableList<ReleasesElementResponse>> = _uiState.asStateFlow()
    private var filtersId = mutableListOf<Int>()

    private var searchJob: Job? = null

    fun getFilterReleases(filters: List<Int>){
        filtersId = filters.toMutableList()
        viewModelScope.launch {
            repository.getSearchWithTextFilter(filtersId, "").let {
                _uiState.value = it.toMutableList()
            }
        }
    }

    fun searchWithFilter(searchText: String){
        if (searchText.length > 3) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                repository.getSearchWithTextFilter(filtersId, searchText).let{
                    _uiState.value = it.toMutableList()
                }
            }
        }
    }

    fun searchDreamCast(searchText: String) {
        viewModelScope.launch {
            dreamCastRepository.searchOnDreamCast(searchText)
        }
    }
}