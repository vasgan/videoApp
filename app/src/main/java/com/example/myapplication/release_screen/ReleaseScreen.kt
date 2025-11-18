package com.example.myapplication.release_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.ui_data.Episode
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.ui.AnnotatedClickableText
import com.example.myapplication.ui.ShimmerImage
import com.example.myapplication.ui.findActivity
import com.example.myapplication.ui.setFullscreen

@SuppressLint("StringFormatMatches")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleaseScreen(
    innerPadding: PaddingValues,
    viewModel: ReleaseScreenViewModel,
    id: String,
    onReleaseClick: (String) -> Unit,
    onGenreClick: (String) -> Unit,
    onEpisodeClick: (Int, Release) -> Unit,
    onBackClick: () -> Unit
) {
    val model by viewModel.uiState.collectAsStateWithLifecycle()
    val favorite by viewModel.favoriteUiState.collectAsStateWithLifecycle()
    val watchList by viewModel.watchListUIState.collectAsStateWithLifecycle()
    val history by viewModel.uiStateHistory.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()
    viewModel.getCurrentRelease(id)
    LaunchedEffect(Unit) {
        activity?.setFullscreen(false)
    }
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        item {
            ShimmerImage(
                url = "https://anilibria.top/" + model?.poster,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                ContentScale.Crop, 0
            )
            FilledTonalButton(
                modifier = Modifier
                    .padding(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 0.dp
                    )
                    .fillMaxWidth(), onClick = {
                    model?.let {
                        if (history == null)
                            onEpisodeClick(0, it)
                        else
                            onEpisodeClick(history!!.episodeNumber, it)
                    }
                }) {
                if (history == null)
                    Text(context.getString(R.string.start_watching))
                else Text(context.getString(R.string.contine_watching, (history!!.episodeNumber + 1).toString()))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val name = model?.name + "(" + model?.episodes?.size + ")"
                Text(
                    text = name,
                    modifier = Modifier
                        .weight(1f)
                        .padding(PaddingValues(16.dp, 16.dp, 0.dp, 0.dp)),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                Image(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(PaddingValues(8.dp, 16.dp, 8.dp, 0.dp))
                        .clickable {
                            viewModel.changeWatchList()
                        },
                    painter = if (watchList) painterResource(id = R.drawable.add_to_queue_24dp_select)
                    else painterResource(id = R.drawable.add_to_queue_24dp),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
                Image(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(PaddingValues(8.dp, 16.dp, 8.dp, 0.dp))
                        .clickable {
                            viewModel.changeFavorite()
                        },
                    painter = if (favorite) painterResource(id = R.drawable.baseline_star_24)
                    else painterResource(id = R.drawable.baseline_star_outline_24),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
            //Жанры
            val localContext = LocalContext.current
            val annotated = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(localContext.getString(R.string.genres) + " ")
                }
                model?.genres?.forEachIndexed { index, response ->
                    pushStringAnnotation(response.name, response.id)
                    withStyle(
                        SpanStyle(
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(response.name)
                    }
                    pop()
                    if (index != model!!.genres.lastIndex) append(", ")
                }
            }

            AnnotatedClickableText(
                text = annotated,
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(PaddingValues(16.dp, 16.dp, 16.dp, 8.dp))
            ) { url ->
                onGenreClick(url)
            }

            //сезон
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(localContext.getString(R.string.season) + " ")
                    }
                    append(model?.year)
                }, modifier = Modifier
                    .padding(PaddingValues(16.dp, 0.dp, 16.dp, 16.dp)),
                style = MaterialTheme.typography.bodyLarge
            )

            // тип количество серий
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(localContext.getString(R.string.type_release) + " ")
                    }
                    append(model?.type)
                }, modifier = Modifier
                    .padding(PaddingValues(16.dp, 0.dp, 16.dp, 16.dp)),
                style = MaterialTheme.typography.bodyLarge
            )
            // разделитель
            HorizontalDivider(
                modifier = Modifier
                    .padding(PaddingValues(16.dp, 0.dp, 16.dp, 16.dp))
                    .fillMaxHeight()
                    .width(1.dp),
                color = Color.LightGray
            )
            //Описание
            Text(
                text = model?.description + " ",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp
                ),
                modifier = Modifier //todo выпилить нормально чекать на нулл
                    .padding(PaddingValues(16.dp, 16.dp, 16.dp, 16.dp)),
            )

            model?.franchise?.size?.let {
                if (it > 0)
                //Разделитель франшизы
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        thickness = 12.dp,
                        color = Color.LightGray
                    )
            }
        }
        model?.franchise?.size?.let {
            items(model?.franchise!!.size) { i ->
                Text(
                    text = model!!.franchise[i].releaseName,
                    modifier = Modifier
                        .clickable {
                            onReleaseClick(model!!.franchise[i].releaseId)
                        }
                        .padding(PaddingValues(16.dp, 16.dp, 16.dp, 0.dp)),
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 16.sp
                    ),
                )
            }
        }
        item {
            model?.franchise?.size?.let {
                //Разделитель франшизы
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    thickness = 12.dp,
                    color = Color.LightGray
                )
            }
        }
        model?.episodes?.size?.let { it1 ->
            items(it1) { i ->
                EpisodeItem(model!!.episodes[i]) {
                    onEpisodeClick(i, model!!)
                }
            }

        }
        item {
            model?.franchise?.size?.let {
                //Разделитель франшизы
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    thickness = 12.dp,
                    color = Color.LightGray
                )
            }
        }
    }

    //todo перерисовать на иконку с фоном каким-нить и нажатием
    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            IconButton(onClick = {
                onBackClick()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent, // фон прозрачный
            scrolledContainerColor = Color.Transparent
        )
    )
}

@Composable
fun EpisodeItem(
    episode: Episode,
    onItemClick: (url: String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(episode.hls480) }) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(height = 72.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                val text = if (episode.name.isEmpty()) "Серия ${episode.ordinal}"
                else "Серия ${episode.ordinal} • ${episode.name}"
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "Смотреть",
                color = Color(0xFFD50000),                 // красный как на скрине
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 12.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )
    }
}