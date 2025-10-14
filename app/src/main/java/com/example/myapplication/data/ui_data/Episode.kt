package com.example.myapplication.data.ui_data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Episode(
    val name: String,
    val id: String,
    val ordinal: Int,
    val hls480: String,
    val hls720: String,
    val hls1080: String,
    val durationBySec: Int,
    val opening: SkipTime,
    val ending: SkipTime,
    val preview: String,
    val sortOrder: Int,
    val releaseId: String
) : Parcelable