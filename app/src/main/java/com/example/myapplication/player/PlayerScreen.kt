package com.example.myapplication.player

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.util.Log
import android.util.Rational
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import com.example.myapplication.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import com.example.myapplication.ui.findActivity
import com.example.myapplication.ui.setFullscreen
import com.example.myapplication.ui.theme.PlayerColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ResourceAsColor")
@Composable
fun PlayerScreen(
    episodeCount: Int,
    viewModel: PLayerViewModel,
    mediaSession: MediaSession?,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    activity?.setFullscreen(true)
    val scope = rememberCoroutineScope()

    var duration by remember { mutableLongStateOf(0L) }
    var draggingPosition by remember { mutableLongStateOf(0L) }
    var showSeriesList by remember { mutableStateOf(false) }
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

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            release?.episodes[episodeCount]?.hls480?.let {
                setMediaItem(MediaItem.fromUri(it))
            }
            prepare()
            playWhenReady = true
        }
    }
    val mediaSession = remember {
        MediaSession.Builder(context, exoPlayer).build()
    }

    fun changeEpisode(episodeNum: Int) {
        viewModel.onChangeEpisode(episodeNum)
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        release?.episodes[episodeNum]?.hls480?.let {
            exoPlayer.setMediaItem(
                MediaItem.fromUri(it),
                true
            )
        }
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        showSeriesList = false
    }
    exoPlayer.addListener(object : Player.Listener {
        private var wasSeek = false
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                wasSeek = true
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (wasSeek && isPlaying) {
                wasSeek = false
                viewModel.draggingEnd()
            }
        }
    })
    DisposableEffect(
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                    setBackgroundColor(context.getColor(R.color.black))
                }
            }
        )
    ) {
        onDispose {
            mediaSession.release()
            exoPlayer.release()
        }
    }
    LaunchedEffect(exoPlayer) {
        while (true) {
            viewModel.changePosition(exoPlayer.currentPosition)
            duration = exoPlayer.duration
            delay(500)
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
                            exoPlayer.setPlaybackSpeed(2f)

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
                                viewModel.endShowSupportView()
                                exoPlayer.setPlaybackSpeed(1f)
                            }
                        }
                    }
                )
            }
            .fillMaxSize(),
    ) {
        when (showSupportView) {
            is SupportView.OnShowEnd -> {}

            is SupportView.OnSkepBackWard ->
                SupportPlayerView(Modifier.align(Alignment.TopCenter), "$secondsSeek sec")

            is SupportView.OnSkepForward ->
                SupportPlayerView(Modifier.align(Alignment.TopCenter), "$secondsSeek sec")

            is SupportView.OnSpeedX2 ->
                SupportPlayerView(Modifier.align(Alignment.TopCenter), "x2")
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)),
        exit = fadeOut(animationSpec = tween(600))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BottomButtons(
                Modifier
                    .padding(end = 16.dp, bottom = 16.dp)
                    .align(Alignment.BottomEnd), isFavorite, {
                    viewModel.onFavoriteClick()
                }, {
                    viewModel.urgentlyHideViews()
                    val aspect = Rational(16, 9)
                    val params = PictureInPictureParams.Builder()
                        .setAspectRatio(aspect)
                        .build()
                    activity?.enterPictureInPictureMode(params)
                }
            )
            PlayerControls(playerState, seekBack = {
                changeEpisode(episodeNumber - 1)
            }, playerPause = {
                viewModel.pauseClick()
            }, playerPlay = {
                viewModel.playClick()
            }, seekForward = {
                changeEpisode(episodeNumber + 1)
            })
            release?.let {
                Log.e("olololololo", episodeNumber.toString())
                TopBarPlayer(
                    Modifier
                        .padding(end = 16.dp, bottom = 16.dp)
                        .align(Alignment.TopCenter), it, episodeNumber,
                    {
                        onBackClick()
                    }, {
                    }, {
                        showSeriesList = true
                    }
                )
            }
            PlayerSlider(
                modifier = Modifier
                    .padding(bottom = 48.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.BottomStart),
                playerPositionMs = position,
                playerDurationMs = duration,
                bufferedFraction = exoPlayer.bufferedPercentage.toFloat() / 100,
                onSeekMs = {
                    exoPlayer.seekTo(it)
                },
                onChangePositionStart = { dragPosition ->
                    draggingPosition = dragPosition
                    viewModel.dragging()
                })
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
            exoPlayer.seekTo(exoPlayer.currentPosition + 1000 * secondsSeek)
            viewModel.seekEnd()
            secondsSeek = 0
        }

        is SeekToState.OnPrevious -> {
            exoPlayer.seekTo(exoPlayer.currentPosition + 1000 * secondsSeek)
            viewModel.seekEnd()
            secondsSeek = 0
        }

        is SeekToState.OnSeekEnd -> {

        }
    }
    when (playerState) {
        is PlayerState.OnPlay -> if (!exoPlayer.isPlaying) exoPlayer.play()
        is PlayerState.OnPause -> if (exoPlayer.isPlaying) exoPlayer.pause()
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
}

@Composable
private fun BottomButtons(
    modifier: Modifier, favorite: Boolean, onFavoriteClick: () -> Unit,
    onPictureInPicture: () -> Unit
) {
    Row(
        modifier.fillMaxWidth(),
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
                //todo настройки
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

@Composable
private fun OpenSettings(modifier: Modifier) {
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