package com.example.myapplication.player.pip

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.myapplication.MainActivity
import com.google.common.util.concurrent.MoreExecutors
import kotlin.jvm.java

class PipActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val token = SessionToken(context, ComponentName(context, PlayerService::class.java))
        val future = MediaController.Builder(context, token).buildAsync()

        future.addListener({
            val ctrl = future.get()
            when (intent.action) {
                MainActivity.ACTION_PIP_PLAY_PAUSE -> {
                    if (ctrl.isPlaying) ctrl.pause() else ctrl.play()
                }
                MainActivity.ACTION_PIP_NEXT -> ctrl.seekToNextMediaItem()
                MainActivity.ACTION_PIP_PREV -> ctrl.seekToPreviousMediaItem()
            }
            ctrl.release()
        }, MoreExecutors.directExecutor())
    }
}