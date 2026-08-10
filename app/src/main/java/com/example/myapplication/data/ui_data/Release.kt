package com.example.myapplication.data.ui_data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Release(
    val id: String,
    val name: String,
    val poster: String,
    val description: String?,
    val year: String,
    val genres: List<Genres>,
    val ageRating: String,
    val type: String,
    val durationByEpisode: String,
    val episodes: List<Episode>,
    val doubbers: String
) : Parcelable