package com.example.myapplication.main_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.ReleaseElementView

@Composable
fun ListScreen(innerPadding: PaddingValues, viewModel: MainViewModel, onItemClick: (String) -> Unit) {
    viewModel.getVideo()
    val items by viewModel.uiState.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.size) { item ->
            ReleaseElementView(items[item], onItemClick)
        }
    }
}