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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.AnimeDestinations.FAVORITE_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.GLOBAL_SEARCH_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.HISTORY_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.MAIN_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.PLAYER_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.PROFILE_HISTORY_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.PROFILE_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.SEARCH_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.SETTINGS_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.WILL_WATCH_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinationsArgs.DOUBERS
import com.example.myapplication.AnimeDestinationsArgs.DREAMCAST_RELEASE_EPISODE
import com.example.myapplication.AnimeDestinationsArgs.DREAMCAST_RELEASE_ID
import com.example.myapplication.AnimeDestinationsArgs.DREAMCAST_RELEASE_URL
import com.example.myapplication.AnimeDestinationsArgs.GENRE_ID_ARG
import com.example.myapplication.AnimeDestinationsArgs.PLAYER_URLS
import com.example.myapplication.AnimeDestinationsArgs.RELEASE_EPISODE
import com.example.myapplication.AnimeDestinationsArgs.RELEASE_ID_ARG
import com.example.myapplication.data.db.history.History.Companion.ANILIBERTY
import com.example.myapplication.data.db.history.History.Companion.DREAM_CAST
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.example.myapplication.data.parse.dreamcast.mapToRelease
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.favorite_screen.FavoriteScreen
import com.example.myapplication.favorite_screen.FavoriteScreenViewModel
import com.example.myapplication.history_screen.HistoryScreen
import com.example.myapplication.history_screen.HistoryViewModel
import com.example.myapplication.main_screen.ListScreen
import com.example.myapplication.main_screen.MainViewModel
import com.example.myapplication.player.PlayerViewModel
import com.example.myapplication.player.PlayerScreen
import com.example.myapplication.profile_screen.ProfileScreen
import com.example.myapplication.release_dreamcast.ReleaseDreamCastScreen
import com.example.myapplication.release_dreamcast.ReleaseDreamCastViewModel
import com.example.myapplication.release_screen.ReleaseAnilibertyScreen
import com.example.myapplication.release_screen.ReleaseScreenViewModel
import com.example.myapplication.search_screen.SearchScreen
import com.example.myapplication.search_screen.SearchScreenViewModel
import com.example.myapplication.settings.SettingsScreen
import com.example.myapplication.settings.SettingsViewModel
import com.example.myapplication.watch.WatchScreen
import com.example.myapplication.watch.WatchScreenViewModel

sealed class Root(val route: String, val label: String, val icon: Int) {
    data object Home : Root(RootGraphs.HOME_GRAPH, "Home", R.drawable.baseline_home_24)
    data object Search : Root(RootGraphs.SEARCH_GRAPH, "Search", R.drawable.baseline_search_24)
    data object GlobalSearch :
        Root(
            RootGraphs.GLOBAL_SEARCH_GRAPH,
            "Global Search",
            R.drawable.baseline_global_search_explore_24
        )

    data object History : Root(RootGraphs.HISTORY_GRAPH, "History", R.drawable.baseline_history_24)
    data object Profile : Root(RootGraphs.PROFILE_GRAPH, "Profile", R.drawable.baseline_person_24)
}

