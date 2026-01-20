package com.example.myapplication.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class PlayerManager() {

    private var _player: ExoPlayer? = null
    val player: ExoPlayer? get() = _player

    private var lastPositionMs: Long = 0L
    private var playWhenReady: Boolean = true
    private var lastMediaItem: MediaItem? = null


    fun init(context: Context, mediaItem: MediaItem) {
        if (_player == null) {
            _player = ExoPlayer.Builder(context.applicationContext).build()
        }

        // если медиа поменялось — установи и восстанови
        if (lastMediaItem != mediaItem) {
            lastMediaItem = mediaItem
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.seekTo(lastPositionMs)
            player?.playWhenReady = playWhenReady
        }
    }

    fun saveState() {
        _player?.let {
            lastPositionMs = it.currentPosition
            playWhenReady = it.playWhenReady
        }
    }

    fun onCleared() {
        _player?.release()
        _player = null
    }
}