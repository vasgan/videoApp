package com.example.myapplication.player

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import com.example.myapplication.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.ui.PlayerView
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.ui.findActivity
import com.example.myapplication.ui.setFullscreen
import com.example.myapplication.ui.theme.PlayerColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@SuppressLint("ResourceAsColor")
@Composable
fun PlayerScreen(
    episodeCount: Int,
    viewModel: PLayerViewModel,
    releaseBase: Release,
    doubers: String,
    onBackClick: () -> Unit,
    PipCallback: () -> Unit,
    ConnectController: (onReady: (MediaController) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    activity?.setFullscreen(true)
    val scope = rememberCoroutineScope()
    var duration by remember { mutableLongStateOf(0L) }
    var draggingPosition by remember { mutableLongStateOf(0L) }
    var showSeriesList by remember { mutableStateOf(false) }
    var showChangeQuality by remember { mutableStateOf(false) }
    var secondsSeek by remember { mutableLongStateOf(0L) }
    val release by viewModel.uiStateRelease.collectAsState()
    val episodeNumber by viewModel.uiStateEpisode.collectAsState()
    val isShowCurrentTime by viewModel.uiStateShowCurrentTime.collectAsState()
    val showSupportView by viewModel.uiStateSpeedX2.collectAsState()
    val seekTo by viewModel.uiStatePlayerSeek.collectAsState()
    val position by viewModel.uiStatePosition.collectAsState()
    val visible by viewModel.uiStateHideViews.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsState()
    val dragging by viewModel.draggingState.collectAsState()
    val isFavorite by viewModel.favoriteUiState.collectAsState()
    val uiStateShowOpening by viewModel.uiStateShowOpening.collectAsState()
    val uiStateShowEnding by viewModel.uiStateShowEnding.collectAsState()
    val uiStateHistory by viewModel.uiStateHistoryPosition.collectAsState()
    var started by rememberSaveable { mutableStateOf(false) }
    var control by remember { mutableStateOf<MediaController?>(null) }

    LaunchedEffect(Unit) {
        viewModel.initViewModel(episodeCount, releaseBase, doubers)
        ConnectController { controller ->
            control = controller
        }
    }

    LaunchedEffect(control, uiStateHistory) {
        if (control == null || uiStateHistory == null) return@LaunchedEffect
        if (started) return@LaunchedEffect
        started = true
        viewModel.getCurrentEpisodeParam().let {
            control?.setMediaItem(MediaItem.fromUri(it.url), uiStateHistory!!)
        }
        control?.prepare()
        control?.playWhenReady = true
    }

    if (control != null) {
        LaunchedEffect(control) {
            while (isActive) {
                control?.let { viewModel.changePosition(it.currentPosition) }
                control?.let { duration = it.duration }
                delay(500)
            }
        }
        fun changeEpisode(episodeNum: Int) {
            control?.currentPosition?.let {
                viewModel.onChangeEpisode(episodeNum, it)
            }
            viewModel.getCurrentEpisodeParam().let {
                control?.setMediaItem(MediaItem.fromUri(it.url), it.timeStart)
            }
            control?.prepare()
            control?.playWhenReady = true
            showSeriesList = false
        }

        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = {
                PlayerView(it).apply {
                    useController = false
                    setBackgroundColor(context.getColor(R.color.black))
                }
            },
            update = { view ->
                if (control !== null) {
                    view.player = control
                }
                view.useController = false
            }
        )

        DisposableEffect(control) {
            control!!.addListener(object : Player.Listener {
                private var wasSeek = false
                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                        wasSeek = true
                        viewModel.updateHistory(control!!.currentPosition)
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (wasSeek && isPlaying) {
                        wasSeek = false
                        viewModel.draggingEnd()
                    }
                    viewModel.updateHistory(control!!.currentPosition)
                }
            })
            val window = (context as Activity).window
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            onDispose {
                viewModel.updateHistory(control!!.currentPosition)
                control?.run {
                    pause()
                    stop()
                    clearMediaItems()
                }
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                activity?.setFullscreen(false)
            }
        }

        var jobMoving: Job? = null
        Box(
            Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { offset ->
                            if (offset.x > size.width / 2f) {
                                jobMoving?.cancel()
                                jobMoving = scope.launch {
                                    viewModel.showForward()
                                    secondsSeek += 10
                                    delay(500)
                                    viewModel.onSeekForward()
                                    viewModel.endShowSupportView()
                                }
                            } else {
                                jobMoving?.cancel()
                                jobMoving = scope.launch {
                                    viewModel.showBackward()
                                    secondsSeek -= 10
                                    delay(500)
                                    viewModel.onSeekBackward()
                                    viewModel.endShowSupportView()
                                }
                            }
                        },
                        onTap = {
                            viewModel.outClick()
                        },
                        onPress = {
                            var weStart = false
                            val job = scope.launch {
                                delay(500) // 1 секунда удержания
                                weStart = true
                                viewModel.fastSpeed()
                            }
                            val released = try {
                                tryAwaitRelease()
                                true
                            } catch (_: Exception) {
                                false
                            }
                            if (released) {
                                job.cancel() // Если отпустили раньше 1 сек — отменяем
                                if (weStart) {
                                    viewModel.normalSpeed()
                                }
                            }
                        }
                    )
                }
                .fillMaxSize(),
        ) {
            when (showSupportView) {
                is SupportView.OnShowEnd -> {
                    control?.setPlaybackSpeed(1f)
                }

                is SupportView.OnSkepBackWard ->
                    SupportPlayerView(Modifier.align(Alignment.TopCenter), "$secondsSeek sec")

                is SupportView.OnSkepForward ->
                    SupportPlayerView(Modifier.align(Alignment.TopCenter), "$secondsSeek sec")

                is SupportView.OnSpeedX2 -> {
                    control?.setPlaybackSpeed(2f)
                    SupportPlayerView(Modifier.align(Alignment.TopCenter), "x2")
                }
            }
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600)),
            exit = fadeOut(animationSpec = tween(600))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                //Нижние кнопки
                BottomButtons(
                    Modifier
                        .align(Alignment.BottomEnd),
                    isFavorite,
                    uiStateShowOpening,
                    uiStateShowEnding, {
                        viewModel.onFavoriteClick()
                    }, {
                        // todo picture in picture
                        Log.e("ololololololo", "first go to pip")
                        viewModel.urgentlyHideViews()
                        control?.let { PipCallback() }
                    }, {
                        showChangeQuality = true
                    }, {
                        release?.episodes[episodeCount]?.ending?.end?.toLong()
                            ?.let { control?.seekTo(it * 1000) }
                    }, {
                        release?.episodes[episodeCount]?.opening?.end?.toLong()
                            ?.let { control?.seekTo(it * 1000) }
                    }
                )
                //Кнопки переключить серию, плей пауз
                PlayerControls(
                    playerState,
                    seekBack = {
                        changeEpisode(episodeNumber - 1)
                    }, playerPause = {
                        viewModel.pauseClick()
                    }, playerPlay = {
                        viewModel.playClick()
                    }, seekForward = {
                        changeEpisode(episodeNumber + 1)
                    })
                //Верхний бар плеера
                TopBarPlayer(
                    Modifier
                        .padding(end = 16.dp, bottom = 16.dp)
                        .align(Alignment.TopCenter),
                    release,
                    episodeNumber,
                    {
                        activity?.setFullscreen(false)
                        onBackClick()
                    }, {
                    }, {
                        showSeriesList = true
                    }
                )
                //скролл плейра
                control?.bufferedPercentage?.toFloat()?.let {
                    PlayerSlider(
                        modifier = Modifier
                            .padding(bottom = 48.dp, start = 16.dp, end = 16.dp)
                            .align(Alignment.BottomStart),
                        playerPositionMs = position,
                        playerDurationMs = duration,
                        bufferedFraction = it / 100,
                        onSeekMs = {
                            control?.seekTo(it)
                        },
                        onChangePositionStart = { dragPosition ->
                            draggingPosition = dragPosition
                            viewModel.dragging()
                        })
                }
                //Текст показываюший время текущего трека
                Text(
                    modifier = Modifier
                        .padding(start = 32.dp, bottom = 24.dp)
                        .align(Alignment.BottomStart)
                        .clickable {
                            viewModel.showCurrentTime()
                        },
                    text = calculateTimeString(
                        position,
                        duration,
                        dragging,
                        draggingPosition,
                        isShowCurrentTime
                    ),
                    color = PlayerColor
                )
            }
        }
        when (seekTo) {
            is SeekToState.OnNext -> {
                control?.currentPosition?.let { control?.seekTo(it + 1000 * secondsSeek) }
                viewModel.seekEnd()
                secondsSeek = 0
            }

            is SeekToState.OnPrevious -> {
                control?.let {
                    it.seekTo(it.currentPosition + 1000 * secondsSeek)
                }
                viewModel.seekEnd()
                secondsSeek = 0
            }

            is SeekToState.OnSeekEnd -> {

            }
        }

        control?.let { player ->
            when (playerState) {
                is PlayerState.OnPlay -> if (!player.isPlaying) player.play()
                is PlayerState.OnPause -> if (player.isPlaying) player.pause()
            }
        }

        if (showSeriesList) {
            release?.episodes?.let {
                ChooseEpisodeDialog(it, {
                    showSeriesList = false
                }, {
                    changeEpisode(it)
                })
            }
        }

        if (showChangeQuality) {
            control?.let { player ->
                OpenSettings(
                    availableVideoHeights(player),
                    {
                        showChangeQuality = false
                    },
                    {
                        selectVideoHeight(player, it)
                        showChangeQuality = false
                    })
            }
        }
    }
}