@Composable
fun AnimeAppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = RootGraphs.HOME_GRAPH,
    navActions: AnimeNavigation = remember(navController) {
        AnimeNavigation(navController)
    },
    viewModelFactory: ViewModelProvider.Factory,
    PipCallBack: () -> Unit,
    ConnectController: (onReady: (MediaController) -> Unit) -> Unit
) {
    val items = listOf(Root.Home, Root.Search, Root.GlobalSearch, Root.History, Root.Profile)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDest = currentBackStackEntry?.destination
    val currentRoute = selectedDest?.route
    val noBottomBarScreens = listOf(PLAYER_SCREEN_ROUTE)
    val showBottomBar = currentRoute !in noBottomBarScreens

    Scaffold(bottomBar = {
        if (showBottomBar) {
            NavigationBar(modifier = Modifier.height(80.dp)) {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = selectedDest.isTopLevelSelected(item.route),
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
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
            navigation(
                startDestination = MAIN_SCREEN_ROUTE,
                route = RootGraphs.HOME_GRAPH
            ) {
                composable(MAIN_SCREEN_ROUTE) {
                    val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
                    ListScreen(
                        PaddingValues(
                            16.dp,
                            innerPadding.calculateTopPadding(),
                            16.dp,
                            innerPadding.calculateBottomPadding()
                        ),
                        viewModel,
                        { navActions.navigateToRelease(it) },
                        { navActions.navigateToDreamCastRelease(it) }
                    )
                }

                releaseDetailsRoutes(
                    rootGraph = RootGraphs.HOME_GRAPH,
                    innerPadding = innerPadding,
                    viewModelFactory = viewModelFactory,
                    navActions = navActions,
                    navController = navController
                )
            }

            navigation(
                startDestination = SEARCH_SCREEN_ROUTE,
                route = RootGraphs.SEARCH_GRAPH
            ) {
                composable(SEARCH_SCREEN_ROUTE) { entry ->
                    val viewModel: SearchScreenViewModel = viewModel(factory = viewModelFactory)
                    val genre = entry.arguments?.getString(GENRE_ID_ARG)
                    SearchScreen(
                        PaddingValues(
                            16.dp,
                            innerPadding.calculateTopPadding(),
                            16.dp,
                            innerPadding.calculateBottomPadding()
                        ),
                        viewModel,
                        navController,
                        genre ?: "",
                        onItemClick = {
                            navActions.navigateToRelease(it)
                        }
                    )
                }

                releaseDetailsRoutes(
                    rootGraph = RootGraphs.SEARCH_GRAPH,
                    innerPadding = innerPadding,
                    viewModelFactory = viewModelFactory,
                    navActions = navActions,
                    navController = navController
                )
            }

            navigation(
                startDestination = GLOBAL_SEARCH_SCREEN_ROUTE,
                route = RootGraphs.GLOBAL_SEARCH_GRAPH
            ) {
                composable(GLOBAL_SEARCH_SCREEN_ROUTE) {
                    //todo сделать глобальный поиск по всем релизам на шикимори
                }

                releaseDetailsRoutes(
                    rootGraph = RootGraphs.GLOBAL_SEARCH_GRAPH,
                    innerPadding = innerPadding,
                    viewModelFactory = viewModelFactory,
                    navActions = navActions,
                    navController = navController
                )
            }

            navigation(
                startDestination = HISTORY_SCREEN_ROUTE,
                route = RootGraphs.HISTORY_GRAPH
            ) {
                composable(HISTORY_SCREEN_ROUTE) {
                    HistoryRoute(viewModelFactory, navActions, navController, false)
                }

                releaseDetailsRoutes(
                    rootGraph = RootGraphs.HISTORY_GRAPH,
                    innerPadding = innerPadding,
                    viewModelFactory = viewModelFactory,
                    navActions = navActions,
                    navController = navController
                )
            }

            navigation(
                startDestination = PROFILE_SCREEN_ROUTE,
                route = RootGraphs.PROFILE_GRAPH
            ) {
                composable(PROFILE_SCREEN_ROUTE) {
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
                            navActions.navigateToHistoryWatchScreen()
                        },
                        onSettingsClick = {
                            navActions.navigateToSettingsScreen()
                        }
                    )
                }

                composable(FAVORITE_SCREEN_ROUTE) {
                    val viewModel: FavoriteScreenViewModel = viewModel(factory = viewModelFactory)
                    FavoriteScreen(
                        viewModel,
                        onItemClick = { id, doubbers, url ->
                            if (doubbers == ANILIBERTY)
                                navActions.navigateFromFavoriteToReleaseScreen(id)
                            else if (doubbers == DREAM_CAST) {
                                navActions.navigateToDreamCastReleaseFromFavorite(id, url)
                            }
                        },
                        {
                            navController.popBackStack()
                        }
                    )
                }

                composable(WILL_WATCH_SCREEN_ROUTE) {
                    val viewModel: WatchScreenViewModel = viewModel(factory = viewModelFactory)
                    WatchScreen(
                        viewModel,
                        {
                            navActions.navigateFromFavoriteToReleaseScreen(it)
                        },
                        {
                            navController.popBackStack()
                        }
                    )
                }

                composable(PROFILE_HISTORY_SCREEN_ROUTE) {
                    HistoryRoute(viewModelFactory, navActions, navController, true)
                }

                composable(SETTINGS_SCREEN_ROUTE) {
                    val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
                    SettingsScreen(viewModel) {
                        navController.popBackStack()
                    }
                }

                releaseDetailsRoutes(
                    rootGraph = RootGraphs.PROFILE_GRAPH,
                    innerPadding = innerPadding,
                    viewModelFactory = viewModelFactory,
                    navActions = navActions,
                    navController = navController
                )
            }

            composable(PLAYER_SCREEN_ROUTE) { entry ->
                val release = navController.previousBackStackEntry?.savedStateHandle?.get<Release>(
                    RELEASE_EPISODE
                )
                val doubers =
                    navController.previousBackStackEntry?.savedStateHandle?.get<String>(DOUBERS)
                val episodeId: Int? = entry.arguments?.getString(PLAYER_URLS)?.toIntOrNull()
                val viewModel: PlayerViewModel = viewModel(factory = viewModelFactory)
                release?.let {
                    doubers?.let { doubers ->
                        PlayerScreen(
                            episodeCount = episodeId ?: 0,
                            viewModel = viewModel,
                            releaseBase = release,
                            doubers = doubers,
                            onBackClick = {
                                navController.popBackStack()
                            },
                            PipCallback = PipCallBack,
                            ConnectController = ConnectController
                        )
                    }
                }
            }
        }
    }
}

