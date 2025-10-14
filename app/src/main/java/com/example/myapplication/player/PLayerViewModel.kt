package com.example.myapplication.player

import android.util.Log
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
    private val _uiStateHideViews: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val uiStateHideViews: StateFlow<Boolean> = _uiStateHideViews.asStateFlow()

    private val _uiPLayerState: MutableStateFlow<PlayerState> = MutableStateFlow(PlayerState.OnPlay())
    val playerState: StateFlow<PlayerState> = _uiPLayerState.asStateFlow()

    private val _uiDraggingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val draggingState: StateFlow<Boolean> = _uiDraggingState.asStateFlow()

    private val _uiStateShowCurrentTime: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val uiStateShowCurrentTime: StateFlow<Boolean> = _uiStateShowCurrentTime.asStateFlow()

    private val _uiStatePlayerSeek: MutableStateFlow<SeekToState> = MutableStateFlow(SeekToState.OnSeekEnd())
    val uiStatePlayerSeek: StateFlow<SeekToState> = _uiStatePlayerSeek.asStateFlow()


    private val _uiStatePosition: MutableStateFlow<Long> = MutableStateFlow(0L)
    val uiStatePosition: StateFlow<Long> = _uiStatePosition.asStateFlow()

    private val _uiStateSupportView: MutableStateFlow<SupportView> = MutableStateFlow(SupportView.OnShowEnd())
    val uiStateSpeedX2: StateFlow<SupportView> = _uiStateSupportView.asStateFlow()


    private var searchJob: Job? = null
    fun outClick() {
        searchJob?.cancel()
        _uiStateHideViews.value = !_uiStateHideViews.value
    }

    fun hideViews() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(10_000)
            Log.e("ololololo", "call after delay")
            _uiStateHideViews.value = !_uiStateHideViews.value
        }
    }

    fun fastSpeed() {
        _uiStateSupportView.value = SupportView.OnSpeedX2()
    }

    fun endShowSupportView() {
        _uiStateSupportView.value = SupportView.OnShowEnd()
    }

    fun showForward() {
        _uiStateSupportView.value = SupportView.OnSkepForward()
    }

    fun showBackward() {
        _uiStateSupportView.value = SupportView.OnSkepBackWard()
    }

    fun showCurrentTime() {
        hideViews()
        _uiStateShowCurrentTime.value = !_uiStateShowCurrentTime.value
    }

    fun dragging() {
        _uiDraggingState.value = true
        searchJob?.cancel()
    }

    fun draggingEnd() {
        _uiDraggingState.value = false
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
        _uiStatePlayerSeek.value = SeekToState.OnNext()
    }

    fun onPreviousClick() {
        hideViews()
        _uiStatePlayerSeek.value = SeekToState.OnPrevious()
    }


    fun changePosition(position: Long) {
        _uiStatePosition.value = position
    }

    fun seekEnd() {
        _uiStatePlayerSeek.value = SeekToState.OnSeekEnd()
    }
}

sealed class PlayerState {
    class OnPause(): PlayerState()
    class OnPlay(): PlayerState()
}


sealed class SupportView {
    class OnSpeedX2() : SupportView()
    class OnSkepForward() : SupportView()
    class OnSkepBackWard(): SupportView()
    class OnShowEnd(): SupportView()
}

sealed class SeekToState {
    class OnNext() : SeekToState()
    class OnPrevious() : SeekToState()
    class OnSeekEnd(): SeekToState()
}