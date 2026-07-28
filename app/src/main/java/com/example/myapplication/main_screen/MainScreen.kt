package com.example.myapplication.main_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.db.history.History
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.example.myapplication.ui.LoadingSpinner
import com.example.myapplication.ui.ReleaseElementView
import com.example.myapplication.ui.ReleaseElementViewDreamCast
import kotlinx.coroutines.launch

@SuppressLint("FrequentlyChangingValue")
@Composable
fun ListScreen(
    innerPadding: PaddingValues,
    viewModel: MainViewModel,
    onItemClick: (String) -> Unit,
    onDreamCastItemClick: (DreamCastReleaseElement) -> Unit
) {
    val listAlState = rememberSaveable(
        saver = LazyListState.Saver
    ) {
        LazyListState()
    }
    val listDcState = rememberSaveable(
        saver = LazyListState.Saver
    ) {
        LazyListState()
    }

    val historyAL by viewModel.uiStateHistory.collectAsState()
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
                    0 ->
                        when (itemsAniliberty) {
                            is CompleteLoadAniliberty,
                            is LoadingNextAl ->
                                LazyColumn(
                                    state = listAlState,
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = innerPadding,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    historyAL?.let {
                                        item {
                                            ShowHistory(it, {

                                            })
                                        }
                                    }
                                    itemsAniliberty?.releases?.let { elements ->
                                        items(elements.size) { item ->
                                            if (item > elements.lastIndex - 3)
                                                viewModel.scrollEndAnilibria()
                                            ReleaseElementView(elements[item], onItemClick)
                                        }
                                    }
                                    if (itemsAniliberty is LoadingNextAl)
                                        items(1) {
                                            LoadingSpinner()
                                        }
                                }

                            is ErrorAl -> {}
                            is LoadingFirstAl -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    strokeWidth = 4.dp
                                )
                            }

                            null -> {}
                        }

                    1 -> {
                        when (itemsDreamCast) {
                            is LoadingNextDC,
                            is CompleteLoadDreamCast -> {
                                LazyColumn(
                                    state = listDcState,
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = innerPadding,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    itemsDreamCast?.releases?.let { list ->
                                        items(list.size) { item ->
                                            if (item > list.lastIndex - 3)
                                                viewModel.scrollEndDC()
                                            ReleaseElementViewDreamCast(list[item]) {
                                                onDreamCastItemClick(list[item])
                                            }
                                        }
                                        if (itemsDreamCast is LoadingNextDC)
                                            items(1) {
                                                LoadingSpinner()
                                            }
                                    }
                                }
                            }

                            is ErrorDC -> {}//TODO(сделать обработку ошибок)
                            is LoadingFirstDC -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    strokeWidth = 4.dp
                                )
                            }

                            null ->
                                Text(stringResource(R.string.not_available_in_this_region))
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("StringFormatInvalid")
@Composable
fun ShowHistory(history: History, onContinueWatchingClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text(LocalContext.current.getString(R.string.your_stop, history.releaseName))
        FilledTonalButton(modifier = Modifier.fillMaxWidth(), onClick = {
            onContinueWatchingClick()
        }) {
            Text(LocalContext.current.getString(R.string.continue_watching))

        }
    }
}
