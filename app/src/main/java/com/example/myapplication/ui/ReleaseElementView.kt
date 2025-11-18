package com.example.myapplication.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.myapplication.R
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement


@Composable
fun ReleaseElementView(item: ReleasesElementResponse, onItemClick: (String) -> Unit) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item.id) }) {
        val (image, title, description) = createRefs()
        ShimmerImage(
            "https://anilibria.top/" + item.poster.preview,
            Modifier.constrainAs(image) {
                width = Dimension.value(120.dp)
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }, ContentScale.FillWidth, 8
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
        if (item.description != null && item.description != "null") {
            Text(
                text = item.description,
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
}

@Composable
fun ReleaseElementViewDreamCast(item: DreamCastReleaseElement, onItemClick: (String) -> Unit) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item.id) }) {
        val (image, title, genres, episodes) = createRefs()
        ShimmerImage(
            "https://" + item.image,
            Modifier.constrainAs(image) {
                width = Dimension.value(120.dp)
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }, ContentScale.FillWidth, 8
        )
        Text(
            text = item.russian,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(image.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .padding(PaddingValues(16.dp, 0.dp, 16.dp, 0.dp)),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 3
        )
        Text(
            text = item.genres + "",
            modifier = Modifier
                .constrainAs(genres) {
                    top.linkTo(title.bottom)
                    start.linkTo(image.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .padding(PaddingValues(16.dp, 16.dp, 16.dp, 0.dp)),
            maxLines = 4
        )
        Text(
            text = LocalContext.current.getString(
                R.string.episodes,
                item.series,
                item.currentSeries),
            modifier = Modifier
                .constrainAs(episodes) {
                    top.linkTo(genres.bottom)
                    start.linkTo(image.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .padding(PaddingValues(16.dp, 16.dp, 16.dp, 0.dp)),
            maxLines = 4
        )
    }
}