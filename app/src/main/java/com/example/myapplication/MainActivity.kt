package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.main_screen.MainViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
/*
    private val viewModel: MainViewModel by viewModels {
        viewModelFactory
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (this.applicationContext as VideoApplication).component.inject(this)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AnimeAppNavGraph(viewModelFactory = viewModelFactory)
            }
        }

    /*    viewModel.getVideo()
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val listPadding = PaddingValues(
                        16.dp,
                        innerPadding.calculateTopPadding(),
                        16.dp,
                        innerPadding.calculateBottomPadding()
                    )
                    ListScreen(listPadding, viewModel)
                }
            }
        }*/
    }
}