private fun NavGraphBuilder.releaseDetailsRoutes(
    rootGraph: String,
    innerPadding: PaddingValues,
    viewModelFactory: ViewModelProvider.Factory,
    navActions: AnimeNavigation,
    navController: NavHostController
) {
    composable(AnimeDestinations.dreamCastReleaseRoute(rootGraph)) {
        val release =
            navController.previousBackStackEntry?.savedStateHandle?.get<DreamCastReleaseElement>(
                DREAMCAST_RELEASE_EPISODE
            )
        val viewModel: ReleaseDreamCastViewModel = viewModel(factory = viewModelFactory)
        val url = navController.previousBackStackEntry?.savedStateHandle?.get<String>(
            DREAMCAST_RELEASE_URL
        )
        val id = navController.previousBackStackEntry?.savedStateHandle?.get<String>(
            DREAMCAST_RELEASE_ID
        )
        ReleaseDreamCastScreen(
            PaddingValues(
                0.dp,
                0.dp,
                0.dp,
                innerPadding.calculateBottomPadding()
            ),
            viewModel,
            release,
            url,
            id,
            { episodeNum, dreamCastRelease, dreamCastSeries ->
                navActions.navigateToPlayerScreen(
                    episodeNum,
                    mapToRelease(dreamCastSeries, dreamCastRelease),
                    DREAM_CAST
                )
            },
            { navController.popBackStack() }
        )
    }

    composable(AnimeDestinations.currentReleaseRoute(rootGraph)) { entry ->
        val viewModel: ReleaseScreenViewModel = viewModel(factory = viewModelFactory)
        val taskId = entry.arguments?.getString(RELEASE_ID_ARG)
        taskId?.let {
            ReleaseAnilibertyScreen(
                PaddingValues(
                    0.dp,
                    0.dp,
                    0.dp,
                    innerPadding.calculateBottomPadding()
                ),
                viewModel,
                it,
                ANILIBERTY,
                onReleaseClick = { releaseId ->
                    navActions.navigateFromReleaseToRelease(releaseId)
                },
                onGenreClick = { genreId ->
                    navActions.navigateFromReleaseToGenreSearch(genreId)
                },
                onEpisodeClick = { episode, release ->
                    navActions.navigateToPlayerScreen(episode, release, ANILIBERTY)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun HistoryRoute(
    viewModelFactory: ViewModelProvider.Factory,
    navActions: AnimeNavigation,
    navController: NavHostController,
    showBackButton: Boolean
) {
    val viewModel: HistoryViewModel = viewModel(factory = viewModelFactory)
    HistoryScreen(
        viewModel,
        { id, doubbers, url ->
            if (doubbers == ANILIBERTY)
                navActions.navigateFromFavoriteToReleaseScreen(id)
            else if (doubbers == DREAM_CAST) {
                navActions.navigateToDreamCastReleaseFromFavorite(id, url)
            }
        },
        {
            navController.popBackStack()
        },
        showBackButton
    )
}

fun NavDestination?.isTopLevelSelected(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}
