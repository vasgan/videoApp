package com.example.myapplication.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.video.ReleaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PLayerViewModel  @Inject constructor(
    private val repository: ReleaseRepository,
): ViewModel() {
    private val _uiStateHideViews: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiStateHideViews: StateFlow<Boolean> = _uiStateHideViews.asStateFlow()
    private val _uiPLayerState: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState.OnPlay())
    val playerState: StateFlow<PlayerState> = _uiPLayerState.asStateFlow()
    private var searchJob: Job? = null
    private val _uiDraggingState: MutableStateFlow<Dragging> = MutableStateFlow(Dragging.OnDraggingEnd())
    val draggingState: StateFlow<Dragging> = _uiDraggingState.asStateFlow()

    fun outClick() {
        searchJob?.cancel()
        _uiStateHideViews.value = !_uiStateHideViews.value
    }

    fun hideViews() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(10_000)
            _uiStateHideViews.value = true
        }
    }

    fun dragging() {
        hideViews()
    }

    fun draggingEnd() {
        hideViews()
    }

    fun pauseClick() {
        hideViews()
        _uiPLayerState.value = PlayerState.OnPause()
    }

    fun playClick() {
        hideViews()
        _uiPLayerState.value = PlayerState.OnPlay()
    }

    fun onNextClick() {
        hideViews()
        _uiPLayerState.value = PlayerState.OnNext()
    }

    fun onPreviousClick() {
        hideViews()
        _uiPLayerState.value = PlayerState.OnPrevious()
    }
}

sealed class PlayerState {
    class OnPause(): PlayerState()
    class OnPlay(): PlayerState()
    class OnNext(): PlayerState()
    class OnPrevious(): PlayerState()
}

sealed class Dragging {
    class OnDraggingStart() : Dragging()
    class OnDraggingEnd(): Dragging()
}