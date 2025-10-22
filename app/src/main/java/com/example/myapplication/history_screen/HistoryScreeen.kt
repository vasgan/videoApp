package com.example.myapplication.history_screen

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.R
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
                        Text(
                            text = history!![i].releaseName,
                            modifier = Modifier
                                .clickable {
                                    onItemClick(history!![i].releaseId.toString())
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
    )
}