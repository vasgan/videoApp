package com.example.myapplication.release_screen

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.myapplication.data.db.history.History
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
    val model by viewModel.uiStateRelease.collectAsStateWithLifecycle()
    val franchise by viewModel.uiStateFranchise.collectAsStateWithLifecycle()
    val watchList by viewModel.watchListUIState.collectAsStateWithLifecycle()
    val history by viewModel.uiStateHistory.collectAsStateWithLifecycle()
    val favorite by viewModel.favoriteUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context.findActivity()
    LaunchedEffect(Unit) {
        activity?.setFullscreen(false)
        viewModel.getCurrentRelease(id)
    }
    when (model) {
        is UiReleaseAniliberty.CompleteUiReleaseAniliberty ->
            model!!.release?.let { release ->
                franchise?.let { franchiseState ->
                    ShowRelease(
                        context, innerPadding, history, watchList, viewModel, release,
                        franchiseState, favorite, onReleaseClick, onGenreClick, onEpisodeClick
                    )
                }
            }

        is UiReleaseAniliberty.ErrorUiReleaseAniliberty -> {
        }

        is UiReleaseAniliberty.LoadingUiReleaseAniliberty -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 4.dp
                )
            }
        }

        null -> {
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
fun ShowRelease(
    context: Context,
    innerPadding: PaddingValues,
    history: History?,
    watchList: Boolean,
    viewModel: ReleaseScreenViewModel,
    model: Release,
    franchiseState: UiFranchiseAniliberty,
    favorite: Boolean,
    onReleaseClick: (String) -> Unit,
    onGenreClick: (String) -> Unit,
    onEpisodeClick: (Int, Release) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        item {
            ShimmerImage(
                url = "https://aniliberty.top/" + model.poster,
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
                    if (history == null)
                        onEpisodeClick(0, model)
                    else
                        onEpisodeClick(history.episodeNumber, model)

                }) {
                if (history == null)
                    Text(context.getString(R.string.start_watching))
                else Text(
                    context.getString(
                        R.string.continue_watching_from_series,
                        (history.episodeNumber + 1).toString()
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val name = model.name + "(" + model.episodes.size + ")"
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
                    contentDescription = null,
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
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            //Жанры
            val localContext = LocalContext.current
            val annotated = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(localContext.getString(R.string.genres) + " ")
                }
                model.genres.forEachIndexed { index, response ->
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
                    if (index != model.genres.lastIndex) append(", ")
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
                    append(model.year)
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
                    append(model.type)
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
            if (!model.description.isNullOrEmpty()) {
                Text(
                    text = model.description,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier
                        .padding(PaddingValues(16.dp, 16.dp, 16.dp, 16.dp)),
                )
            }
            if (franchiseState is UiFranchiseAniliberty.CompleteUiFranchiseAniliberty) {
                franchiseState.franchise?.size.let {
                    it?.let { it1 ->
                        if (it1 > 0)
                        //Разделитель франшизы
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .height(12.dp),
                                thickness = 12.dp,
                                color = Color.LightGray
                            )
                    }
                }
            }
        }
        when (franchiseState) {
            is UiFranchiseAniliberty.CompleteUiFranchiseAniliberty -> {
                franchiseState.franchise?.size?.let {
                    items(it) { i ->
                        Text(
                            text = franchiseState.franchise[i].releaseName,
                            modifier = Modifier
                                .clickable {
                                    onReleaseClick(franchiseState.franchise[i].releaseId)
                                }
                                .padding(PaddingValues(16.dp, 8.dp, 16.dp, 8.dp)),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 16.sp
                            ),
                        )
                    }
                }
                item {
                    franchiseState.franchise?.size.let {
                        //Разделитель франшизы
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                                .height(12.dp),
                            thickness = 12.dp,
                            color = Color.LightGray
                        )
                    }
                }
            }

            is UiFranchiseAniliberty.ErrorUiFranchiseAniliberty -> {
            }

            is UiFranchiseAniliberty.LoadingUiFranchiseAniliberty -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            strokeWidth = 4.dp
                        )
                    }
                }
            }
        }


        items(model.episodes.size) { i ->
            EpisodeItem(model.episodes[i]) {
                onEpisodeClick(i, model)
            }
        }
        item {
            //Разделитель Эпизодов
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