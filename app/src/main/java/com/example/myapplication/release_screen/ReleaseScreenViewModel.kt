package com.example.myapplication.release_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.favorite.Favorite
import com.example.myapplication.data.db.history.History
import com.example.myapplication.data.network.Error
import com.example.myapplication.data.network.Loading
import com.example.myapplication.data.network.Success
import com.example.myapplication.data.mapper.toRelease
import com.example.myapplication.data.network.responses.BaseFranchiseResponse
import com.example.myapplication.data.network.responses.FranchiseResponse
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.ui_data.Franchise
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.repository.favorite.FavoriteRepository
import com.example.myapplication.repository.history.HistoryRepository
import com.example.myapplication.repository.video.ReleaseRepository
import com.example.myapplication.repository.watch_list.WatchListRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class ReleaseScreenViewModel @Inject constructor(
    private val repository: ReleaseRepository,
    private val favoriteRepository: FavoriteRepository,
    private val watchListRepository: WatchListRepository,
    private val watchHistoryRepository: HistoryRepository
) : ViewModel() {

    //Парсинг релиза
    private val _uiStateRelease: MutableStateFlow<UiReleaseAniliberty?> = MutableStateFlow(null)
    val uiStateRelease: StateFlow<UiReleaseAniliberty?> = _uiStateRelease.asStateFlow()

    //Парсинг франчайза
    private val _uiStateFranchise: MutableStateFlow<UiFranchiseAniliberty?> = MutableStateFlow(null)
    val uiStateFranchise: StateFlow<UiFranchiseAniliberty?> = _uiStateFranchise.asStateFlow()

    // Список на просмотр
    private val _uiStateWatchList: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val watchListUIState: StateFlow<Boolean> = _uiStateWatchList.asStateFlow()

    //Избранное
    private val _uiStateFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val favoriteUiState: StateFlow<Boolean> = _uiStateFavorite.asStateFlow()

    private val _uiStateHistory: MutableStateFlow<History?> = MutableStateFlow(null)
    val uiStateHistory: StateFlow<History?> = _uiStateHistory.asStateFlow()

    fun getCurrentRelease(id: String, doubbers: String) {
        getHistory(id.toInt(), doubbers)
        repository.getRelease(id).onEach {
            when (it) {
                is Error<*> -> {
                    _uiStateRelease.value = UiReleaseAniliberty.ErrorUiReleaseAniliberty()
                }

                is Loading<*> -> {
                    _uiStateRelease.value = UiReleaseAniliberty.LoadingUiReleaseAniliberty()
                }

                is Success<*> -> {
                    _uiStateRelease.value =
                        UiReleaseAniliberty.CompleteUiReleaseAniliberty(
                            (it.data as FullReleaseResponse).toRelease(doubbers)
                        )
                }
            }
        }.launchIn(viewModelScope)

        repository.getFranchises(id).onEach {
            when (it) {
                is Error<*> -> {
                    _uiStateFranchise.value = UiFranchiseAniliberty.ErrorUiFranchiseAniliberty()
                }

                is Loading<*> -> {
                    _uiStateFranchise.value = UiFranchiseAniliberty.LoadingUiFranchiseAniliberty()
                }

                is Success<*> -> {
                    if (it.data is ArrayList<*>) {
                        if (it.data.isNotEmpty()) {
                            _uiStateFranchise.value =
                                UiFranchiseAniliberty.CompleteUiFranchiseAniliberty(
                                    mapFranchise(it.data[0] as BaseFranchiseResponse)
                                )
                        } else
                            _uiStateFranchise.value =
                                UiFranchiseAniliberty.CompleteUiFranchiseAniliberty(emptyList())
                    } else
                        _uiStateFranchise.value =
                            UiFranchiseAniliberty.CompleteUiFranchiseAniliberty(emptyList())
                }
            }
        }.launchIn(viewModelScope)

        favoriteRepository.getFavorite(id.toInt()).onEach {
            _uiStateFavorite.value = (it.id > 0)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            _uiStateWatchList.value = watchListRepository.getWatchItem(id.toInt(), doubbers) != null
        }
    }

    private fun getHistory(id: Int, source: String) {
        viewModelScope.launch {
            watchHistoryRepository.getHistoryRelease(id, source)?.let {
                if (it.isNotEmpty())
                _uiStateHistory.value = it[0]
            }
        }
    }

    private fun mapFranchise(franchise: BaseFranchiseResponse?): List<Franchise> {
        val value = mutableListOf<Franchise>()
        franchise?.franchiseReleases?.forEach {
            value.add(
                Franchise(
                    it.id ?: "",
                    it.sortOrder ?: 0,
                    it.releaseId ?: "",
                    it.franchiseId ?: "",
                    it.release?.name?.main ?: ""
                )
            )
        }
        return value
    }

    fun changeFavorite() {
        viewModelScope.launch {
            if (_uiStateRelease.value is UiReleaseAniliberty.CompleteUiReleaseAniliberty) {
                val id = _uiStateRelease.value?.release?.id?.toInt()
                val name = _uiStateRelease.value?.release?.name
                val poster = _uiStateRelease.value?.release?.poster
                val doubers = _uiStateRelease.value?.release?.doubbers
                if (id != null && name != null && poster != null && doubers != null)
                    if (!_uiStateFavorite.value) {
                        favoriteRepository.addFavorite(id, name, poster, doubers)
                        _uiStateFavorite.value = true
                    } else {
                        favoriteRepository.removeFavorite(id)
                        _uiStateFavorite.value = false
                    }
            } else return@launch
        }
    }

    fun changeWatchList() {
        viewModelScope.launch {
            val id = _uiStateRelease.value?.release?.id?.toInt()
            val name = _uiStateRelease.value?.release?.name
            val poster = _uiStateRelease.value?.release?.poster
            val doubers = _uiStateRelease.value?.release?.doubbers
            val url = _uiStateRelease.value?.release?.url
            if (id != null && name != null && poster != null && doubers != null)
                if (!_uiStateWatchList.value) {
                    watchListRepository.addToWatchList(id, name, poster, doubers, url)
                    _uiStateWatchList.value = true
                } else {
                    watchListRepository.removeFromWatchList(id, doubers)
                    _uiStateWatchList.value = false
                }
        }
    }
}

sealed class UiReleaseAniliberty(val release: Release?) {
    class LoadingUiReleaseAniliberty(releases: Release? = null) : UiReleaseAniliberty(releases)
    class CompleteUiReleaseAniliberty(releases: Release?) : UiReleaseAniliberty(releases)
    class ErrorUiReleaseAniliberty(releases: Release? = null) : UiReleaseAniliberty(releases)
}

sealed class UiFranchiseAniliberty(val franchise: List<Franchise>?) {
    class LoadingUiFranchiseAniliberty(franchise: List<Franchise>? = null) : UiFranchiseAniliberty(franchise)
    class CompleteUiFranchiseAniliberty(franchise: List<Franchise>?) : UiFranchiseAniliberty(franchise)
    class ErrorUiFranchiseAniliberty(franchise: List<Franchise>? = null) : UiFranchiseAniliberty(franchise)
}
