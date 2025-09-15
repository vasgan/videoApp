package com.example.myapplication.video

import com.example.myapplication.data.network.responses.BaseFranchiseResponse
import com.example.myapplication.data.network.responses.FranchiseResponse
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.network.responses.ReleasesElementResponse

interface VideoRepository {
    suspend fun getVideo(): List<ReleasesElementResponse>

    suspend fun getRelease(id: String): FullReleaseResponse?

    suspend fun getFranchises(id: String): List<BaseFranchiseResponse>?

}