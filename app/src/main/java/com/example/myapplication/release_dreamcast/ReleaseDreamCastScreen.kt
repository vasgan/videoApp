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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.myapplication.ui.ShimmerImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleaseDreamCastScreen(
    innerPadding: PaddingValues,
    viewModel: ReleaseDreamCastViewModel,
    dreamCastRelease: DreamCastReleaseElement,
    onReleaseClick: (String) -> Unit,
    onEpisodeClick: (Int, DreamCastReleaseElement, DreamCastDescriptionAndSeries) -> Unit,
    onBackClick: () -> Unit
) {
    viewModel.getSeriesList("https://dreamerscast.com" + dreamCastRelease.url)
    val model by viewModel.uiState.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        item {
            ShimmerImage(
                url = "https://" + dreamCastRelease.image,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                ContentScale.Crop, 0
            )
            //Заголовок
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val name = dreamCastRelease.russian
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

                        },
                    painter = painterResource(id = R.drawable.add_to_queue_24dp),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
                Image(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(PaddingValues(8.dp, 16.dp, 8.dp, 0.dp))
                        .clickable {

                        },
                    painter = painterResource(id = R.drawable.baseline_star_outline_24),
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
            if (model?.description != null)
                Text(
                    text = AnnotatedString.fromHtml(
                        Html.fromHtml(
                            model?.description,
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

        model?.series?.size?.let { it1 ->
            items(it1) { i ->
                EpisodeItem((i + 1).toString(), {
                    onEpisodeClick(i, dreamCastRelease, model!!)
                })
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