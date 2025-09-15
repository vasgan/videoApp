package com.example.myapplication.release_screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.myapplication.ui.AnnotatedClickableText
import com.example.myapplication.ui.ShimmerImage

@Composable
fun ReleaseScreen(viewModel: ReleaseScreenViewModel, id: String, onReleaseClick: (String) -> Unit) {
    viewModel.getCurrentRelease(id)
    val model by viewModel.uiState.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            ShimmerImage(
                url = "https://aniliberty.top/" + model?.poster,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            )
            //Заголовок
            val name = model?.name + "(" + model?.episodes?.size + ")"
            Text(
                text = name,
                modifier = Modifier
                    .padding(PaddingValues(16.dp, 16.dp, 16.dp, 0.dp)),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
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
                Toast.makeText(localContext, url, Toast.LENGTH_LONG).show()
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
                EpisodeItem(model!!.episodes[i])
                //EpisodeElement(model!!.episodes[i])
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
}

@Composable
fun EpisodeItem(
    episode: Episode,
    onItemClick: () -> Unit = {},
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
                modifier = Modifier.weight(1f)
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