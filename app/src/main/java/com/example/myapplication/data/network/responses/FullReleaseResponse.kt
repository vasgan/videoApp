package com.example.myapplication.data.network.responses

import com.google.gson.annotations.SerializedName

data class FullReleaseResponse(
    val id: String,
    val type: SourceViewResponse,
    val year: String,
    val name: NameResponse,
    val alias: String,
    val season: SeasonResponse,
    val poster: ImageResponse,
    val freshAt: String,
    val createdAt: String,
    val updatedAt: String,
    val isOngoing: Boolean,

    @SerializedName("age_rating")
    val ageRating: AgeRatingResponse,
    val publishDay: PublishDayResponse,

    val description: String,
    val notification: String? = null,
    val episodesTotal: Int? = null,
    val externalPlayer: String? = null,
    val isInProduction: Boolean,
    val isBlockedByGeo: Boolean,
    val isBlockedByCopyrights: Boolean,

    val addedInUsersFavorites: Int,
    @SerializedName("average_duration_of_episode")
    val averageDurationOfEpisode: Int,
    val addedInPlannedCollection: Int,
    val addedInWatchedCollection: Int,
    val addedInWatchingCollection: Int,
    val addedInPostponedCollection: Int,
    val addedInAbandonedCollection: Int,

    val genres: List<GenresResponse>,
    val members: List<MemberResponse>,
    //val sponsor: Sponsor? = null,
    val episodes: List<EpisodeResponse> = emptyList(),
    //val torrents: List<Torrent> = emptyList()
)