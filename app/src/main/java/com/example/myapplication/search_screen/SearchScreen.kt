package com.example.myapplication.search_screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.myapplication.ui.ReleaseElementView

@Composable
fun SearchScreen(
    innerPadding: PaddingValues,
    viewModel: SearchScreenViewModel,
    nav: NavController,
    genresId: String,
    onItemClick: (String) -> Unit
) {
    val genreIds = mutableListOf<Int>()

    if (genresId.toIntOrNull() != null) {
        genreIds.add(genresId.toInt())
        viewModel.getFilterReleases(genreIds)
    }
    val items by viewModel.uiState.collectAsState()
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        item {
            var text by remember { mutableStateOf("") }
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    viewModel.searchWithFilter(it)
                },
                placeholder = { Text("Введите название") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        items(items.size) { item ->
            ReleaseElementView(items[item], onItemClick)
        }
    }
}