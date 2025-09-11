package com.example.myapplication.release_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.ui.ShimmerImage

@Composable
fun ReleaseScreen(viewModel: ReleaseScreenViewModel, id: String) {
    viewModel.getCurrentRelease(id)
    val item by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
        ) {
            val (image, title, description) = createRefs()
            ShimmerImage(
                "https://aniliberty.top/" + item?.poster?.preview,
                Modifier.constrainAs(image) {
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
            Text(
                text = item?.name?.main + "",
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(image.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .padding(PaddingValues(16.dp, 16.dp, 16.dp, 0.dp)),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = item?.description + " ",//todo выпилить нормально чекать на нулл
                modifier = Modifier
                    .constrainAs(description) {
                        top.linkTo(title.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .padding(PaddingValues(16.dp, 16.dp, 16.dp, 0.dp)),
            )
        }
    }
}