package com.example.myapplication.release_dreamcast

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.history.History
import com.example.myapplication.data.db.history.History.Companion.DREAM_CAST
import com.example.myapplication.data.parse.dreamcast.DreamCastDescriptionAndSeries
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.example.myapplication.repository.dreamcast.DreamCastRepository
import com.example.myapplication.repository.favorite.FavoriteRepository
import com.example.myapplication.repository.history.HistoryRepository
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
) : ViewModel() {

    private val _uiState: MutableStateFlow<DreamCastDescriptionAndSeries?> = MutableStateFlow(null)
    val uiState: StateFlow<DreamCastDescriptionAndSeries?> = _uiState.asStateFlow()

    private val _uiStateHistory: MutableStateFlow<History?> = MutableStateFlow(null)
    val uiStateHistory: StateFlow<History?> = _uiStateHistory.asStateFlow()

    //Избранное
    private val _uiStateFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val favoriteUiState: StateFlow<Boolean> = _uiStateFavorite.asStateFlow()

    //Информация о релизе
    private val _uiStateRelease: MutableStateFlow<DreamCastReleaseElement?> = MutableStateFlow(null)
    val uiStateRelease: StateFlow<DreamCastReleaseElement?> = _uiStateRelease.asStateFlow()

    fun initRelease(url: String, id: String) {
        viewModelScope.launch  {
            _uiStateRelease.value = repository.getReleaseById(url, id)
        }
    }

    fun initFavorite(id: Int) {
        favoriteRepository.getFavorite(id).onEach {
            _uiStateFavorite.value = (it.id > 0)
        }.launchIn(viewModelScope)
    }

    fun getSeriesList(url: String) {
        viewModelScope.launch {
            _uiState.value = repository.getVideoUrls(url)
        }
    }

    fun getHistory(id: Int) {
        viewModelScope.launch {
            // todo test it
            watchHistoryRepository.getHistoryRelease(id)?.let {
                if (it.isNotEmpty())
                    _uiStateHistory.value = it[0]
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
                        DREAM_CAST,
                        dreamCastRelease.url)
                }
                _uiStateFavorite.value = true
            } else {
                dreamCastRelease?.id?.let { favoriteRepository.removeFavorite(it.toInt()) }
                _uiStateFavorite.value = false
            }
        }
    }
}