package com.example.myapplication.player.pip

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.myapplication.MainActivity

class PipActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        dispatchPipAction(intent.action)
    }

    private fun dispatchPipAction(action: String?) {
        val pendingResult = goAsync()
        Handler(Looper.getMainLooper()).post {
            try {
                when (action) {
                    MainActivity.ACTION_PIP_PREV -> PlayerHolder.onPrev?.invoke()
                    MainActivity.ACTION_PIP_NEXT -> PlayerHolder.onNext?.invoke()
                    MainActivity.ACTION_PIP_PLAY_PAUSE -> PlayerHolder.player?.let { player ->
                        if (player.isPlaying) player.pause() else player.play()
                    }
                }
            } catch (exception: Exception) {
                Log.w(TAG, "Could not handle PiP action", exception)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        const val TAG = "PipActionReceiver"
    }
}
