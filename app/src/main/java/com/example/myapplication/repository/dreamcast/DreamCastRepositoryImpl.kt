package com.example.myapplication.repository.dreamcast

import com.example.myapplication.data.network.Error
import com.example.myapplication.data.network.Loading
import com.example.myapplication.data.network.NetworkError
import com.example.myapplication.data.network.NetworkResponseWrapper
import com.example.myapplication.data.network.Success
import com.example.myapplication.data.parse.dreamcast.BaseDreamCastSearch
import com.example.myapplication.data.parse.dreamcast.DreamCastDescriptionAndSeries
import com.example.myapplication.data.parse.dreamcast.DreamCastHtmlParser
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class DreamCastRepositoryImpl : DreamCastRepository {
    private val htmlParser = DreamCastHtmlParser()

    override fun searchOnDreamCast(search: String, pageNumber: Int): Flow<NetworkResponseWrapper<List<DreamCastReleaseElement>>> {
        return flow {
            emit(Loading())
            val result = fetchReleasesBySearch(search, pageNumber)
            emit(Success(result))
        }.catch {
            emit(
                Error(
                    it.message.toString(),
                    networkError = NetworkError.fromThrowable(it)
                )
            )
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getReleaseById(urlRelease: String, id: String): DreamCastReleaseElement {
        return getDreamCastReleaseByUrl(urlRelease, id)
    }

    override suspend fun getVideoUrls(urlRelease: String): DreamCastDescriptionAndSeries {
        return getDreamCastDescriptionAndSeries(urlRelease)
    }

    override fun getReleases(pageNumber: Int): Flow<List<DreamCastReleaseElement>> {
        return flow { fetchReleasesBySearch("", pageNumber) }
    }

    private suspend fun fetchReleasesBySearch(searchString: String, pageNumber: Int): List<DreamCastReleaseElement> =
        withContext(Dispatchers.IO) {
            val url = "https://dreamerscast.com/?search=$searchString&pageNumber=$pageNumber"
            val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("User-Agent", "Mozilla/5.0 (Kotlin client)")
                setRequestProperty("Accept-Language", "ru")
                connectTimeout = 5_000
                readTimeout = 5_000
            }
            val html = conn.inputStream.bufferedReader().use { it.readText() }
            Gson().fromJson(html, BaseDreamCastSearch::class.java).releases
        }

    private suspend fun getDreamCastReleaseByUrl(url: String, id: String): DreamCastReleaseElement {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        return withContext(Dispatchers.IO) {
            val req = Request.Builder()
                .url(dreamCastUrl(url))
                .header("User-Agent", "Mozilla/5.0 (Kotlin)")
                .header("Accept-Language", "ru")
                .build()

            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) error("HTTP ${resp.code}")
                htmlParser.parseRelease(resp.body.string(), id)
            }
        }
    }

    private suspend fun getDreamCastDescriptionAndSeries(url: String): DreamCastDescriptionAndSeries {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        return withContext(Dispatchers.IO) {
            val req = Request.Builder()
                .url(dreamCastUrl(url))
                .header("User-Agent", "Mozilla/5.0 (Kotlin)")
                .header("Accept-Language", "ru")
                .build()

            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) error("HTTP ${resp.code}")
                htmlParser.parseDescriptionAndSeries(resp.body.string())
            }
        }
    }

    private fun dreamCastUrl(pathOrUrl: String): String =
        if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
            pathOrUrl
        } else {
            "https://dreamerscast.com/$pathOrUrl"
        }
}
