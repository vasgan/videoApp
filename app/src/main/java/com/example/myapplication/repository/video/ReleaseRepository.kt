package com.example.myapplication.repository.video

import com.example.myapplication.data.network.responses.BaseFranchiseResponse
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.network.responses.ReleasesElementResponse

interface ReleaseRepository {
    suspend fun getVideo(): List<ReleasesElementResponse>

    suspend fun getRelease(id: String): FullReleaseResponse?

    suspend fun getSearchWithTextFilter(id: List<Int>, text: String): List<ReleasesElementResponse>

    suspend fun getFranchises(id: String): List<BaseFranchiseResponse>?

}