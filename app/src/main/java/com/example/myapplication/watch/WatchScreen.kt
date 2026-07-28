package com.example.myapplication.watch

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.R
import com.example.myapplication.ui.CenterScreenProgressIndicator
import com.example.myapplication.ui.EmptyState
import com.example.myapplication.ui.SimpleTopBar

@Composable
fun WatchScreen(
    viewModel: WatchScreenViewModel,
    onItemClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val watchListItems by viewModel.watchListUiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            SimpleTopBar(R.string.views_plans) { onBackClick() }
        }, content = { innerPaddingValues ->
            when {
                watchListItems == null -> {
                    CenterScreenProgressIndicator()
                }

                watchListItems!!.isEmpty() -> {
                    EmptyState(
                        title = stringResource(R.string.empty_watch_letter),
                        modifier = Modifier
                            .padding(innerPaddingValues)
                            .fillMaxSize(),
                        icon = painterResource(R.drawable.add_to_queue_24dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPaddingValues)
                            .fillMaxSize()
                    ) {
                        watchListItems?.let {
                            items(it.size) { i ->
                                Text(
                                    text = watchListItems!![i].name,
                                    modifier = Modifier
                                        .clickable {
                                            onItemClick(watchListItems!![i].id)
                                        }
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
        })
}