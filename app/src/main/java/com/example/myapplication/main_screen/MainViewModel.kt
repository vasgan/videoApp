package com.example.myapplication.main_screen

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

    fun getDreamCastReleases() {
        if (loadDreamCast != null) return
        pageDC = 1
        loadOnDreamCast()
    }

    fun scrollEndDC() {
        if (loadDreamCast != null) return
        pageDC++
        loadOnDreamCast()
    }

    fun getAnilibertyLastReleases() {
        if (loadAnilibria != null) return
        pageAl = 1
        loadOnAnilibria()
    }

    fun scrollEndAnilibria() {
        if (loadAnilibria != null) return
        pageAl++
        loadOnAnilibria()
    }

    private fun loadOnAnilibria() {
        loadAnilibria = repository.getReleases(pageAl).onEach {
            _uiState.value = when(it) {
                is Error -> {
                    loadAnilibria = null
                    if (pageAl > 1)
                        ErrorAl(_uiState.value?.releases)
                    else ErrorAl()
                }
                is Loading ->
                    if (pageAl > 1)
                        LoadingNextAl(_uiState.value?.releases)
                    else LoadingFirstAl()
                is Success -> {
                    loadAnilibria = null
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
                        ErrorDC()
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
class ErrorDC(releases: List<DreamCastReleaseElement>? = null) : UiDreamCastState(releases)
class LoadingNextDC(releases: List<DreamCastReleaseElement>? = null) : UiDreamCastState(releases)

sealed class UiAnilibertyState(val releases: List<ReleasesElementResponse>? = null)

class LoadingFirstAl(releases: List<ReleasesElementResponse>? = null) : UiAnilibertyState(releases)
class ErrorAl(releases: List<ReleasesElementResponse>? = null) : UiAnilibertyState(releases)
class LoadingNextAl(releases: List<ReleasesElementResponse>?) : UiAnilibertyState(releases)
class CompleteLoadAniliberty(releases: List<ReleasesElementResponse>?) : UiAnilibertyState(releases)