package com.example.myapplication.favorite_screen

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
fun FavoriteScreen(innerPaddingValues: PaddingValues, viewModel: FavoriteScreenViewModel, onItemClick: (String) -> Unit) {
    val favorite by viewModel.favoriteUiState.collectAsStateWithLifecycle()
    viewModel.getFavorite()
    LazyColumn(
        modifier = Modifier
            .padding(innerPaddingValues)
            .fillMaxSize()
    ) {
        favorite?.let {
            items(it.size) { i ->
                Text(
                    text = favorite!![i].name,
                    modifier = Modifier
                        .clickable {
                            onItemClick(favorite!![i].id.toString())
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