package com.example.myapplication.main_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import com.example.myapplication.main_screen.MainViewModel
import com.example.myapplication.ui.ShimmerImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

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

@Composable
fun ReleaseElementView(item: ReleasesElementResponse, onItemClick: (String) -> Unit) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item.id) }) {
        val (image, title, description) = createRefs()
        ShimmerImage(
            "https://aniliberty.top/" + item.poster.preview,
            Modifier.constrainAs(image) {
                width = Dimension.value(120.dp)
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )
        Text(
            text = item.name.main,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(image.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .padding(PaddingValues(16.dp, 0.dp, 16.dp, 0.dp)),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = item.description + " ",//todo выпилить нормально чекать на нулл
            modifier = Modifier
                .constrainAs(description) {
                    top.linkTo(title.bottom)
                    start.linkTo(image.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .padding(PaddingValues(16.dp, 16.dp, 16.dp, 0.dp)),
            maxLines = 4
        )
    }
}