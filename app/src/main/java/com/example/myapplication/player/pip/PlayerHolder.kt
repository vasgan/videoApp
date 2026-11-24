package com.example.myapplication.player.pip

import androidx.media3.common.Player

object PlayerHolder {
    var player: Player? = null
    var onNext: (() -> Unit)? = null
    var onPrev: (() -> Unit)? = null
}
