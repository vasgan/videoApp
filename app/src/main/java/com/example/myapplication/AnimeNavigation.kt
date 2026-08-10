package com.example.myapplication

import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.myapplication.AnimeDestinations.FAVORITE_SCREEN_ROUTE
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
import com.example.myapplication.AnimeScreens.DETAIL_RELEASE_SCREEN
import com.example.myapplication.AnimeScreens.FAVORITE_SCREEN
import com.example.myapplication.AnimeScreens.GLOBAL_SEARCH_SCREEN
import com.example.myapplication.AnimeScreens.HISTORY_SCREEN
import com.example.myapplication.AnimeScreens.MAIN_SCREEN
import com.example.myapplication.AnimeScreens.PLAYER_SCREEN
import com.example.myapplication.AnimeScreens.PROFILE_SCREEN
import com.example.myapplication.AnimeScreens.RELEASE_DREAM_CAST_SCREEN
import com.example.myapplication.AnimeScreens.SEARCH_SCREEN
import com.example.myapplication.AnimeScreens.SETTINGS_SCREEN
import com.example.myapplication.AnimeScreens.WILL_WATCH_SCREEN
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.example.myapplication.data.ui_data.Release


private object AnimeScreens {
    const val MAIN_SCREEN = "main_screen"
    const val SEARCH_SCREEN = "search_screen"
    const val DETAIL_RELEASE_SCREEN = "details_screen"
    const val PROFILE_SCREEN = "profile_screen"
    const val GLOBAL_SEARCH_SCREEN = "global_search_screen"
    const val FAVORITE_SCREEN = "favorite_screen"
    const val WILL_WATCH_SCREEN = "will_watch_screen"
    const val PLAYER_SCREEN = "player_screen"
    const val HISTORY_SCREEN = "history_screen"
    const val SETTINGS_SCREEN = "settings_screen"
    const val RELEASE_DREAM_CAST_SCREEN = "release_dreamcast_screen"
}

object AnimeDestinationsArgs {
    const val RELEASE_ID_ARG = "userMessage"
    const val GENRE_ID_ARG = "genreId"
    const val PLAYER_URLS = "urls"
    const val RELEASE_EPISODE = "release_episode"
    const val DOUBERS = "doubers"
    const val DREAMCAST_RELEASE_EPISODE = "dreamcast_release_episode"
    const val DREAMCAST_RELEASE_ID = "dreamcast_release_id"
    const val DREAMCAST_RELEASE_URL = "dreamcast_release_url"
}

object AnimeDestinations {
    const val MAIN_SCREEN_ROUTE = MAIN_SCREEN
    const val SEARCH_SCREEN_ROUTE = "$SEARCH_SCREEN?$GENRE_ID_ARG={$GENRE_ID_ARG}"
    const val PROFILE_SCREEN_ROUTE = PROFILE_SCREEN
    const val FAVORITE_SCREEN_ROUTE = FAVORITE_SCREEN
    const val GLOBAL_SEARCH_SCREEN_ROUTE = GLOBAL_SEARCH_SCREEN
    const val WILL_WATCH_SCREEN_ROUTE = WILL_WATCH_SCREEN
    const val HISTORY_SCREEN_ROUTE = HISTORY_SCREEN
    const val PROFILE_HISTORY_SCREEN_ROUTE = "$PROFILE_SCREEN/$HISTORY_SCREEN"
    const val SETTINGS_SCREEN_ROUTE = SETTINGS_SCREEN
    const val PLAYER_SCREEN_ROUTE = "$PLAYER_SCREEN?$PLAYER_URLS={$PLAYER_URLS}"
    const val RELEASE_DREAMCAST_SCREEN_ROUTE = RELEASE_DREAM_CAST_SCREEN

    fun currentReleaseRoute(rootGraph: String): String {
        return "$rootGraph/$DETAIL_RELEASE_SCREEN?$RELEASE_ID_ARG={$RELEASE_ID_ARG}"
    }

    fun currentReleaseRoute(rootGraph: String, releaseId: String): String {
        return "$rootGraph/$DETAIL_RELEASE_SCREEN?$RELEASE_ID_ARG=$releaseId"
    }

    fun dreamCastReleaseRoute(rootGraph: String): String {
        return "$rootGraph/$RELEASE_DREAM_CAST_SCREEN"
    }
}

object RootGraphs {
    const val HOME_GRAPH = "home_graph"
    const val SEARCH_GRAPH = "search_graph"
    const val GLOBAL_SEARCH_GRAPH = "global_search_graph"
    const val HISTORY_GRAPH = "history_graph"
    const val PROFILE_GRAPH = "profile_graph"
}

class AnimeNavigation(private val navController: NavController) {
    fun navigateToRelease(releaseId: String) {
        navController.navigate(AnimeDestinations.currentReleaseRoute(currentRootGraph(), releaseId)) {
            launchSingleTop = true
        }
    }

    fun navigateToDreamCastRelease(release: DreamCastReleaseElement){
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set(DREAMCAST_RELEASE_EPISODE, release)
        navController.navigate(AnimeDestinations.dreamCastReleaseRoute(currentRootGraph()))
    }

    fun navigateToDreamCastReleaseFromFavorite(id: String, url: String?){
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set(DREAMCAST_RELEASE_ID, id)
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set(DREAMCAST_RELEASE_URL, url)
        navController.navigate(AnimeDestinations.dreamCastReleaseRoute(currentRootGraph()))
    }

    fun navigateToPlayerScreen(enterEpisode: Int, release: Release, doubers: String) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set(RELEASE_EPISODE, release)
        navController.currentBackStackEntry?.savedStateHandle?.set(DOUBERS, doubers)
        navController.navigate(
            route = PLAYER_SCREEN.let {
                "$it?$PLAYER_URLS=${enterEpisode}"
            }
        )
    }

    fun navigateFromReleaseToRelease(releaseId: String) {
        navigateToRelease(releaseId)
    }

    fun navigateFromReleaseToGenreSearch(genres: String) {
        navController.navigate(
            SEARCH_SCREEN.let {
                "$it?$GENRE_ID_ARG=$genres"
            }
        )
    }

    fun navigateFromProfileToFavorite() {
        navController.navigate(FAVORITE_SCREEN_ROUTE)
    }

    fun navigateFromProfileToWillWatch() {
        navController.navigate(WILL_WATCH_SCREEN_ROUTE)
    }

    fun navigateToHistoryWatchScreen() {
        navController.navigate(AnimeDestinations.PROFILE_HISTORY_SCREEN_ROUTE)
    }

    fun navigateToSettingsScreen() {
        navController.navigate(SETTINGS_SCREEN_ROUTE)
    }

    fun navigateFromFavoriteToReleaseScreen(releaseId: String) {
        navigateToRelease(releaseId)
    }

    private fun currentRootGraph(): String {
        val hierarchy = navController.currentBackStackEntry
            ?.destination
            ?.hierarchy
            ?.mapNotNull { it.route }
            ?.toSet()
            ?: return RootGraphs.HOME_GRAPH

        return when {
            RootGraphs.SEARCH_GRAPH in hierarchy -> RootGraphs.SEARCH_GRAPH
            RootGraphs.GLOBAL_SEARCH_GRAPH in hierarchy -> RootGraphs.GLOBAL_SEARCH_GRAPH
            RootGraphs.HISTORY_GRAPH in hierarchy -> RootGraphs.HISTORY_GRAPH
            RootGraphs.PROFILE_GRAPH in hierarchy -> RootGraphs.PROFILE_GRAPH
            else -> RootGraphs.HOME_GRAPH
        }
    }
}
