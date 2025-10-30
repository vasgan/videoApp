package com.example.myapplication.data.parse.dreamcast

import android.os.Parcelable
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