package com.example.myapplication.main_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.myapplication.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.example.myapplication.ui.ReleaseElementView
import com.example.myapplication.ui.ReleaseElementViewDreamCast
import kotlinx.coroutines.launch

@Composable
fun ListScreen(
    innerPadding: PaddingValues,
    viewModel: MainViewModel,
    onItemClick: (String) -> Unit,
    onDreamCastItemClick:(DreamCastReleaseElement) -> Unit
) {
    viewModel.getAnilibertyLastReleases()
    viewModel.getDreamCastReleases()
    val itemsAniliberty by viewModel.uiState.collectAsState()
    val itemsDreamCast by viewModel.uiDreamCastState.collectAsState()
    val tabs = listOf(
        stringResource(R.string.aniliberty),
        stringResource(R.string.dreamcast)
    )
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { positions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(positions[pagerState.currentPage])
                        .height(2.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (page) {
                    0 -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = innerPadding,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(itemsAniliberty.size) { item ->
                            ReleaseElementView(itemsAniliberty[item], onItemClick)
                        }
                    }

                    1 -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = innerPadding,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(itemsDreamCast.size) { item ->
                            ReleaseElementViewDreamCast(itemsDreamCast[item]) {
                                onDreamCastItemClick(itemsDreamCast[item])
                             //   viewModel.getDreamCastUrls("https://dreamerscast.com/" + itemsDreamCast[item].url)
                            }
                        }
                    }
                }
            }
        }
    }
}