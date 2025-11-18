package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaSession
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.myapplication.AnimeDestinations.MAIN_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.PLAYER_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.SEARCH_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinationsArgs.DOUBERS
import com.example.myapplication.AnimeDestinationsArgs.DREAMCAST_RELEASE_EPISODE
import com.example.myapplication.AnimeDestinationsArgs.GENRE_ID_ARG
import com.example.myapplication.AnimeDestinationsArgs.PLAYER_URLS
import com.example.myapplication.AnimeDestinationsArgs.RELEASE_EPISODE
import com.example.myapplication.AnimeDestinationsArgs.RELEASE_ID_ARG
import com.example.myapplication.data.db.history.History.Companion.ANILIBERTY
import com.example.myapplication.data.db.history.History.Companion.DREAM_CAST
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.example.myapplication.data.parse.dreamcast.mapToRelease
import com.example.myapplication.data.ui_data.Episode
import com.example.myapplication.data.ui_data.Franchise
import com.example.myapplication.data.ui_data.Genres
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.data.ui_data.SkipTime
import com.example.myapplication.favorite_screen.FavoriteScreen
import com.example.myapplication.favorite_screen.FavoriteScreenViewModel
import com.example.myapplication.history_screen.HistoryScreen
import com.example.myapplication.history_screen.HistoryViewModel
import com.example.myapplication.main_screen.MainViewModel
import com.example.myapplication.main_screen.ListScreen
import com.example.myapplication.player.PLayerViewModel
import com.example.myapplication.player.PlayerScreen
import com.example.myapplication.profile_screen.ProfileScreen
import com.example.myapplication.release_dreamcast.ReleaseDreamCastScreen
import com.example.myapplication.release_dreamcast.ReleaseDreamCastViewModel
import com.example.myapplication.release_screen.ReleaseScreen
import com.example.myapplication.release_screen.ReleaseScreenViewModel
import com.example.myapplication.search_screen.SearchScreen
import com.example.myapplication.search_screen.SearchScreenViewModel
import com.example.myapplication.settings.SettingsScreen
import com.example.myapplication.settings.SettingsViewModel
import com.example.myapplication.watch.WatchScreen
import com.example.myapplication.watch.WatchScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlin.String

sealed class Root(val route: String, val label: String, val icon: Int) {
    data object Home : Root(MAIN_SCREEN_ROUTE, "Home", R.drawable.baseline_home_24)
    data object Search : Root(SEARCH_SCREEN_ROUTE, "Search", R.drawable.baseline_search_24)
    data object GlobalSearch :
        Root("Gloval_search", "Global Search", R.drawable.baseline_global_search_explore_24)

    data object History : Root("profile", "Profile", R.drawable.baseline_history_24)
    data object Profile : Root("profile", "Profile", R.drawable.baseline_person_24)
}

