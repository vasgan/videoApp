package com.example.myapplication.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

@Composable
fun ShimmerImage(url: String, modifier: Modifier = Modifier) {
    var isLoading by remember { mutableStateOf(true) }

    AsyncImage(
        contentScale = ContentScale.Crop,
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .listener(
                onSuccess = { _, _ -> isLoading = false },
                onError = { _, _ -> isLoading = false }
            )
            .build(),
        contentDescription = null,
        //todo выпилить либу и запилить кастомный шиммер
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .placeholder(
                visible = isLoading,
                highlight = PlaceholderHighlight.shimmer(),
                color = Color.LightGray
            )
    )
}