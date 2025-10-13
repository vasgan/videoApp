package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    /*    private val handler = CoroutineExceptionHandler { _, e ->
            Log.e("Main", "Unhandled coroutine error", e)
        }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        this.getString(R.string.title_activity_main)
        super.onCreate(savedInstanceState)
        (this.applicationContext as VideoApplication).component.inject(this)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AnimeAppNavGraph(viewModelFactory = viewModelFactory)
            }
        }
        lifecycleScope.launch(/*handler*/) {
          //  getHtml("https://dreamerscast.com/home/release/454-tensei-shitara-dainana-ouji-datta-node-kimama-ni-majutsu-wo-kiwamemasu-2nd-season")
            //val releases = withTimeout(15_000) { fetchReleasesByGenre("пере") }
//            Log.d("Main", "Found ${releases.size} releases")
        }
    }
}