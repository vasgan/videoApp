package com.example.myapplication.data.ui_data

import android.os.Parcelable
import com.example.myapplication.data.network.responses.FullReleaseResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class Franchise(
    val id: String,
    val sortOrder: Int,
    val releaseId: String,
    val franchiseId: String,
    val releaseName: String
) : Parcelable