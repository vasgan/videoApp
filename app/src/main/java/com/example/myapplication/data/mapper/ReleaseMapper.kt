package com.example.myapplication.data.mapper

import com.example.myapplication.data.network.responses.EpisodeResponse
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.network.responses.GenresResponse
import com.example.myapplication.data.ui_data.Episode
import com.example.myapplication.data.ui_data.Genres
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.data.ui_data.SkipTime

fun FullReleaseResponse.toRelease(dubbers: String): Release = Release(
    id = id,
    name = name.main,
    poster = poster.preview,
    description = description,
    year = "$year ${season.description}",
    genres = genres.mapToGenres(),
    ageRating = ageRating.value,
    type = "${type.description} (${episodes.size}),${averageDurationOfEpisode}мин",
    durationByEpisode = averageDurationOfEpisode.toString(),
    episodes = episodes.mapToEpisodes(),
    doubbers = dubbers
)

private fun List<GenresResponse>.mapToGenres(): List<Genres> = map { Genres(it.id, it.name) }

private fun List<EpisodeResponse>.mapToEpisodes(): List<Episode> = map {
    Episode(
        name = it.name.orEmpty(),
        id = it.id,
        ordinal = it.ordinal,
        hls480 = it.hls480.orEmpty(),
        hls720 = it.hls720.orEmpty(),
        hls1080 = it.hls1080.orEmpty(),
        durationBySec = it.duration,
        opening = SkipTime(it.opening?.start ?: 0, it.opening?.stop ?: 0),
        ending = SkipTime(it.ending?.start ?: 0, it.ending?.stop ?: 0),
        preview = it.preview.thumbnail.orEmpty(),
        sortOrder = it.sortOrder,
        releaseId = it.releaseId.toString()
    )
}
