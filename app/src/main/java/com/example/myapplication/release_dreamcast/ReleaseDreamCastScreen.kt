package com.example.myapplication.release_dreamcast

import android.annotation.SuppressLint
import android.text.Html
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.R
import com.example.myapplication.data.parse.dreamcast.DreamCastDescriptionAndSeries
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.example.myapplication.helpers.htmlToAnnotatedString
import com.example.myapplication.ui.EmptyState
import com.example.myapplication.ui.LoadingSpinner
import com.example.myapplication.ui.ShimmerImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleaseDreamCastScreen(
    innerPadding: PaddingValues,
    viewModel: ReleaseDreamCastViewModel,
    dreamCastReleaseFromData: DreamCastReleaseElement?,
    url: String?,
    id: String?,
    onEpisodeClick: (Int, DreamCastReleaseElement, DreamCastDescriptionAndSeries) -> Unit,
    onBackClick: () -> Unit
) {
    val releaseId = id ?: dreamCastReleaseFromData?.id
    val releaseUrl = url ?: dreamCastReleaseFromData?.url

    LaunchedEffect(releaseId, releaseUrl) {
        viewModel.loadDetails(releaseUrl, releaseId, dreamCastReleaseFromData)
    }

    val detailState by viewModel.detailState.collectAsStateWithLifecycle()
    val favorite by viewModel.favoriteUiState.collectAsStateWithLifecycle()
    val watchList by viewModel.watchListUiState.collectAsStateWithLifecycle()

    when (val state = detailState) {
        DreamCastDetailState.Loading -> Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LoadingSpinner()
        }

        DreamCastDetailState.Error -> EmptyState(
            title = stringResource(R.string.other_load_error),
            modifier = Modifier.padding(innerPadding),
            icon = painterResource(R.drawable.baseline_history_24)
        )

        is DreamCastDetailState.Content -> DreamCastDetailContent(
            innerPadding = innerPadding,
            dreamCastRelease = state.release,
            model = state.series,
            history = state.history,
            favorite = favorite,
            watchList = watchList,
            onEpisodeClick = onEpisodeClick,
            onWatchListClick = viewModel::changeWatchList,
            onFavoriteClick = viewModel::changeFavorite
        )
    }

    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        )
    )
}

@Composable
private fun DreamCastDetailContent(
    innerPadding: PaddingValues,
    dreamCastRelease: DreamCastReleaseElement,
    model: DreamCastDescriptionAndSeries,
    history: com.example.myapplication.data.db.history.History?,
    favorite: Boolean,
    watchList: Boolean,
    onEpisodeClick: (Int, DreamCastReleaseElement, DreamCastDescriptionAndSeries) -> Unit,
    onWatchListClick: (DreamCastReleaseElement?) -> Unit,
    onFavoriteClick: (DreamCastReleaseElement?) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
        item {
            val image = dreamCastRelease.image.takeIf { it.isNotBlank() }?.let {
                if (it.indexOf("://") >= 0) {
                    dreamCastRelease.image
                } else {
                    "https://" + dreamCastRelease.image
                }
            }
            image?.let {
                ShimmerImage(
                    url = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    ContentScale.Crop, 0
                )
            }
            FilledTonalButton(
                modifier = Modifier
                    .padding(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 0.dp
                    )
                    .fillMaxWidth(), onClick = {
                    val episode = history?.episodeNumber
                        ?.takeIf { it in model.series.indices }
                        ?: 0
                    onEpisodeClick(episode, dreamCastRelease, model)
                }) {
                val currentHistory = history?.takeIf { it.episodeNumber in model.series.indices }
                if (currentHistory == null)
                    Text(LocalContext.current.getString(R.string.start_watching))
                else Text(
                    LocalContext.current.getString(
                        R.string.continue_watching_from_series,
                        (currentHistory.episodeNumber + 1).toString()
                    )
                )
            }
            //Заголовок
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = htmlToAnnotatedString(dreamCastRelease.russian),
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
                            onWatchListClick(dreamCastRelease)
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
                            onFavoriteClick(dreamCastRelease)
                        },
                    painter = if (favorite) painterResource(id = R.drawable.baseline_star_24)
                    else painterResource(id = R.drawable.baseline_star_outline_24),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .padding(PaddingValues(16.dp, 0.dp, 16.dp, 16.dp))
                    .fillMaxHeight()
                    .width(1.dp),
                color = Color.LightGray
            )
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(LocalContext.current.getString(R.string.genres) + " ")
                    }
                    append(dreamCastRelease.genres)
                }, modifier = Modifier
                    .padding(PaddingValues(16.dp, 0.dp, 16.dp, 16.dp)),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(LocalContext.current.getString(R.string.episode_count) + " ")
                    }
                    append(
                        LocalContext.current.getString(
                            R.string.episodes,
                            dreamCastRelease.series,
                            dreamCastRelease.currentSeries
                        )
                    )
                }, modifier = Modifier
                    .padding(PaddingValues(16.dp, 0.dp, 16.dp, 16.dp)),
                style = MaterialTheme.typography.bodyLarge
            )
            if (model.description.isNotBlank())
                Text(
                    text = AnnotatedString.fromHtml(
                        Html.fromHtml(
                            model.description,
                            Html.FROM_HTML_MODE_LEGACY
                        ).toString()
                    ),
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 16.sp
                    ),
                    modifier = Modifier
                        .padding(PaddingValues(16.dp, 16.dp, 16.dp, 16.dp)),
                )
        }

        items(model.series.size) { i ->
            EpisodeItem((i + 1).toString(), {
                onEpisodeClick(i, dreamCastRelease, model)
            })
        }
    }
}

@SuppressLint("StringFormatMatches")
@Composable
fun EpisodeItem(
    episode: String,
    onItemClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }) {
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
                val text = LocalContext.current.getString(R.string.series, episode, "")
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
