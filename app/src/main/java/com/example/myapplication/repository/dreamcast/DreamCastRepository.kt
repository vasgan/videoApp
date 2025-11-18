package com.example.myapplication.repository.dreamcast

import com.example.myapplication.data.network.NetworkResponseWrapper
import com.example.myapplication.data.parse.dreamcast.DreamCastDescriptionAndSeries
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import kotlinx.coroutines.flow.Flow

interface DreamCastRepository {
    fun searchOnDreamCast(search: String = "", pageNumber: Int = 1): Flow<NetworkResponseWrapper<List<DreamCastReleaseElement>>>

    suspend fun getVideoUrls(urlRelease: String): DreamCastDescriptionAndSeries

    fun getReleases(pageNumber: Int = 1): Flow<List<DreamCastReleaseElement>>
}