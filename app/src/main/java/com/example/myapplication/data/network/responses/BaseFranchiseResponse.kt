package com.example.myapplication.data.network.responses

import com.google.gson.annotations.SerializedName

data class BaseFranchiseResponse (
    @SerializedName("franchise_releases") val franchiseReleases: List<FranchiseResponse>?
)