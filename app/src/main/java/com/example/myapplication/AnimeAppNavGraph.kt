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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.myapplication.AnimeDestinations.MAIN_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.SEARCH_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinationsArgs.GENRE_ID_ARG
import com.example.myapplication.AnimeDestinationsArgs.RELEASE_ID_ARG
import com.example.myapplication.favorite_screen.FavoriteScreen
import com.example.myapplication.favorite_screen.FavoriteScreenViewModel
import com.example.myapplication.main_screen.MainViewModel
import com.example.myapplication.main_screen.ListScreen
import com.example.myapplication.profile_screen.ProfileScreen
import com.example.myapplication.release_screen.ReleaseScreen
import com.example.myapplication.release_screen.ReleaseScreenViewModel
import com.example.myapplication.search_screen.SearchScreen
import com.example.myapplication.search_screen.SearchScreenViewModel
import com.example.myapplication.watch.WatchScreen
import com.example.myapplication.watch.WatchScreenViewModel
import kotlinx.coroutines.CoroutineScope

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
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = MAIN_SCREEN_ROUTE,
    navActions: AnimeNavigation = remember(navController) {
        AnimeNavigation(navController)
    }, viewModelFactory: ViewModelProvider.Factory
) {
    val items = listOf(Root.Home, Root.Search, Root.GlobalSearch, Root.History, Root.Profile)
    // Текущая destination для подсветки выбранной вкладки
    val currentDestination by navController.currentBackStackEntryAsState()
    val selectedDest = currentDestination?.destination
    Scaffold(bottomBar = {
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
                        innerPadding.calculateTopPadding(),
                        16.dp,
                        innerPadding.calculateBottomPadding()
                    ), viewModel, {
                        navActions.navigateToRelease(it)
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

            composable(Root.History.route) { entry ->

            }

            composable(Root.GlobalSearch.route) { entry ->
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
                    }
                )
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
                        })
                }
            }

            composable(AnimeDestinations.WILL_WATCH_SCREEN_ROUTE) {
                val viewModel: WatchScreenViewModel = viewModel(factory = viewModelFactory)
                WatchScreen(
                    PaddingValues(
                        0.dp,
                        innerPadding.calculateTopPadding(),
                        0.dp,
                        innerPadding.calculateBottomPadding()
                    ), viewModel, {
                        navActions.navigateFromFavoriteToReleaseScreen(it)
                    })
            }

            composable(AnimeDestinations.FAVORITE_SCREEN_ROUTE) {
                val viewModel: FavoriteScreenViewModel = viewModel(factory = viewModelFactory)
                FavoriteScreen(
                    PaddingValues(
                        0.dp,
                        innerPadding.calculateTopPadding(),
                        0.dp,
                        innerPadding.calculateBottomPadding()
                    ), viewModel, {
                        navActions.navigateFromFavoriteToReleaseScreen(it)
                    })
            }
        }
    }
}

fun NavDestination?.isTopLevelSelected(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}