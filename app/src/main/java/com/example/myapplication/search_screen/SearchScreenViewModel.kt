package com.example.myapplication.search_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.network.Error
import com.example.myapplication.data.network.Loading
import com.example.myapplication.data.network.Success
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.release_screen.UiReleaseAniliberty
import com.example.myapplication.repository.dreamcast.DreamCastRepository
import com.example.myapplication.repository.video.ReleaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchScreenViewModel @Inject constructor(
    private val repository: ReleaseRepository,
    private val dreamCastRepository: DreamCastRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<UISearchAl?> = MutableStateFlow(null)
    val uiState: StateFlow<UISearchAl?> = _uiState.asStateFlow()
    private var filtersId = mutableListOf<Int>()

    private var searchJob: Job? = null

    fun getFilterReleases(filters: List<Int>) {
        filtersId = filters.toMutableList()
        repository.getSearchWithTextFilter(filtersId, "").onEach {
     //       _uiState.value = it.toMutableList()
        }.launchIn(viewModelScope)
    }

    fun searchWithFilter(searchText: String) {
        if (searchText.length > 3) {
            searchJob?.cancel()
            searchJob =
                repository.getSearchWithTextFilter(filtersId, searchText).onEach {
                    when(it) {
                        is Error<*> -> {
                        }
                        is Loading<*> -> {
                            _uiState.value = UISearchAl.LoadingUISearchAl()
                        }
                        is Success<*> -> {
                            _uiState.value = UISearchAl.CompleteUISearchAl(it.data as List<ReleasesElementResponse>?)
                        }
                    }
            }.launchIn(viewModelScope)
        }
    }

    fun searchDreamCast(searchText: String) {
        viewModelScope.launch {
            dreamCastRepository.searchOnDreamCast(searchText)
        }
    }
}

sealed class UISearchAl(val releases: List<ReleasesElementResponse>?) {
    class LoadingUISearchAl(releases: List<ReleasesElementResponse>? = null) : UISearchAl(releases)
    class CompleteUISearchAl(releases: List<ReleasesElementResponse>?) : UISearchAl(releases)
    class ErrorUISearchAl(releases: List<ReleasesElementResponse>?) : UISearchAl(releases)
}