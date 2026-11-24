package com.example.myapplication.player

import com.example.myapplication.data.ui_data.Episode
import com.example.myapplication.data.ui_data.SkipTime
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerEpisodeIndexTest {
    private val episodes = listOf(episode("first"), episode("second"))

    @Test
    fun initialIndexUsesRequestedEpisodeWhenItExists() {
        assertEquals(1, episodes.initialEpisodeIndex(1))
    }

    @Test
    fun initialIndexFallsBackToFirstEpisodeForNegativeOrTooLargeIndex() {
        assertEquals(0, episodes.initialEpisodeIndex(-1))
        assertEquals(0, episodes.initialEpisodeIndex(2))
    }

    @Test
    fun emptyEpisodesHaveNoValidIndex() {
        assertEquals(-1, emptyList<Episode>().initialEpisodeIndex(0))
        assertEquals(false, emptyList<Episode>().isValidEpisodeIndex(-1))
    }

    private fun episode(name: String) = Episode(
        name = name,
        id = name,
        ordinal = 0,
        hls480 = "https://example.com/$name.m3u8",
        hls720 = "",
        hls1080 = "",
        durationBySec = 0,
        opening = SkipTime(0, 0),
        ending = SkipTime(0, 0),
        preview = "",
        sortOrder = 0,
        releaseId = "release"
    )
}
