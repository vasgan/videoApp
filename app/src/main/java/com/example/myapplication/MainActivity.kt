package com.example.myapplication

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.ComponentName
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import android.util.Rational
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.myapplication.player.pip.PipActionReceiver
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import javax.inject.Inject

class MainActivity : FragmentActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var controller: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controllerListener: Player.Listener? = null
    private var isInPipMode by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (this.applicationContext as VideoApplication).component.inject(this)
        isInPipMode = isInPictureInPictureMode
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AnimeAppNavGraph(
                    viewModelFactory = viewModelFactory,
                    isInPipMode = isInPipMode,
                    PipCallBack = {
                        enterPip()
                    },
                    ConnectController = { onReady ->
                        connectController(onReady)
                    })
            }
        }
    }

    private fun connectController(onReady: (MediaController) -> Unit) {
        controller?.let {
            onReady(it)
            return
        }

        val token = SessionToken(this, ComponentName(this, com.example.myapplication.player.pip.PlayerService::class.java))
        val future = MediaController.Builder(this, token).buildAsync()
        controllerFuture = future
        future.addListener({
            if (future.isCancelled || isDestroyed) return@addListener
            val ctrl = try {
                future.get()
            } catch (exception: Exception) {
                if (exception is InterruptedException) Thread.currentThread().interrupt()
                Log.w(TAG, "Could not connect to the playback session", exception)
                return@addListener
            }
            if (isDestroyed) {
                ctrl.release()
                return@addListener
            }
            controller = ctrl
            installControllerListener(ctrl)
            onReady(ctrl)
        }, MoreExecutors.directExecutor())
    }

    private fun installControllerListener(mediaController: MediaController) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isInPipMode) updatePipParams(isPlaying)
            }
        }
        controllerListener = listener
        mediaController.addListener(listener)
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPipMode = isInPictureInPictureMode
        if (isInPictureInPictureMode) updatePipParams(controller?.isPlaying == true)
    }

    private fun enterPip(): Boolean {
        val mediaController = controller ?: return false
        if (isInPipMode || isFinishing || isDestroyed || mediaController.currentMediaItem == null) {
            return false
        }
        return enterPictureInPictureMode(createPipParams(mediaController.isPlaying))
    }

    private fun updatePipParams(isPlaying: Boolean) {
        if (isInPipMode && !isFinishing && !isDestroyed) {
            setPictureInPictureParams(createPipParams(isPlaying))
        }
    }

    private fun createPipParams(isPlaying: Boolean): PictureInPictureParams {
        return PictureInPictureParams.Builder()
            .setAspectRatio(Rational(16, 9))
            .setActions(buildPipActions(isPlaying))
            .build()
    }

    private fun buildPipActions(isPlaying: Boolean): List<RemoteAction> {
        val title = if (isPlaying) "Pause" else "Play"
        val icon = if (isPlaying) {
            androidx.media3.session.R.drawable.media3_icon_pause
        } else {
            androidx.media3.session.R.drawable.media3_icon_play
        }
        return listOf(
            pipAction(
                androidx.media3.session.R.drawable.media3_icon_previous,
                "Previous episode",
                ACTION_PIP_PREV,
                REQUEST_CODE_PREV
            ),
            pipAction(icon, title, ACTION_PIP_PLAY_PAUSE, REQUEST_CODE_PLAY_PAUSE),
            pipAction(
                androidx.media3.session.R.drawable.media3_icon_next,
                "Next episode",
                ACTION_PIP_NEXT,
                REQUEST_CODE_NEXT
            ),
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
        controllerFuture?.cancel(true)
        controller?.let { mediaController ->
            controllerListener?.let(mediaController::removeListener)
            mediaController.release()
        }
        controllerListener = null
        controller = null
        super.onDestroy()
    }

    companion object {
        const val ACTION_PIP_PLAY_PAUSE = "pip.PLAY_PAUSE"
        const val ACTION_PIP_NEXT = "pip.NEXT"
        const val ACTION_PIP_PREV = "pip.PREV"
        private const val REQUEST_CODE_PLAY_PAUSE = 1
        private const val REQUEST_CODE_PREV = 2
        private const val REQUEST_CODE_NEXT = 3
        private const val TAG = "MainActivity"
    }
}