@Composable
private fun SkipButton(
    isEnding: Boolean,
    onSkipEnding: () -> Unit,
    onSkipStart: () -> Unit
) {
    Row(
        Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        FilledTonalButton(onClick = {
            if (isEnding)
                onSkipEnding()
            else
                onSkipStart()
        }) {
            if (isEnding)
                Text(LocalContext.current.getString(R.string.skep_ending))
            else
                Text(LocalContext.current.getString(R.string.skep_ending))
        }
        OutlinedButton(
            modifier = Modifier.padding(start = 16.dp),
            onClick = { /* действие */ }
        ) {
            Text(LocalContext.current.getString(R.string.cancel))
        }
    }
}

@Composable
private fun BottomButtons(
    modifier: Modifier,
    favorite: Boolean,
    showSkipOpeningButton: Boolean,
    showSkipEndingButton: Boolean,
    onFavoriteClick: () -> Unit,
    onPictureInPicture: () -> Unit,
    onSettingsClick: () -> Unit,
    onSkipEnding: () -> Unit,
    onSkipStart: () -> Unit
) {
    Column(modifier) {
        if (showSkipOpeningButton)
            SkipButton(false, onSkipEnding, onSkipStart)
        if (showSkipEndingButton)
            SkipButton(true, onSkipEnding, onSkipStart)
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(
                onClick = {
                    onFavoriteClick()
                }) {
                Icon(
                    painter = painterResource(
                        if (!favorite) R.drawable.baseline_star_outline_24 else
                            R.drawable.baseline_star_24
                    ),
                    contentDescription = "",
                    tint = PlayerColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(
                onClick = {
                    //todo поделиться???
                }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_share_24),
                    contentDescription = "",
                    tint = PlayerColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(
                onClick = {
                    onPictureInPicture()
                }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_picture_in_picture_24),
                    contentDescription = "",
                    tint = PlayerColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(
                onClick = {
                    onSettingsClick()
                }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_settings_24),
                    contentDescription = "",
                    tint = PlayerColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun OpenSettings(
    episodes: List<Int>,
    dismissDialog: () -> Unit,
    changeQuality: (Int) -> Unit
) {
    ChangeQualityDialog(episodes, { dismissDialog() }, { changeQuality(it) })
    //todo качество
    //todo скорость
    //todo кнопка пропуска опеннинга
    //todo кнопка пропуска ендинга
    //todo Авто пропуск опеннинга
    //todo Авто пропуск ендинга
    // Автовоспроизведение (переход к следующей серии)
}

private fun calculateTimeString(
    currentTime: Long,
    allTime: Long,
    dragging: Boolean,
    draggingPosition: Long,
    isShowCurrentTime: Boolean
): String {
    if (allTime <= 0L) return "00:00/00:00"
    var timeInSec: Int = ((if (dragging) draggingPosition else currentTime) / 1000).toInt()
    val allTimeInSec: Int = (allTime / 1000).toInt()
    timeInSec = if (!isShowCurrentTime) timeInSec else allTimeInSec - timeInSec
    return (if (!isShowCurrentTime) "" else "-") + calculateTimeFromLongToString(timeInSec) + "/" + calculateTimeFromLongToString(
        allTimeInSec
    )
}

fun calculateTimeFromLongToString(timeInSec: Int): String {
    val minute: Int = timeInSec / 60
    val second: Int = timeInSec % 60
    val minuteStr =
        if (minute < 10 && minute != 0) "0$minute" else if (minute == 0) "00" else minute.toString()
    val secondStr =
        if (second < 10 && second != 0) "0$second" else if (second == 0) "00" else second.toString()
    return "$minuteStr:$secondStr"
}

@OptIn(UnstableApi::class)
fun availableVideoHeights(player: MediaController): List<Int> {
    val set = sortedSetOf<Int>()
    player.currentTracks.groups
        .filter { it.type == C.TRACK_TYPE_VIDEO && it.isSupported }
        .forEach { group ->
            for (i in 0 until group.mediaTrackGroup.length) {
                val h = group.mediaTrackGroup.getFormat(i).height
                if (h > 0) set += h
            }
        }
    return set.toList().sortedDescending()
}

@OptIn(UnstableApi::class)
fun selectVideoHeight(player: MediaController, targetHeight: Int) {
    val groups = player.currentTracks.groups
        .filter { it.type == C.TRACK_TYPE_VIDEO && it.isSupported }
    for (g in groups) {
        val mtg = g.mediaTrackGroup
        for (i in 0 until mtg.length) {
            val h = mtg.getFormat(i).height
            if (h == targetHeight) {
                val builder = player.trackSelectionParameters.buildUpon()
                builder.clearOverridesOfType(C.TRACK_TYPE_VIDEO)
                val selectedTrackGroup = TrackSelectionOverride(
                    mtg,
                    listOf(i)
                )
                builder.setOverrideForType(selectedTrackGroup)
                player.trackSelectionParameters = builder.build()
            }
        }
    }
}