package com.example.myapplication.history_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.R
import com.example.myapplication.ui.ShimmerImage
import com.example.myapplication.ui.SimpleTopBar

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onItemClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val history by viewModel.historyUiState.collectAsStateWithLifecycle()
    viewModel.getFavorite()
    Scaffold(
        topBar = {
            SimpleTopBar(R.string.history) { onBackClick() }
        }, content = { innerPaddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPaddingValues)
                    .fillMaxSize()
            ) {
                history?.let {
                    items(it.size) { i ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onItemClick(history!![i].releaseId.toString())
                                }) {
                            ShimmerImage(
                                "https://aniliberty.top/" + history!![i].imageUrl,
                                Modifier
                                    .width(120.dp)
                                    .wrapContentHeight()
                                    .padding(start = 16.dp, top = 8.dp, bottom = 16.dp)
                                    .fillMaxSize(),
                                ContentScale.FillWidth, 8
                            )
                            Column {
                                Text(
                                    text = history!![i].releaseName,
                                    modifier = Modifier
                                        .padding(PaddingValues(16.dp, 16.dp, 16.dp, 0.dp)),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                                Text(
                                    text = stringResource(
                                        R.string.you_stop, history!![i].episodeNumber
                                    ),
                                    modifier = Modifier
                                        .padding(PaddingValues(16.dp, 16.dp, 16.dp, 0.dp)),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}