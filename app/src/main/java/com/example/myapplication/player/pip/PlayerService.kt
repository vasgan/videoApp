package com.example.myapplication.player.pip

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.myapplication.R

class PlayerService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var session: MediaSession

    override fun onCreate() {
        super.onCreate()
        android.util.Log.e("PlayerService", "Created")
        ensureNotificationChannel()

        player = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build(),
                true
            )
            repeatMode = Player.REPEAT_MODE_OFF
        }

        session = MediaSession.Builder(this, player).build()

        // ВАЖНО: для targetSdk 36 — стартуем foreground быстро, чтобы не словить ограничения
        startForeground(NOTIF_ID, buildStubNotification())
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = session

    override fun onDestroy() {
        session.release()
        player.release()
        super.onDestroy()
    }

    private fun buildStubNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Playing")
            .setOngoing(true)
            .build()
    }

    private fun ensureNotificationChannel() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ch = NotificationChannel(
            CHANNEL_ID,
            "PlayerAnime",
            NotificationManager.IMPORTANCE_LOW
        )
        nm.createNotificationChannel(ch)
    }

    companion object {
        const val CHANNEL_ID = "playback_channel"
        const val NOTIF_ID = 1001
    }
}