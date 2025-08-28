package com.example.myapplication.data.network

import androidx.annotation.Keep
import com.example.myapplication.data.network.responses.base.BaseResponseWrapper
import com.example.myapplication.video.VideoData
import retrofit2.http.GET

@Keep
interface Api {
    @GET("anime/catalog/releases")
    suspend fun getReleases(): BaseResponseWrapper<List<VideoData>>
}