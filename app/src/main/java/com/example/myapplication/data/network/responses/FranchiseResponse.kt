package com.example.myapplication.data.network.responses

import com.google.gson.annotations.SerializedName

data class FranchiseResponse(
    val id: String?,
    val sortOrder: Int?,
    @SerializedName("release_id")
    val releaseId: String?,
    @SerializedName("franchised_id")
    val franchiseId: String?,
    val release: FullReleaseResponse?
)