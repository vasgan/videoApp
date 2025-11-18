package com.example.myapplication.repository.video

import com.example.myapplication.data.network.NetworkResponseWrapper
import com.example.myapplication.data.network.responses.BaseFranchiseResponse
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import kotlinx.coroutines.flow.Flow

interface ReleaseRepository {
    fun getReleases(page: Int): Flow<NetworkResponseWrapper<List<ReleasesElementResponse>>>

    fun getRelease(id: String): Flow<NetworkResponseWrapper<FullReleaseResponse>>

    suspend fun getSearchWithTextFilter(id: List<Int>, text: String): List<ReleasesElementResponse>

    fun getFranchises(id: String): Flow<NetworkResponseWrapper<List<BaseFranchiseResponse>>>

}