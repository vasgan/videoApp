package com.example.myapplication.history_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.R
import com.example.myapplication.data.ReleaseSource
import com.example.myapplication.helpers.htmlToAnnotatedString
import com.example.myapplication.ui.CenterScreenProgressIndicator
import com.example.myapplication.ui.EmptyState
import com.example.myapplication.ui.ShimmerImage
import com.example.myapplication.ui.SimpleTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onItemClick: (String, String, String?) -> Unit,
    onBackClick: () -> Unit,
    showBackButton: Boolean
) {
    val history by viewModel.historyUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            SimpleTopBar(R.string.history, showBackButton) { onBackClick() }
        }, content = { innerPaddingValues ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier
                    .padding(innerPaddingValues)
                    .fillMaxSize()
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    val currentHistory = history
                    when {
                        currentHistory == null -> {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CenterScreenProgressIndicator()
                                }
                            }
                        }

                        currentHistory.isEmpty() -> {
                            item {
                                EmptyState(
                                    title = stringResource(R.string.empty_history),
                                    modifier = Modifier.fillParentMaxSize(),
                                    icon = painterResource(R.drawable.baseline_history_24)
                                )
                            }
                        }

                        else -> {
                            items(currentHistory.size) { i ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onItemClick(
                                                currentHistory[i].releaseId.toString(),
                                                currentHistory[i].doubers,
                                                currentHistory[i].url
                                            )
                                        }) {
                                    ShimmerImage(
                                        ReleaseSource.posterUrl(
                                            currentHistory[i].doubers,
                                            currentHistory[i].imageUrl
                                        ),
                                        Modifier
                                            .width(120.dp)
                                            .wrapContentHeight()
                                            .padding(start = 16.dp, top = 8.dp, bottom = 16.dp)
                                            .fillMaxSize(),
                                        ContentScale.FillWidth, 8
                                    )
                                    Column {
                                        Text(
                                            text = htmlToAnnotatedString(currentHistory[i].releaseName),
                                            modifier = Modifier
                                                .padding(PaddingValues(16.dp, 16.dp, 16.dp, 0.dp)),
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                lineHeight = 16.sp
                                            )
                                        )
                                        Text(
                                            text = stringResource(
                                                R.string.you_stop, currentHistory[i].episodeNumber + 1
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
            }
        })
}
