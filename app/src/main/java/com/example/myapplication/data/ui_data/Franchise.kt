package com.example.myapplication.data.ui_data

import com.example.myapplication.data.network.responses.FullReleaseResponse

data class Franchise(
    val id: String,
    val sortOrder: Int,
    val releaseId: String,
    val franchiseId: String,
    val releaseName: String
)