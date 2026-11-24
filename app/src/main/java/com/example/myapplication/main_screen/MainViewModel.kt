package com.example.myapplication.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.history.History
import com.example.myapplication.data.ReleaseSource
import com.example.myapplication.data.mapper.toRelease
import com.example.myapplication.data.network.Error
import com.example.myapplication.data.network.Loading
import com.example.myapplication.data.network.NetworkError
import com.example.myapplication.data.network.Success
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.example.myapplication.data.parse.dreamcast.mapToRelease
import com.example.myapplication.repository.dreamcast.DreamCastRepository
import com.example.myapplication.repository.history.HistoryRepository
import com.example.myapplication.repository.video.ReleaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: ReleaseRepository,
    private val dreamCastRepository: DreamCastRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiAnilibertyState?> = MutableStateFlow(null)
    val uiState: StateFlow<UiAnilibertyState?> = _uiState.asStateFlow()
    private val _uiStateDreamCast: MutableStateFlow<UiDreamCastState?> = MutableStateFlow(null)
    val uiDreamCastState: StateFlow<UiDreamCastState?> = _uiStateDreamCast.asStateFlow()

    private val _uiStateLoadHistory: MutableStateFlow<History?> = MutableStateFlow(null)
    val uiStateHistory: StateFlow<History?> = _uiStateLoadHistory.asStateFlow()

    private val _uiStateDreamCastHistory: MutableStateFlow<History?> = MutableStateFlow(null)
    val uiStateDreamCastHistory: StateFlow<History?> = _uiStateDreamCastHistory.asStateFlow()

    private val _continueWatchingEvents = MutableSharedFlow<ContinueWatchingDestination>()
    val continueWatchingEvents: SharedFlow<ContinueWatchingDestination> = _continueWatchingEvents

    private var loadAniLiberty: Job? = null
    private var loadDreamCast: Job? = null
    private var pageAl = 1
    private var pageDC = 1

    init {
        getAnilibertyLastReleases()
        getDreamCastReleases()
        loadHistory()
        loadDreamCastHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiStateLoadHistory.value = historyRepository.getLastALRelease()
        }
    }

    fun loadDreamCastHistory() {
        viewModelScope.launch {
            _uiStateDreamCastHistory.value = historyRepository.getLastDCRelease()
        }
    }

    fun continueWatching() {
        val history = _uiStateLoadHistory.value ?: return
        repository.getRelease(history.releaseId.toString()).onEach { response ->
            when (response) {
                is Success -> {
                    val release = (response.data as FullReleaseResponse)
                        .toRelease(ReleaseSource.Aniliberty.value)
                    val episode = history.episodeNumber
                    if (episode in release.episodes.indices) {
                        _continueWatchingEvents.emit(
                            ContinueWatchingDestination(
                                episode = episode,
                                release = release,
                                source = ReleaseSource.Aniliberty.value
                            )
                        )
                    }
                }

                is Error -> Unit
                is Loading -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun continueWatchingDreamCast() {
        val history = _uiStateDreamCastHistory.value ?: return
        val url = history.url ?: return

        viewModelScope.launch {
            val dreamCastRelease = dreamCastRepository.getReleaseById(
                urlRelease = url,
                id = history.releaseId.toString()
            )
            val series = dreamCastRepository.getVideoUrls(url)
            val release = mapToRelease(series, dreamCastRelease)
            val episode = history.episodeNumber

            if (episode in release.episodes.indices) {
                _continueWatchingEvents.emit(
                    ContinueWatchingDestination(
                        episode = episode,
                        release = release,
                        source = ReleaseSource.DreamCast.value
                    )
                )
            }
        }
    }

    fun getDreamCastReleases() {
        if (loadDreamCast != null) return
        pageDC = 1
        loadOnDreamCast()
    }

    fun refreshDreamCast() {
        getDreamCastReleases()
        loadDreamCastHistory()
    }

    fun scrollEndDC() {
        if (loadDreamCast != null) return
        pageDC++
        loadOnDreamCast()
    }

    fun getAnilibertyLastReleases() {
        if (loadAniLiberty != null) return
        pageAl = 1
        loadOnAniliberty()
    }

    fun refreshAniliberty() {
        getAnilibertyLastReleases()
        loadHistory()
    }

    fun scrollEndAnilibria() {
        if (loadAniLiberty != null) return
        pageAl++
        loadOnAniliberty()
    }

    private fun loadOnAniliberty() {
        loadAniLiberty = repository.getReleases(pageAl).onEach {
            _uiState.value = when(it) {
                is Error -> {
                    loadAniLiberty = null
                    if (pageAl > 1)
                        ErrorAl(_uiState.value?.releases, it.networkError)
                    else ErrorAl(error = it.networkError)
                }
                is Loading ->
                    if (pageAl > 1)
                        LoadingNextAl(_uiState.value?.releases)
                    else LoadingFirstAl()
                is Success -> {
                    loadAniLiberty = null
                    if (pageAl > 1)
                    CompleteLoadAniliberty(_uiState.value?.releases?.plus(it.data))
                    else CompleteLoadAniliberty(it.data)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadOnDreamCast() {
        loadDreamCast = dreamCastRepository
            .searchOnDreamCast( pageNumber = pageDC)
            .onEach {
                _uiStateDreamCast.value = when (it) {
                    is Error -> {
                        loadDreamCast = null
                        if (pageDC > 1)
                            ErrorDC(_uiStateDreamCast.value?.releases, it.networkError)
                        else ErrorDC(error = it.networkError)
                    }

                    is Loading -> {
                        if (pageDC > 1)
                            LoadingNextDC(_uiStateDreamCast.value?.releases)
                        else
                            LoadingFirstDC()
                    }
                    is Success -> {
                        loadDreamCast = null
                        if (pageDC > 1)
                        _uiStateDreamCast.value?.releases?.plus( it.data)?.let { releases -> CompleteLoadDreamCast(releases) }
                        else
                            CompleteLoadDreamCast(it.data)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}

sealed class UiDreamCastState(val releases: List<DreamCastReleaseElement>? = null)

class LoadingFirstDC(releases: List<DreamCastReleaseElement>? = null) : UiDreamCastState(releases)
class CompleteLoadDreamCast(releases: List<DreamCastReleaseElement>) : UiDreamCastState(releases)
class ErrorDC(
    releases: List<DreamCastReleaseElement>? = null,
    val error: NetworkError
) : UiDreamCastState(releases)
class LoadingNextDC(releases: List<DreamCastReleaseElement>? = null) : UiDreamCastState(releases)

sealed class UiAnilibertyState(val releases: List<ReleasesElementResponse>? = null)

class LoadingFirstAl(releases: List<ReleasesElementResponse>? = null) : UiAnilibertyState(releases)
class ErrorAl(
    releases: List<ReleasesElementResponse>? = null,
    val error: NetworkError
) : UiAnilibertyState(releases)
class LoadingNextAl(releases: List<ReleasesElementResponse>?) : UiAnilibertyState(releases)
class CompleteLoadAniliberty(releases: List<ReleasesElementResponse>?) : UiAnilibertyState(releases)

data class ContinueWatchingDestination(
    val episode: Int,
    val release: Release,
    val source: String
)
