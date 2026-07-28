package com.example.myapplication

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.ComponentName
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.example.myapplication.player.pip.PipActionReceiver
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.common.util.concurrent.MoreExecutors
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var mediaSession: MediaSession? = null

    private var controller: MediaController? = null
    private var isInPipMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (this.applicationContext as VideoApplication).component.inject(this)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AnimeAppNavGraph(
                    viewModelFactory = viewModelFactory,
                    PipCallBack = {
                        controller?.let { ctrl ->
                            enterPip(ctrl)
                        }
                    },
                    ConnectController = { onReady ->
                        connectController(onReady)
                    })
            }
        }
    }

    private fun connectController(onReady: (MediaController) -> Unit) {
        val token = SessionToken(this, ComponentName(this, com.example.myapplication.player.pip.PlayerService::class.java))
        val future = MediaController.Builder(this, token).buildAsync()
        future.addListener({
            val ctrl = future.get()
            controller = ctrl
            onReady(ctrl)
        }, MoreExecutors.directExecutor())
    }


    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPipMode = isInPictureInPictureMode
        // если хочешь — можешь тут триггерить compose state через vm/flow
    }

    private fun enterPip(ctrl: MediaController) {
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(16, 9))
            .setActions(buildPipActions())
            .build()
        enterPictureInPictureMode(params)
    }

    private fun buildPipActions(): List<RemoteAction> {
        // Prev/Next = смена серии
        return listOf(
            pipAction(androidx.media3.session.R.drawable.media3_icon_previous, "Prev", ACTION_PIP_PREV, 1),
            pipAction(androidx.media3.session.R.drawable.media3_icon_play, "PlayPause", ACTION_PIP_PLAY_PAUSE, 2),
            pipAction(androidx.media3.session.R.drawable.media3_icon_next, "Next", ACTION_PIP_NEXT, 3),
        )
    }

    private fun pipAction(iconRes: Int, title: String, action: String, requestCode: Int): RemoteAction {
        val intent = Intent(this, PipActionReceiver::class.java).setAction(action)
        val pi = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return RemoteAction(Icon.createWithResource(this, iconRes), title, title, pi)
    }

    override fun onDestroy() {
        controller?.release()
        controller = null
        super.onDestroy()
    }

    companion object {
        const val ACTION_PIP_PLAY_PAUSE = "pip.PLAY_PAUSE"
        const val ACTION_PIP_NEXT = "pip.NEXT"
        const val ACTION_PIP_PREV = "pip.PREV"
    }
}