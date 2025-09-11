package com.example.myapplication.video

import com.example.myapplication.data.db.VideoDao
import com.example.myapplication.data.network.Api
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl  @Inject constructor(
    private val localDataSource: VideoDao,
    private val dispatcher: CoroutineDispatcher,
    private val api: Api
) : VideoRepository {
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
}
