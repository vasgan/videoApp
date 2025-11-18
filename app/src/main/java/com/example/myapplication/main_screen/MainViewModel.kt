package com.example.myapplication.main_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.network.Error
import com.example.myapplication.data.network.Loading
import com.example.myapplication.data.network.Success
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.example.myapplication.repository.dreamcast.DreamCastRepository
import com.example.myapplication.repository.video.ReleaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: ReleaseRepository,
    private val dreamCastRepository: DreamCastRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiAnilibertyState?> = MutableStateFlow(null)
    val uiState: StateFlow<UiAnilibertyState?> = _uiState.asStateFlow()
    private val _uiStateDreamCast: MutableStateFlow<UiDreamCastState?> = MutableStateFlow(null)
    val uiDreamCastState: StateFlow<UiDreamCastState?> = _uiStateDreamCast.asStateFlow()

    private var loadAnilibria: Job? = null
    private var loadDreamCast: Job? = null
    private var pageAl = 1
    private var pageDC = 1

    init {
        getAnilibertyLastReleases()
        getDreamCastReleases()
    }
    /*  fun getDreamCastUrls(url: String) {
            viewModelScope.launch {
                dreamCastRepository.getVideoUrls(url)
            }
        }*/

    fun getDreamCastReleases() {
        if (loadDreamCast != null) return
        pageDC = 1
        loadDreamCast = dreamCastRepository
            .searchOnDreamCast("", pageDC)
            .onEach {
                _uiStateDreamCast.value = when (it) {
                    is Error -> {
                        loadDreamCast = null
                        ErrorDC()
                    }
                    is Loading -> LoadingFirstDC()
                    is Success -> {
                        loadDreamCast = null
                        CompleteLoadDreamCast(it.data)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun scrollEndDC() {
        if (loadDreamCast != null) return
        pageDC++
        loadDreamCast = dreamCastRepository
            .searchOnDreamCast("", pageDC)
            .onEach {
                _uiStateDreamCast.value = when (it) {
                    is Error -> {
                        loadDreamCast = null
                        ErrorDC()
                    }

                    is Loading -> LoadingNextDC(_uiStateDreamCast.value?.releases)
                    is Success -> {
                        loadDreamCast = null
                        _uiStateDreamCast.value?.releases?.plus( it.data)?.let { releases -> CompleteLoadDreamCast(releases) }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun getAnilibertyLastReleases() {
        if (loadAnilibria != null) return
        pageAl = 1
        _uiState.value?.releases?.let {
            if (it.isNotEmpty())
                _uiState.value = CompleteLoadAniliberty(it)
        }
        loadAnilibria = repository.getReleases(pageAl).onEach {
            _uiState.value = when(it) {
                is Error -> {
                    loadAnilibria = null
                    ErrorAl()
                }
                is Loading -> LoadingFirstAl()
                is Success -> {
                    loadAnilibria = null
                    CompleteLoadAniliberty(it.data)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun scrollEndAnilibria() {
        if (loadAnilibria != null) return
        pageAl++
        loadAnilibria = repository.getReleases(pageAl).onEach {
            _uiState.value = when(it) {
                is Error -> {
                    loadAnilibria = null
                    ErrorAl(_uiState.value?.releases)
                }
                is Loading -> LoadingNextAl(_uiState.value?.releases)
                is Success -> {
                    loadAnilibria = null
                    CompleteLoadAniliberty(_uiState.value?.releases?.plus(it.data))
                }
            }
        }.launchIn(viewModelScope)
    }
}

sealed class UiDreamCastState(val releases: List<DreamCastReleaseElement>? = null)

class LoadingFirstDC(releases: List<DreamCastReleaseElement>? = null) : UiDreamCastState(releases)
class CompleteLoadDreamCast(releases: List<DreamCastReleaseElement>) : UiDreamCastState(releases)
class ErrorDC(releases: List<DreamCastReleaseElement>? = null) : UiDreamCastState(releases)
class LoadingNextDC(releases: List<DreamCastReleaseElement>? = null) : UiDreamCastState(releases)

sealed class UiAnilibertyState(val releases: List<ReleasesElementResponse>? = null)

class LoadingFirstAl(releases: List<ReleasesElementResponse>? = null) : UiAnilibertyState(releases)
class ErrorAl(releases: List<ReleasesElementResponse>? = null) : UiAnilibertyState(releases)
class LoadingNextAl(releases: List<ReleasesElementResponse>?) : UiAnilibertyState(releases)
class CompleteLoadAniliberty(releases: List<ReleasesElementResponse>?) : UiAnilibertyState(releases)