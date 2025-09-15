package com.example.myapplication.data.network

import androidx.annotation.Keep
import androidx.room.Query
import com.example.myapplication.data.network.responses.BaseFranchiseResponse
import com.example.myapplication.data.network.responses.FranchiseResponse
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import com.example.myapplication.data.network.responses.base.BaseResponseWrapper
import retrofit2.http.GET
import retrofit2.http.Path

@Keep
interface Api {
    @GET("anime/catalog/releases")
    suspend fun getReleases(): BaseResponseWrapper<List<ReleasesElementResponse>>

    @GET("anime/releases/{id}")
    suspend fun getRelease(@Path("id") id: String): FullReleaseResponse

    @GET("anime/franchises/release/{id}?include=franchise_releases")
    suspend fun getFranchises(@Path("id")id: String): List<BaseFranchiseResponse>
//    https://aniliberty.top/api/v1/anime/franchises/release/10002?include=franchise_releases
}