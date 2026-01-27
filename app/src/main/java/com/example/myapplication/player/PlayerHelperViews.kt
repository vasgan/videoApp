package com.example.myapplication.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.R
import com.example.myapplication.data.ui_data.Episode
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.ui.CustomSlider
import com.example.myapplication.ui.theme.PlayerBackgroundTextColor
import com.example.myapplication.ui.theme.PlayerColor
import com.example.myapplication.ui.theme.TransparentColor

@Composable
fun ChangeQualityDialog(
    episodes: List<Int>,
    dismissDialog: () -> Unit,
    changeQuality: (quality: Int) -> Unit
) {
    var openDialog by remember { mutableStateOf(true) }
    if (openDialog) {
        Dialog(
            onDismissRequest = {
                openDialog = false
                dismissDialog()
            }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (i in 0..episodes.size - 1) {
                        Text(
                            text = episodes[i].toString(),
                            fontSize = 14.sp,
                            maxLines = 3,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    changeQuality(episodes[i])
                                }
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(Color(0xFFE0E0E0))
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChooseEpisodeDialog(
    episodes: List<Episode>,
    dismissDialog: () -> Unit,
    chooseEpisode: (episodeNumber: Int) -> Unit
) {
    Dialog(
        onDismissRequest = {
            dismissDialog()
        }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0..< episodes.size) {
                    val name =
                        stringResource(
                            R.string.series, (i + 1).toString(), episodes[i].name
                        )
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        maxLines = 3,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                chooseEpisode(i)
                            }
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color(0xFFE0E0E0))
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TopBarPlayer(
    modifier: Modifier,
    release: Release?,
    episode: Int,
    onBackClick: () -> Unit,
    translateToScreen: () -> Unit,
    showEpisodeList: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.3f)) // полупрозрачный фон
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        release?.let {
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
                val name =
                    stringResource(
                        R.string.series, (episode + 1).toString(), release.episodes[episode].name
                    )
                Text(
                    text = name,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
            IconButton(onClick = {
                translateToScreen()
            }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_cast_24),
                    contentDescription = "Меню",
                    tint = PlayerColor
                )
            }
            IconButton(onClick = {
                showEpisodeList()
            }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_playlist_play_24),
                    contentDescription = "Меню",
                    tint = PlayerColor
                )
            }
        }
    }
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

@Composable
fun SupportPlayerView(modifier: Modifier, text: String) {
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