package com.example.myapplication.release_dreamcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.history.History
import com.example.myapplication.data.ReleaseSource
import com.example.myapplication.data.parse.dreamcast.DreamCastDescriptionAndSeries
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.example.myapplication.repository.dreamcast.DreamCastRepository
import com.example.myapplication.repository.favorite.FavoriteRepository
import com.example.myapplication.repository.history.HistoryRepository
import com.example.myapplication.repository.watch_list.WatchListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReleaseDreamCastViewModel @Inject constructor(
    val repository: DreamCastRepository,
    private val watchHistoryRepository: HistoryRepository,
    private val favoriteRepository: FavoriteRepository,
    private val watchListRepository: WatchListRepository,
) : ViewModel() {

    private val _detailState = MutableStateFlow<DreamCastDetailState>(DreamCastDetailState.Loading)
    val detailState: StateFlow<DreamCastDetailState> = _detailState.asStateFlow()

    //Избранное
    private val _uiStateFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val favoriteUiState: StateFlow<Boolean> = _uiStateFavorite.asStateFlow()

    private val _uiStateWatchList: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val watchListUiState: StateFlow<Boolean> = _uiStateWatchList.asStateFlow()

    fun initFavorite(id: Int) {
        favoriteRepository.getFavorite(id).onEach {
            _uiStateFavorite.value = (it.id > 0)
        }.launchIn(viewModelScope)
    }

    fun initWatchList(id: Int) {
        viewModelScope.launch {
            _uiStateWatchList.value = watchListRepository.getWatchItem(id, ReleaseSource.DreamCast.value) != null
        }
    }

    fun loadDetails(
        releaseUrl: String?,
        releaseId: String?,
        releaseFromNavigation: DreamCastReleaseElement?
    ) {
        if (releaseUrl.isNullOrBlank() || releaseId.isNullOrBlank()) {
            _detailState.value = DreamCastDetailState.Error
            return
        }

        viewModelScope.launch {
            _detailState.value = DreamCastDetailState.Loading
            try {
                val release = releaseFromNavigation
                    ?: repository.getReleaseById(releaseUrl, releaseId)
                val id = release.id.toIntOrNull() ?: error("Invalid DreamCast release ID")
                val series = repository.getVideoUrls(releaseUrl)
                val history = watchHistoryRepository
                    .getHistoryRelease(id, ReleaseSource.DreamCast.value)
                    ?.firstOrNull()

                _detailState.value = DreamCastDetailState.Content(release, series, history)
                initFavorite(id)
                initWatchList(id)
            } catch (_: Exception) {
                _detailState.value = DreamCastDetailState.Error
            }
        }
    }

    fun changeFavorite(dreamCastRelease: DreamCastReleaseElement?) {
        viewModelScope.launch {
            if (!_uiStateFavorite.value) {
                dreamCastRelease?.id?.let {
                    favoriteRepository.addFavorite(
                        it.toInt(),
                        dreamCastRelease.russian,
                        dreamCastRelease.image,
                        ReleaseSource.DreamCast.value,
                        dreamCastRelease.url)
                }
                _uiStateFavorite.value = true
            } else {
                dreamCastRelease?.id?.let { favoriteRepository.removeFavorite(it.toInt()) }
                _uiStateFavorite.value = false
            }
        }
    }

    fun changeWatchList(dreamCastRelease: DreamCastReleaseElement?) {
        viewModelScope.launch {
            val id = dreamCastRelease?.id?.toInt()
            if (id != null) {
                if (!_uiStateWatchList.value) {
                    watchListRepository.addToWatchList(
                        id,
                        dreamCastRelease.russian,
                        dreamCastRelease.image,
                        ReleaseSource.DreamCast.value,
                        dreamCastRelease.url
                    )
                    _uiStateWatchList.value = true
                } else {
                    watchListRepository.removeFromWatchList(id, ReleaseSource.DreamCast.value)
                    _uiStateWatchList.value = false
                }
            }
        }
    }
}

sealed interface DreamCastDetailState {
    data object Loading : DreamCastDetailState
    data object Error : DreamCastDetailState
    data class Content(
        val release: DreamCastReleaseElement,
        val series: DreamCastDescriptionAndSeries,
        val history: History?
    ) : DreamCastDetailState
}
