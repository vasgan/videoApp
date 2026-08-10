package com.example.myapplication.data.parse.dreamcast

import android.os.Parcelable
import com.example.myapplication.data.db.history.History.Companion.DREAM_CAST
import com.example.myapplication.data.ui_data.Episode
import com.example.myapplication.data.ui_data.Genres
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.data.ui_data.SkipTime
import kotlinx.parcelize.Parcelize

@Parcelize
data class DreamCastReleaseElement(
    val id: String,
    val russian: String,
    val original: String,
    val image: String,
    val wall: String,
    val description: String,
    val status: String,
    val type: String,
    val dateissue: String,
    val season: String,
    val genres: String,
    val studio: String,
    val duration: String,
    val series: String,
    val currentSeries: String,
    val dubbers: String,
    val timers: String,
    val user: String,
    val url: String,
    val views: String,
    val rating: String,
    val date: String,
    val update: String): Parcelable


fun mapToRelease(dreamCastSeries: DreamCastDescriptionAndSeries, dreamCastRelease: DreamCastReleaseElement): Release {
    val episode = mutableListOf<Episode>()
    for (i in 0..dreamCastSeries.series.size - 1) {
        episode.add(
            Episode(
                "",
                "",
                i,
                dreamCastSeries.series[i],
                dreamCastSeries.series[i],
                dreamCastSeries.series[i],
                0,
                SkipTime(0, 0),
                SkipTime(0, 0),
                dreamCastRelease.image,
                i,
                dreamCastRelease.id
            )
        )
    }
    return Release(
        dreamCastRelease.id,
        dreamCastRelease.russian + "",
        dreamCastRelease.image + "",
        dreamCastSeries.description + "",
        dreamCastRelease.date + "",
        listOf(),
        dreamCastRelease.timers + "",
        dreamCastRelease.type + "",
        dreamCastRelease.duration + "",
        episode,
        DREAM_CAST,
        dreamCastRelease.url
    )
}
