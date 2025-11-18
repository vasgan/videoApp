package com.example.myapplication.data.ui_data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Franchise(
    val id: String,
    val sortOrder: Int,
    val releaseId: String,
    val franchiseId: String,
    val releaseName: String
) : Parcelable