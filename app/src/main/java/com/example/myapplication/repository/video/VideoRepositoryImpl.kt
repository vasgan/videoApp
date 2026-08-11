package com.example.myapplication.repository.video

import com.example.myapplication.data.db.video.VideoDao
import com.example.myapplication.data.network.Api
import com.example.myapplication.data.network.Error
import com.example.myapplication.data.network.Loading
import com.example.myapplication.data.network.NetworkError
import com.example.myapplication.data.network.NetworkResponseWrapper
import com.example.myapplication.data.network.Success
import com.example.myapplication.data.network.request.RequestFilter
import com.example.myapplication.data.network.request.SearchRequest
import com.example.myapplication.data.network.responses.BaseFranchiseResponse
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Int
import kotlin.String
import kotlin.collections.List

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val localDataSource: VideoDao,
    private val api: Api
) : ReleaseRepository {
    override fun getReleases(page: Int): Flow<NetworkResponseWrapper<List<ReleasesElementResponse>>> {
        return flow {
            emit(Loading())
            val result = api.getReleases(page)
            if (result.isSuccessful) {
                result.body()?.let {
                    emit(Success(it.data))
                }
            } else {
                val message = result.message() ?: "Unknown error"
                emit(Error(message, networkError = NetworkError.fromHttpCode(result.code(), message)))
            }
        }.catch { e ->
            emit(Error(e.message ?: "Unexpected error", networkError = NetworkError.fromThrowable(e)))
        }
            .flowOn(Dispatchers.IO)
    }

    override fun getRelease(id: String): Flow<NetworkResponseWrapper<FullReleaseResponse>> {
        return flow {
            emit(Loading())
            emit(Success(api.getRelease(id)))
        }.catch { e ->
            emit(Error(e.message ?: "Unexpected error", networkError = NetworkError.fromThrowable(e)))
        }
            .flowOn(Dispatchers.IO)
    }

    override fun getSearchWithTextFilter(
        id: List<Int>,
        text: String
    ): Flow<NetworkResponseWrapper<List<ReleasesElementResponse>?>> {
        return flow {
            emit(Loading())
            val searchRequest = SearchRequest(
                1, 20, RequestFilter(
                    id,
                    mutableListOf(),
                    mutableListOf(),
                    null,
                    text,
                    "",
                    mutableListOf(),
                    mutableListOf(),
                    mutableListOf()
                )
            )
            val result = api.getSearchWithFilter(searchRequest).body()?.data
            emit(Success(result))
        }.catch { e ->
            emit(Error(e.message ?: "Unexpected error", networkError = NetworkError.fromThrowable(e)))
        }
            .flowOn(Dispatchers.IO)
    }

    override fun getFranchises(id: String): Flow<NetworkResponseWrapper<List<BaseFranchiseResponse>>> {
        return flow {
            emit(Loading())
            emit(Success(api.getFranchises(id)))
        }.catch { e ->
            emit(Error(e.message ?: "Unexpected error", networkError = NetworkError.fromThrowable(e)))
        }
            .flowOn(Dispatchers.IO)
    }
}
