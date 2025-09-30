package com.example.myapplication.watch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WatchScreen(
    innerPaddingValues: PaddingValues,
    viewModel: WatchScreenViewModel,
    onItemClick: (String) -> Unit
) {
    val watchListItems by viewModel.watchListUiState.collectAsStateWithLifecycle()
    viewModel.getWatchRelease()
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