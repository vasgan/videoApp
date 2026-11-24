package com.example.myapplication.player.pip

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.cast.CastPlayer
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlayerService : MediaSessionService() {

    private lateinit var localPlayer: ExoPlayer
    private lateinit var player: CastPlayer
    private lateinit var session: MediaSession

    override fun onCreate() {
        super.onCreate()

        localPlayer = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build(),
                true
            )
            repeatMode = Player.REPEAT_MODE_OFF
        }
        player = CastPlayer.Builder(this)
            .setLocalPlayer(localPlayer)
            .build()
        PlayerHolder.player = player

        session = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = session

    override fun onDestroy() {
        session.release()
        if (PlayerHolder.player === player) PlayerHolder.player = null
        player.release()
        localPlayer.release()
        super.onDestroy()
    }

}
