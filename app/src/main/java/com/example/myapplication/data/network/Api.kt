package com.example.myapplication.data.network

import androidx.annotation.Keep
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
}