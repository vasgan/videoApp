package com.example.myapplication.repository.video

import com.example.myapplication.data.db.video.VideoDao
import com.example.myapplication.data.network.Api
import com.example.myapplication.data.network.request.RequestFilter
import com.example.myapplication.data.network.request.SearchRequest
import com.example.myapplication.data.network.responses.BaseFranchiseResponse
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Int
import kotlin.String
import kotlin.collections.List

@Singleton
class VideoRepositoryImpl  @Inject constructor(
    private val localDataSource: VideoDao,
    private val api: Api
) : ReleaseRepository {
    override suspend fun getVideo(): List<ReleasesElementResponse> {
        val result = api.getReleases().body()?.data
        result?.let {
            return result
        }
        return mutableListOf()
    }

    override suspend fun getRelease(id: String): FullReleaseResponse? {
        val result = api.getRelease(id)
        return result
    }

    override suspend fun getSearchWithTextFilter(id: List<Int>, text: String): List<ReleasesElementResponse> {
        val searchRequest = SearchRequest(1, 20, RequestFilter(   id,
            mutableListOf(),
            mutableListOf(),
            null,
        text,
        "",
                mutableListOf(),
            mutableListOf(),
        mutableListOf()))
        val result = api.getSearchWithFilter(searchRequest).body()?.data
        result?.let {
            return result
        }
        return mutableListOf()
    }

    override suspend fun getFranchises(id: String): List<BaseFranchiseResponse>{
        val result = api.getFranchises(id)
        return result
    }
}
