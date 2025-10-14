package com.example.myapplication.player

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.ui.CustomSlider
import com.example.myapplication.ui.findActivity
import com.example.myapplication.ui.setFullscreen
import com.example.myapplication.ui.theme.PlayerBackgroundTextColor
import com.example.myapplication.ui.theme.PlayerColor
import com.example.myapplication.ui.theme.TransparentColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("ResourceAsColor")
@Composable
fun PlayerScreen(episodeNumber: Int, release: Release, viewModel: PLayerViewModel, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val activity = context.findActivity()
    activity?.setFullscreen(true)
    val urlEpisode = release.episodes[episodeNumber].hls480
    val scope = rememberCoroutineScope()

    var duration by remember { mutableLongStateOf(0L) }
    var draggingPosition by remember { mutableLongStateOf(0L) }
    var isShowCurrentTime by remember { mutableStateOf(false) }

    val showSupportView by viewModel.uiStateSpeedX2.collectAsState()
    val seekTo by viewModel.uiStatePlayerSeek.collectAsState()
    val position by viewModel.uiStatePosition.collectAsState()
    val visible by viewModel.uiStateHideViews.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsState()
    val dragging by viewModel.draggingState.collectAsState()

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(urlEpisode))
            prepare()
            playWhenReady = true
        }
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
    var seconds by remember { mutableLongStateOf(0L) }
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
                                seconds += 10
                                delay(500)
                                viewModel.onNextClick()
                                viewModel.endShowSupportView()
                            }
                        } else {
                            jobMoving?.cancel()
                            jobMoving = scope.launch {
                                viewModel.showBackward()
                                seconds -= 10
                                delay(500)
                                viewModel.onPreviousClick()
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
            is SupportView.OnShowEnd -> {
                seconds = 0
            }

            is SupportView.OnSkepBackWard ->
                SupportPlayerView(Modifier.align(Alignment.TopCenter), "$seconds sec")

            is SupportView.OnSkepForward ->
                SupportPlayerView(Modifier.align(Alignment.TopCenter), "$seconds sec")

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
                    .align(Alignment.BottomEnd)
            )
            PlayerControls(playerState, seekBack = {
                viewModel.onPreviousClick()
            }, playerPause = {
                viewModel.pauseClick()
            }, playerPlay = {
                viewModel.playClick()
            }, seekForward = {
                viewModel.onNextClick()
            })
            TopBarPlayer(
                Modifier
                    .padding(end = 16.dp, bottom = 16.dp)
                    .align(Alignment.TopCenter), release, episodeNumber,
                {
                    onBackClick()
                }
            )
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
                        isShowCurrentTime = !isShowCurrentTime
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
            exoPlayer.seekTo(exoPlayer.currentPosition + 10_000)
            viewModel.seekEnd()
        }

        is SeekToState.OnPrevious -> {
            exoPlayer.seekTo(exoPlayer.currentPosition - 10_000)
            viewModel.seekEnd()
        }

        is SeekToState.OnSeekEnd -> {}
    }
    when (playerState) {
        is PlayerState.OnPlay -> if (!exoPlayer.isPlaying) exoPlayer.play()
        is PlayerState.OnPause -> if (exoPlayer.isPlaying) exoPlayer.pause()
    }

    //todo поиграться с кэшем
}

@Composable
private fun SupportPlayerView(modifier: Modifier, text: String) {
    Text(
        modifier = modifier
            .padding(0.dp, 72.dp, 0.dp, 0.dp)
            .background(
                color = PlayerBackgroundTextColor,
                shape = RoundedCornerShape(32.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp),
        text = text,
        color = PlayerColor
    )

}

@Composable
private fun BottomButtons(modifier: Modifier) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        IconButton(
            onClick = {
                //todo добавить в избранное
            }) {
            Icon(
                painter = painterResource(R.drawable.baseline_star_outline_24),
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
                //todo картинка в картинке
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
private fun TopBarPlayer(
    modifier: Modifier, release: Release, episode: Int, onBackClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.3f)) // полупрозрачный фон
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onBackClick() }) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back_24),
                contentDescription = "",
                tint = PlayerColor
            )
        }
        IconButton(onClick = { }) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back_24),
                contentDescription = "",
                tint = TransparentColor
            )
        }
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = release.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            if (!release.episodes[episode].name.isBlank())
            Text(
                text = release.episodes[episode].name,
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
        IconButton(onClick = {
            //todo трансляция на экран
        }) {
            Icon(
                painter = painterResource(R.drawable.baseline_playlist_play_24),
                contentDescription = "Меню",
                tint = PlayerColor
            )
        }
        IconButton(onClick = { //todo список всех серий
        }) {
            Icon(
                painter = painterResource(R.drawable.baseline_cast_24),
                contentDescription = "Меню",
                tint = PlayerColor
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

@Composable
fun PlayerControls(
    playerState: PlayerState,
    seekBack: () -> Unit,
    playerPause: () -> Unit,
    playerPlay: () -> Unit,
    seekForward: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(Modifier.align(Alignment.Center), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            IconButton(
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        color = Color(LocalContext.current.getColor(R.color.button_transparent_color)),
                        shape = CircleShape
                    ), onClick = { seekBack() }) {
                Icon(
                    painterResource(R.drawable.baseline_skip_previous_24),
                    null,
                    tint = androidx.compose.ui.graphics.Color.Unspecified
                )
            }
            IconButton(
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        color = Color(LocalContext.current.getColor(R.color.button_transparent_color)),
                        shape = CircleShape
                    ), onClick = {

                    if (playerState is PlayerState.OnPlay) {
                        playerPause()
                    } else {
                        playerPlay()
                    }
                }) {
                Icon(
                    painter = if (playerState is PlayerState.OnPlay) painterResource(R.drawable.baseline_pause_24)
                    else painterResource(
                        R.drawable.baseline_play_arrow_24
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            IconButton(
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        color = Color(LocalContext.current.getColor(R.color.button_transparent_color)),
                        shape = CircleShape
                    ), onClick = { seekForward() }) {
                Icon(
                    painterResource(R.drawable.baseline_skip_next_24),
                    null,
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@Composable
fun PlayerSlider(
    playerPositionMs: Long,
    playerDurationMs: Long,
    bufferedFraction: Float,
    onSeekMs: (Long) -> Unit,
    onChangePositionStart: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val range = 0f..playerDurationMs.coerceAtLeast(1L).toFloat()
    CustomSlider(
        value = playerPositionMs.toFloat(),
        onPreviewChange = { onChangePositionStart(it.toLong()) },
        onCommitChange = { onSeekMs(it.toLong()) },
        valueRange = range,
        bufferedFraction = bufferedFraction,
        activeColor = PlayerColor,
        inactiveColor = Color.Gray.copy(alpha = 0.35f),
        bufferedColor = Color.LightGray.copy(alpha = 0.6f),
        thumbColor = Color.White,
        trackHeight = 6.dp,
        thumbRadius = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}