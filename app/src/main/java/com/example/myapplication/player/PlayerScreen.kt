package com.example.myapplication.player

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.myapplication.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.myapplication.ui.CustomSlider
import com.example.myapplication.ui.findActivity
import com.example.myapplication.ui.setFullscreen
import com.example.myapplication.ui.theme.PlayerColor
import kotlinx.coroutines.delay

@SuppressLint("ResourceAsColor")
@Composable
fun PlayerScreen(mediaUrls: List<String>, viewModel: PLayerViewModel) {
    val context = LocalContext.current
    val activity = context.findActivity()
    activity?.setFullscreen(true)
    val last = mediaUrls[0].lastIndexOf("?")
    val string = mediaUrls[0].substring(0, last)

    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var draggingPosition by remember { mutableLongStateOf(0L) }
    var dragging by remember { mutableStateOf(false) }
    var isShowCurrentTime by remember { mutableStateOf(false) }
    val visible by viewModel.uiStateHideViews.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsState()
    val draggingState by viewModel.draggingState.collectAsState()
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(string))
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
                dragging = false
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
            position = exoPlayer.currentPosition
            duration = exoPlayer.duration
            delay(500)
        }
    }

    Box(
        Modifier
            .clickable {
                viewModel.outClick()
            }
            .fillMaxSize())

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)),
        exit = fadeOut(animationSpec = tween(600))
    ) {
        PlayerControls(playerState, seekBack = {
            viewModel.onPreviousClick()
        }, playerPause = {
            viewModel.pauseClick()
        }, playerPlay = {
            viewModel.playClick()
        }, seekForward = {
            viewModel.onNextClick()
        })
        PlayerSlider(
            playerPositionMs = position,
            playerDurationMs = duration,
            bufferedFraction = exoPlayer.bufferedPercentage.toFloat() / 100,
            onSeekMs = {
                exoPlayer.seekTo(it)
                viewModel.draggingEnd()
            },
            onChangePositionStart = { dragPosition ->
                draggingPosition = dragPosition
                dragging = true
                viewModel.dragging()
            })

        Text(
            modifier = Modifier.clickable {
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
    //todo Сделать двойной тап скип 10 секунд
    //todo сделать лонг тап скорость x2
    //todo тап все прятать/показывать с анимацией
    //todo прятать по таймеру 10 секунд
}

@Composable
private fun BottomButtons(modifier: Modifier) {
    //todo добавить в избранное
    //todo поделиться???
    //todo картинка в картинке
    //todo настройки
}

@Composable
private fun TopButtons(modifier: Modifier) {
    //todo кнопка назад
    //todo название сирика
    //todo название серии
    //todo трансляция на экран
    //todo список всех серий
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