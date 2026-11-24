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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.network.NetworkError
import com.example.myapplication.data.db.history.History
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.ui.EmptyState
import com.example.myapplication.ui.LoadingSpinner
import com.example.myapplication.ui.ReleaseElementView
import com.example.myapplication.ui.ReleaseElementViewDreamCast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("FrequentlyChangingValue")
@Composable
fun ListScreen(
    innerPadding: PaddingValues,
    viewModel: MainViewModel,
    onItemClick: (String) -> Unit,
    onDreamCastItemClick: (DreamCastReleaseElement) -> Unit,
    onContinueWatching: (Int, Release, String) -> Unit
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
    val historyDreamCast by viewModel.uiStateDreamCastHistory.collectAsState()
    val itemsAniliberty by viewModel.uiState.collectAsState()
    val itemsDreamCast by viewModel.uiDreamCastState.collectAsState()
    val tabs = listOf(
        stringResource(R.string.aniliberty),
        stringResource(R.string.dreamcast)
    )
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.continueWatchingEvents.collect { destination ->
            onContinueWatching(destination.episode, destination.release, destination.source)
        }
    }

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
            when (page) {
                0 -> AnilibertyPage(
                    innerPadding = innerPadding,
                    state = itemsAniliberty,
                    history = historyAL,
                    listState = listAlState,
                    onRefresh = viewModel::refreshAniliberty,
                    onScrollEnd = viewModel::scrollEndAnilibria,
                    onItemClick = onItemClick,
                    onContinueWatchingClick = viewModel::continueWatching
                )

                1 -> DreamCastPage(
                    innerPadding = innerPadding,
                    state = itemsDreamCast,
                    history = historyDreamCast,
                    listState = listDcState,
                    onRefresh = viewModel::refreshDreamCast,
                    onScrollEnd = viewModel::scrollEndDC,
                    onItemClick = onDreamCastItemClick,
                    onContinueWatchingClick = viewModel::continueWatchingDreamCast
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnilibertyPage(
    innerPadding: PaddingValues,
    state: UiAnilibertyState?,
    history: History?,
    listState: LazyListState,
    onRefresh: () -> Unit,
    onScrollEnd: () -> Unit,
    onItemClick: (String) -> Unit,
    onContinueWatchingClick: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = state is LoadingFirstAl,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                is CompleteLoadAniliberty,
                is LoadingNextAl -> {
                    history?.let {
                        item {
                            ShowHistory(it, onContinueWatchingClick)
                        }
                    }
                    state.releases?.let { elements ->
                        items(elements.size) { item ->
                            if (item > elements.lastIndex - 3)
                                onScrollEnd()
                            ReleaseElementView(elements[item], onItemClick)
                        }
                    }
                    if (state is LoadingNextAl)
                        item {
                            LoadingSpinner()
                        }
                }

                is ErrorAl -> {
                    item {
                        ErrorState(
                            error = state.error,
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }

                is LoadingFirstAl -> {
                    item {
                        PageProgress(Modifier.fillParentMaxSize())
                    }
                }

                null -> Unit
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DreamCastPage(
    innerPadding: PaddingValues,
    state: UiDreamCastState?,
    history: History?,
    listState: LazyListState,
    onRefresh: () -> Unit,
    onScrollEnd: () -> Unit,
    onItemClick: (DreamCastReleaseElement) -> Unit,
    onContinueWatchingClick: () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = state is LoadingFirstDC,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                is CompleteLoadDreamCast,
                is LoadingNextDC -> {
                    history?.let {
                        item {
                            ShowHistory(it, onContinueWatchingClick)
                        }
                    }
                    state.releases?.let { list ->
                        items(list.size) { item ->
                            if (item > list.lastIndex - 3)
                                onScrollEnd()
                            ReleaseElementViewDreamCast(list[item]) {
                                onItemClick(list[item])
                            }
                        }
                    }
                    if (state is LoadingNextDC)
                        item {
                            LoadingSpinner()
                        }
                }

                is ErrorDC -> {
                    item {
                        ErrorState(
                            error = state.error,
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }

                is LoadingFirstDC -> {
                    item {
                        PageProgress(Modifier.fillParentMaxSize())
                    }
                }

                null -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.not_available_in_this_region))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PageProgress(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            strokeWidth = 4.dp
        )
    }
}

@Composable
private fun ErrorState(
    error: NetworkError,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    EmptyState(
        title = stringResource(error.titleRes()),
        modifier = modifier,
        icon = painterResource(R.drawable.baseline_history_24)
    )
}

private fun NetworkError.titleRes(): Int {
    return when (this) {
        NetworkError.ApiUnavailable -> R.string.api_unavailable
        NetworkError.NoInternet -> R.string.internet_unavailable
        is NetworkError.Other -> R.string.other_load_error
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
