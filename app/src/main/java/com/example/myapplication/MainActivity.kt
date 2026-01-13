package com.example.myapplication

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.session.MediaSession
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val pipBuilder = PictureInPictureParams.Builder()
    private var mediaSession: MediaSession? = null
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    suspend fun functionA(): Int {
        return 10 //Random.nextInt(1, 10)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.getString(R.string.title_activity_main)
        super.onCreate(savedInstanceState)
        (this.applicationContext as VideoApplication).component.inject(this)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AnimeAppNavGraph(viewModelFactory = viewModelFactory, mediaSession = mediaSession)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pipBuilder
                .setAutoEnterEnabled(true)           // при свайпе вверх сам войдёт в PiP
                .setSeamlessResizeEnabled(true)      // плавное изменение размера (13+)
            setPictureInPictureParams(pipBuilder.build())
        }

        lifecycleScope.launch {
            val value1 = async {functionA()
            }.await()
            val value2 = async { functionA() }.await()
            Log.e("olololololo", (value1 + value2).toString())

        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ){
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        //todo исправить контролы
    }

    override fun onDestroy() {
        mediaSession?.release()
        super.onDestroy()
    }
}