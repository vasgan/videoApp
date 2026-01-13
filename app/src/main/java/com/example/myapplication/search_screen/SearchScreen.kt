package com.example.myapplication.search_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.ReleaseElementView
import com.example.myapplication.R

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
                placeholder = { Text(stringResource(R.string.enter_name)) },
                singleLine = true,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 16.dp)
                    .fillMaxWidth()
            )
        }
        when (items) {
            is UISearchAl.CompleteUISearchAl -> {
                items!!.releases?.let { releses ->
                    if (releses.size == 0) {
                        item {
                            Text(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                text = stringResource(R.string.nothing_found)
                            )
                        }
                    } else
                        items(releses.size) { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 8.dp, bottom = 8.dp)
                            ) {
                                ReleaseElementView(releses[item], onItemClick)
                            }
                        }
                }
            }

            is UISearchAl.ErrorUISearchAl -> {

            }

            is UISearchAl.LoadingUISearchAl -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            strokeWidth = 4.dp
                        )
                    }
                }
            }

            null -> {

            }
        }
    }
}