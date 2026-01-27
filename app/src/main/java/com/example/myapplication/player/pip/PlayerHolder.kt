package com.example.myapplication.player.pip

import androidx.media3.exoplayer.ExoPlayer

object PlayerHolder {
    var player: ExoPlayer? = null
    var onNext: (() -> Unit)? = null
    var onPrev: (() -> Unit)? = null
}