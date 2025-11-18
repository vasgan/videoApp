package com.example.myapplication.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.history.History
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.repository.favorite.FavoriteRepository
import com.example.myapplication.repository.history.HistoryRepository
import com.example.myapplication.repository.video.ReleaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PLayerViewModel @Inject constructor(
    private val repository: ReleaseRepository,
    private val favoriteRepository: FavoriteRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    private val _uiStateHideViews: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val uiStateHideViews: StateFlow<Boolean> = _uiStateHideViews.asStateFlow()

    private val _uiPLayerState: MutableStateFlow<PlayerState> =
        MutableStateFlow(PlayerState.OnPlay())
    val playerState: StateFlow<PlayerState> = _uiPLayerState.asStateFlow()

    private val _uiDraggingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val draggingState: StateFlow<Boolean> = _uiDraggingState.asStateFlow()

    private val _uiStateShowCurrentTime: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiStateShowCurrentTime: StateFlow<Boolean> = _uiStateShowCurrentTime.asStateFlow()

    private val _uiStatePlayerSeek: MutableStateFlow<SeekToState> =
        MutableStateFlow(SeekToState.OnSeekEnd())
    val uiStatePlayerSeek: StateFlow<SeekToState> = _uiStatePlayerSeek.asStateFlow()

    private val _uiStatePosition: MutableStateFlow<Long> = MutableStateFlow(0L)
    val uiStatePosition: StateFlow<Long> = _uiStatePosition.asStateFlow()

    private val _uiStateSupportView: MutableStateFlow<SupportView> =
        MutableStateFlow(SupportView.OnShowEnd())
    val uiStateSpeedX2: StateFlow<SupportView> = _uiStateSupportView.asStateFlow()

    private val _uiStateEpisode: MutableStateFlow<Int> = MutableStateFlow(0)
    val uiStateEpisode: StateFlow<Int> = _uiStateEpisode.asStateFlow()

    private val _uiStateRelease: MutableStateFlow<Release?> = MutableStateFlow(null)
    val uiStateRelease: StateFlow<Release?> = _uiStateRelease.asStateFlow()

    private val _uiStateFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val favoriteUiState: StateFlow<Boolean> = _uiStateFavorite.asStateFlow()

    private val _uiStateHistory: MutableStateFlow<History?> = MutableStateFlow(null)
    val uiStateHistory: StateFlow<History?> =_uiStateHistory.asStateFlow()

    private val _uiStateShowOpening: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiStateShowOpening: StateFlow<Boolean> =_uiStateShowOpening.asStateFlow()

    private val _uiStateShowEnding: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiStateShowEnding: StateFlow<Boolean> =_uiStateShowEnding.asStateFlow()

    private var hideViewsJob: Job? = null

    fun initViewModel(episode: Int, release: Release) {
        _uiStateEpisode.value = episode
        _uiStateRelease.value = release
        viewModelScope.launch {//todo переписать на флоу
            if (favoriteRepository.getFavorite(release.id.toInt()) != null)
                _uiStateFavorite.value = true
        }
        viewModelScope.launch {
            _uiStateHistory.value = historyRepository.getHistoryRelease(release.id.toInt())
        }
    }

    fun outClick() {
        hideViewsJob?.cancel()
        _uiStateHideViews.value = !_uiStateHideViews.value
    }

    fun urgentlyHideViews() {
        hideViewsJob?.cancel()
        _uiStateHideViews.value = !_uiStateHideViews.value
    }

    fun hideViews() {
        hideViewsJob?.cancel()
        hideViewsJob = viewModelScope.launch {
            delay(10_000)
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
        hideViewsJob?.cancel()
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

    fun onChangeEpisode(episodeNum: Int) {
        hideViews()
        _uiStateEpisode.value = episodeNum
    }

    fun onSeekBackward() {
        _uiStatePlayerSeek.value = SeekToState.OnPrevious()
    }

    fun onSeekForward() {
        _uiStatePlayerSeek.value = SeekToState.OnNext()
    }

    fun updateHistory(position: Long, doubers: String) {
        viewModelScope.launch {
            historyRepository.addToHistory(
                History(
                    uiStateRelease.value?.id?.toInt() ?: 0,
                    doubers,
                    uiStateRelease.value?.name ?: "",
                    uiStateEpisode.value.toString(),
                    uiStateEpisode.value,
                    position,
                    System.currentTimeMillis(),
                    uiStateRelease.value?.poster.toString()
                )
            )
        }
    }

    fun changePosition(position: Long) {
        _uiStatePosition.value = position
        if (uiStateRelease.value?.episodes[uiStateEpisode.value]?.opening?.stop?.let { position/1000 > it } == true &&
            uiStateRelease.value?.episodes[uiStateEpisode.value]?.opening?.end?.let { position/1000 < it } == true
        ) _uiStateShowOpening.value = true else {
            if (_uiStateShowOpening.value) _uiStateShowOpening.value = false
        }

        _uiStatePosition.value = position
        if (uiStateRelease.value?.episodes[uiStateEpisode.value]?.ending?.stop?.let { position/1000 > it } == true &&
            uiStateRelease.value?.episodes[uiStateEpisode.value]?.ending?.end?.let { position/1000 < it } == true
        ) _uiStateShowEnding.value = true else {
            if (_uiStateShowEnding.value) _uiStateShowEnding.value = false
        }
    }

    fun seekEnd() {
        _uiStatePlayerSeek.value = SeekToState.OnSeekEnd()
    }

    fun onFavoriteClick() {
        viewModelScope.launch {
            val id = _uiStateRelease.value?.id?.toInt()
            val name = _uiStateRelease.value?.name
            val imageUrl = _uiStateRelease.value?.poster
            if (id != null && name != null && imageUrl != null)
                if (!_uiStateFavorite.value) {
                    favoriteRepository.addFavorite(id, name, imageUrl)
                    _uiStateFavorite.value = true
                } else {
                    favoriteRepository.removeFavorite(id)
                    _uiStateFavorite.value = false
                }
        }
    }
}

sealed class PlayerState {
    class OnPause() : PlayerState()
    class OnPlay() : PlayerState()
}


sealed class SupportView {
    class OnSpeedX2() : SupportView()
    class OnSkepForward() : SupportView()
    class OnSkepBackWard() : SupportView()
    class OnShowEnd() : SupportView()
}

sealed class SeekToState {
    class OnNext() : SeekToState()
    class OnPrevious() : SeekToState()
    class OnSeekEnd() : SeekToState()
}