package com.example.myapplication.repository.dreamcast

import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement

interface DreamCastRepository {
    suspend fun searchOnDreamCast(search: String): List<DreamCastReleaseElement>

    suspend fun getVideoUrls(urlRelease: String): List<String>

    suspend fun getReleases(): List<DreamCastReleaseElement>
}