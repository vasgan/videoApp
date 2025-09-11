package com.example.myapplication

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.myapplication.AnimeDestinationsArgs.RELEASE_ID_ARG
import com.example.myapplication.AnimeScreens.DETAIL_RELEASE_SCREEN
import com.example.myapplication.AnimeScreens.MAIN_SCREEN


private object AnimeScreens {
    const val MAIN_SCREEN = "tasks"
    const val DETAIL_RELEASE_SCREEN = "details_screen"
}

object AnimeDestinationsArgs {
    const val RELEASE_ID_ARG = "userMessage"
}

object AnimeDestinations {
    const val MAIN_SCREEN_ROUTE = MAIN_SCREEN
    const val CURRENT_RELEASE_ROUTE = "$DETAIL_RELEASE_SCREEN?$RELEASE_ID_ARG={$RELEASE_ID_ARG}"
}

class AnimeNavigation(private val navController: NavHostController) {
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

}