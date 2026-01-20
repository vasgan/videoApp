package com.example.myapplication.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.myapplication.player.error.PlayerNotInitialization

class PlayerManager {
    private var _player: ExoPlayer? = null
    val player: ExoPlayer? get() = _player

    private var lastPositionMs: Long = 0L
    private var playWhenReady: Boolean = true
    private var lastMediaItem: MediaItem? = null

    fun initPlayer(context: Context, url: String) {
        if (_player == null) {
            _player = ExoPlayer.Builder(context.applicationContext).build()
        }
        val mediaItem = MediaItem.fromUri(url)
        if (lastMediaItem != mediaItem) {
            lastMediaItem = mediaItem
            _player?.setMediaItem(mediaItem)
            _player?.prepare()
            _player?.seekTo(lastPositionMs)
            _player?.playWhenReady = playWhenReady
        }
    }

    fun seekForward(position: Long) {
        checkInit()
        _player?.seekTo(position)
    }

    fun play() {
        checkInit()
        _player?.let {
            if (it.isPlaying)
                it.pause()
            else
                it.play()
        }
    }

    fun speedX() {
        checkInit()
    }

    fun changeEpisode(url: String) {
        checkInit()
        val mediaItem = MediaItem.fromUri(url)
        if (lastMediaItem != mediaItem) {
            lastMediaItem = mediaItem
            _player?.setMediaItem(mediaItem)
            _player?.prepare()
            _player?.seekTo(lastPositionMs)
            _player?.playWhenReady = playWhenReady
        }
    }

    private fun checkInit() {
        if (_player == null) throw PlayerNotInitialization()
    }

    fun endPlay() {
        _player?.release()
    }
}