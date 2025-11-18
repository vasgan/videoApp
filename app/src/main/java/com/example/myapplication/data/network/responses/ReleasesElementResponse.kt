package com.example.myapplication.data.network.responses

data class ReleasesElementResponse(
    val id: String,
    val year: String,
    val poster: ImageResponse,
    val isOngoing: Boolean,
    val name: NameResponse,
    val description: String?,
    val episodesTotal: String,
    val genres: List<GenresResponse>
)