@Composable
fun AnimeAppNavGraph(
    modifier: Modifier = Modifier,
    mediaSession: MediaSession?,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = MAIN_SCREEN_ROUTE,
    navActions: AnimeNavigation = remember(navController) {
        AnimeNavigation(navController)
    }, viewModelFactory: ViewModelProvider.Factory
) {
    val items = listOf(Root.Home, Root.Search, Root.GlobalSearch, Root.History, Root.Profile)
    val currentDestination by navController.currentBackStackEntryAsState()
    val selectedDest = currentDestination?.destination
    val noBottomBarScreens = listOf(PLAYER_SCREEN_ROUTE)
    val currentRoute = currentDestination?.destination?.route
    val showBottomBar = currentRoute in noBottomBarScreens
    Scaffold(bottomBar = {
        if (!showBottomBar) {
            NavigationBar(modifier = Modifier.height(80.dp)) {
                items.forEach { item ->
                    val selected = selectedDest.isTopLevelSelected(item.route)
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                // Сохраняем состояние предыдущих табов
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true       // не плодим дубликаты
                                restoreState = true          // восстанавливаем стек таба
                            }
                        },
                        icon = {
                            Icon(
                                painterResource(item.icon),
                                contentDescription = item.label
                            )
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
            composable(Root.Home.route) {
                val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
                ListScreen(
                    PaddingValues(
                        16.dp,
                        24.dp,
                        16.dp,
                        innerPadding.calculateBottomPadding()
                    ), viewModel, {
                        navActions.navigateToRelease(it)
                    }, {
                        navActions.navigateToDreamCastRelease(it)
                    })
            }

            composable(Root.Search.route) { entry ->
                val viewModel: SearchScreenViewModel = viewModel(factory = viewModelFactory)
                val genre = entry.arguments?.getString(GENRE_ID_ARG)
                SearchScreen(
                    PaddingValues(
                        0.dp,
                        innerPadding.calculateTopPadding(),
                        0.dp,
                        innerPadding.calculateBottomPadding()
                    ),
                    viewModel,
                    navController, genre ?: "", onItemClick = {
                        navActions.navigateToRelease(it)
                    }
                )
            }

            composable(PLAYER_SCREEN_ROUTE) { entry ->
                val release = navController.previousBackStackEntry?.savedStateHandle?.get<Release>(
                    RELEASE_EPISODE
                )
                val doubers = navController.previousBackStackEntry?.savedStateHandle?.get<String>(DOUBERS)
                val episodeId: Int? = entry.arguments?.getString(PLAYER_URLS)?.toIntOrNull()
                val viewModel: PLayerViewModel = viewModel(factory = viewModelFactory)
                release?.let {
                    doubers?.let { doubers ->
                        PlayerScreen(
                            episodeCount = episodeId ?: 0,
                            viewModel =viewModel,
                            releaseBase = release,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            mediaSession = mediaSession,
                            doubers = doubers
                        )
                    }
                }
            }

            composable(Root.History.route) { entry ->
                //todo сделать отображение истории
            }

            composable(Root.GlobalSearch.route) { entry ->
                //todo сделать глобальный поиск по всем релизам на шикимори
            }

            composable(Root.Profile.route) { entry ->
                ProfileScreen(
                    PaddingValues(
                        0.dp,
                        innerPadding.calculateTopPadding(),
                        0.dp,
                        innerPadding.calculateBottomPadding()
                    ),
                    onFavoriteClick = {
                        navActions.navigateFromProfileToFavorite()
                    },
                    onWillWatchClick = {
                        navActions.navigateFromProfileToWillWatch()
                    },
                    onHistoryClick = {
                        navActions.navigateToHistryWatchScreen()
                    },
                    onSettingsClick = {
                        navActions.navigateToSettingsScreen()
                    }
                )
            }

            composable(AnimeDestinations.RELEASE_DREAMCAST_SCREEN_ROUTE) {
                val release =
                    navController.previousBackStackEntry?.savedStateHandle?.get<DreamCastReleaseElement>(
                        DREAMCAST_RELEASE_EPISODE
                    )
                val viewModel: ReleaseDreamCastViewModel = viewModel(factory = viewModelFactory)
                release?.let {
                    ReleaseDreamCastScreen(
                        PaddingValues(
                            0.dp,
                            0.dp,
                            0.dp,
                            innerPadding.calculateBottomPadding()
                        ),
                        viewModel,
                        release,
                        {}, { episodeNum, dreamCastRelease, dreamCastSeries ->
                            navActions.navigateToPlayerScreen(episodeNum, mapToRelease(dreamCastSeries,
                                dreamCastRelease), DREAM_CAST)
                        }, {
                            navController.popBackStack()
                        })
                }
            }

            composable(AnimeDestinations.CURRENT_RELEASE_ROUTE) { entry ->
                val viewModel: ReleaseScreenViewModel = viewModel(factory = viewModelFactory)
                val taskId = entry.arguments?.getString(RELEASE_ID_ARG)
                taskId?.let {
                    ReleaseScreen(
                        PaddingValues(
                            0.dp,
                            0.dp,//innerPadding.calculateTopPadding(),
                            0.dp,
                            innerPadding.calculateBottomPadding()
                        ), viewModel, it, onReleaseClick = {
                            navActions.navigateFromReleaseToRelease(it)
                        }, onGenreClick = {
                            navActions.navigateFromReleaseToGenreSearch(it)
                        }, onEpisodeClick = { episode, release ->
                            navActions.navigateToPlayerScreen(episode, release, ANILIBERTY)
                        }, onBackClick = {
                            navController.popBackStack()
                        })
                }
            }

            composable(AnimeDestinations.WILL_WATCH_SCREEN_ROUTE) {
                val viewModel: WatchScreenViewModel = viewModel(factory = viewModelFactory)
                WatchScreen(
                    viewModel, {
                        navActions.navigateFromFavoriteToReleaseScreen(it)
                    },
                    {
                        navController.popBackStack()
                    })
            }

            composable(AnimeDestinations.FAVORITE_SCREEN_ROUTE) {
                val viewModel: FavoriteScreenViewModel = viewModel(factory = viewModelFactory)
                FavoriteScreen(
                    viewModel, {
                        navActions.navigateFromFavoriteToReleaseScreen(it)
                    }, {
                        navController.popBackStack()
                    })
            }

            composable(AnimeDestinations.SETTINGS_SCREEN_ROUTE) {
                val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
                SettingsScreen(viewModel) {
                    navController.popBackStack()
                }
            }

            composable(AnimeDestinations.HISTORY_SCREEN_ROUTE) {
                val viewModel: HistoryViewModel = viewModel(factory = viewModelFactory)
                HistoryScreen(viewModel, {
                    navActions.navigateFromFavoriteToReleaseScreen(it)
                }, {
                    navController.popBackStack()
                })
            }
        }
    }
}

fun NavDestination?.isTopLevelSelected(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}