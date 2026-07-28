package com.example.myapplication.favorite_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.example.myapplication.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.ui.CenterScreenProgressIndicator
import com.example.myapplication.ui.EmptyState
import com.example.myapplication.ui.ShimmerImage
import com.example.myapplication.ui.SimpleTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    viewModel: FavoriteScreenViewModel,
    onItemClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val favorite by viewModel.favoriteUiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            SimpleTopBar(R.string.favorite) { onBackClick() }
        }, content = { innerPaddingValues ->
            when {
                favorite == null -> {
                    CenterScreenProgressIndicator()
                }
                favorite!!.isEmpty() -> {
                    EmptyState(
                        title = stringResource(R.string.empty_favorite),
                        modifier = Modifier
                            .padding(innerPaddingValues)
                            .fillMaxSize(),
                        icon = painterResource( R.drawable.baseline_star_outline_24))
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPaddingValues)
                            .fillMaxSize()
                    ) {
                        favorite?.let {
                            items(it.size) { i ->
                                Row {
                                    ShimmerImage(
                                        "https://aniliberty.top/" + favorite!![i].imageUrl,
                                        Modifier
                                            .width(120.dp)
                                            .wrapContentHeight()
                                            .padding(16.dp)
                                            .fillMaxSize(),
                                        ContentScale.FillWidth, 8
                                    )
                                    Text(
                                        text = favorite!![i].name,
                                        modifier = Modifier
                                            .clickable {
                                                onItemClick(favorite!![i].id.toString())
                                            }
                                            .padding(PaddingValues(0.dp, 16.dp, 16.dp, 16.dp)),
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
    )
}