package com.example.myapplication

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.AnimeDestinations.MAIN_SCREEN_ROUTE
import com.example.myapplication.AnimeDestinationsArgs.RELEASE_ID_ARG
import com.example.myapplication.main_screen.MainViewModel
import com.example.myapplication.main_screen.ListScreen
import com.example.myapplication.release_screen.ReleaseScreen
import com.example.myapplication.release_screen.ReleaseScreenViewModel
import kotlinx.coroutines.CoroutineScope


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
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(MAIN_SCREEN_ROUTE) {
            val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                val listPadding = PaddingValues(
                    16.dp,
                    innerPadding.calculateTopPadding(),
                    16.dp,
                    innerPadding.calculateBottomPadding()
                )
                ListScreen(listPadding, viewModel, {
                    navActions.navigateToRelease(it)
                })
            }
        }

        composable(AnimeDestinations.CURRENT_RELEASE_ROUTE) { entry ->
            val viewModel: ReleaseScreenViewModel = viewModel(factory = viewModelFactory)
            val taskId = entry.arguments?.getString(RELEASE_ID_ARG)
            taskId?.let {
                ReleaseScreen(viewModel, it, onReleaseClick = {
                    navActions.navigateFromReleaseToRelease(it)
                })
            }
        }
    }
}