package com.example.myapplication.player

import com.example.myapplication.data.ui_data.Episode

internal fun List<Episode>.isValidEpisodeIndex(index: Int): Boolean = index in indices

internal fun List<Episode>.initialEpisodeIndex(index: Int): Int = when {
    isEmpty() -> -1
    index in indices -> index
    else -> 0
}
