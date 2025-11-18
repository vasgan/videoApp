package com.example.myapplication.repository.video

import com.example.myapplication.data.db.video.VideoDao
import com.example.myapplication.data.network.Api
import com.example.myapplication.data.network.Error
import com.example.myapplication.data.network.Loading
import com.example.myapplication.data.network.NetworkResponseWrapper
import com.example.myapplication.data.network.Success
import com.example.myapplication.data.network.request.RequestFilter
import com.example.myapplication.data.network.request.SearchRequest
import com.example.myapplication.data.network.responses.BaseFranchiseResponse
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
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
    override fun getReleases(page: Int): Flow<NetworkResponseWrapper<List<ReleasesElementResponse>>> {
        return flow{
            emit(Loading())
            val result = api.getReleases(page)
            if (result.isSuccessful) {
                result.body()?.let {
                    emit(Success(result.body()!!.data))
                }
            } else {
                emit(Error(result.message() ?: "Unknown error"))
            }
        }.catch { e ->
                emit(Error(e.message ?: "Unexpected error"))
            }
            .flowOn(Dispatchers.IO)
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
