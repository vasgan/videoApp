package com.example.myapplication

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.myapplication.AnimeDestinations.FAVORITE_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.HISTORY_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.SETTINGS_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinations.WILL_WATCH_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinationsArgs.GENRE_ID_ARG
import com.example.myapplication.AnimeDestinationsArgs.PLAYER_URLS
import com.example.myapplication.AnimeDestinationsArgs.RELEASE_EPISODE
import com.example.myapplication.AnimeDestinationsArgs.RELEASE_ID_ARG
import com.example.myapplication.AnimeScreens.DETAIL_RELEASE_SCREEN
import com.example.myapplication.AnimeScreens.FAVORITE_SCREEN
import com.example.myapplication.AnimeScreens.HISTORY_SCREEN
import com.example.myapplication.AnimeScreens.MAIN_SCREEN
import com.example.myapplication.AnimeScreens.PLAYER_SCREEN
import com.example.myapplication.AnimeScreens.PROFILE_SCREEN
import com.example.myapplication.AnimeScreens.SEARCH_SCREEN
import com.example.myapplication.AnimeScreens.SETTINGS_SCREEN
import com.example.myapplication.AnimeScreens.WILL_WATCH_SCREEN
import com.example.myapplication.data.ui_data.Release


private object AnimeScreens {
    const val MAIN_SCREEN = "main_screen"
    const val SEARCH_SCREEN = "search_screen"
    const val DETAIL_RELEASE_SCREEN = "details_screen"
    const val PROFILE_SCREEN = "profile_screen"
    const val FAVORITE_SCREEN = "favorite_screen"
    const val WILL_WATCH_SCREEN = "will_watch_screen"
    const val PLAYER_SCREEN = "player_screen"
    const val HISTORY_SCREEN = "history_screen"
    const val SETTINGS_SCREEN = "settings_screen"
}

object AnimeDestinationsArgs {
    const val RELEASE_ID_ARG = "userMessage"
    const val GENRE_ID_ARG = "genreId"
    const val PLAYER_URLS = "urls"
    const val RELEASE_EPISODE = "release_episode"
}

object AnimeDestinations {
    const val MAIN_SCREEN_ROUTE = MAIN_SCREEN
    const val SEARCH_SCREEN_ROUTE = "$SEARCH_SCREEN?$GENRE_ID_ARG={$GENRE_ID_ARG}"
    const val CURRENT_RELEASE_ROUTE = "$DETAIL_RELEASE_SCREEN?$RELEASE_ID_ARG={$RELEASE_ID_ARG}"
    const val PROFILE_SCREEN_ROUTE = PROFILE_SCREEN
    const val FAVORITE_SCREEN_ROUTE = FAVORITE_SCREEN
    const val WILL_WATCH_SCREEN_ROUTE = WILL_WATCH_SCREEN
    const val HISTORY_SCREEN_ROUTE = HISTORY_SCREEN
    const val SETTINGS_SCREEN_ROUTE = SETTINGS_SCREEN
    const val PLAYER_SCREEN_ROUTE = "$PLAYER_SCREEN?$PLAYER_URLS={$PLAYER_URLS}"
}

class AnimeNavigation(private val navController: NavController) {
    fun navigateToRelease(releaseId: String = "") {
        navController.navigate(
            DETAIL_RELEASE_SCREEN.let {
                if (releaseId != "") "$it?$RELEASE_ID_ARG=$releaseId" else it
            }
        ) {
            popUpTo(navController.graph.findStartDestination().id)
            launchSingleTop = true
        }
    }

    fun navigateToPlayerScreen(enterEpisode: Int, release: Release) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set(RELEASE_EPISODE, release)
        navController.navigate(
            route = PLAYER_SCREEN.let {
                "$it?$PLAYER_URLS=${enterEpisode}"
            }
        )
    }

    fun navigateFromReleaseToRelease(releaseId: String = "") {
        navController.navigate(
            DETAIL_RELEASE_SCREEN.let {
                if (releaseId != "") "$it?$RELEASE_ID_ARG=$releaseId" else it
            }
        )
    }

    fun navigateFromReleaseToGenreSearch(genres: String = "") {
        navController.navigate(
            SEARCH_SCREEN.let {
                if (genres != "") "$it?$GENRE_ID_ARG=$genres" else it
            }
        )
    }

    fun navigateFromProfileToFavorite() {
        navController.navigate(FAVORITE_SCREEN_ROUTE)
    }

    fun navigateFromProfileToWillWatch() {
        navController.navigate(WILL_WATCH_SCREEN_ROUTE)
    }

    fun navigateToHistryWatchScreen() {
        navController.navigate(HISTORY_SCREEN_ROUTE)
    }

    fun navigateToSettingsScreen() {
        navController.navigate(SETTINGS_SCREEN_ROUTE)
    }

    fun navigateFromFavoriteToReleaseScreen(releaseId: String = "") {
        navController.navigate(
            DETAIL_RELEASE_SCREEN.let {
                if (releaseId != "") "$it?$RELEASE_ID_ARG=$releaseId" else it
            }
        )
    }
}