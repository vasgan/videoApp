package com.example.myapplication.repository.dreamcast

import android.util.Base64
import android.util.Log
import com.example.myapplication.data.network.Error
import com.example.myapplication.data.network.Loading
import com.example.myapplication.data.network.NetworkResponseWrapper
import com.example.myapplication.data.network.Success
import com.example.myapplication.data.parse.dreamcast.BaseDreamCastSearch
import com.example.myapplication.data.parse.dreamcast.DreamCastDescriptionAndSeries
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
    override fun searchOnDreamCast(search: String, pageNumber: Int): Flow<NetworkResponseWrapper<List<DreamCastReleaseElement>>> {
        return flow {
            emit(Loading())
            val result = fetchReleasesBySearch(search, pageNumber)
            emit(Success(result))
        }.catch {
            if (it is SocketTimeoutException) {

            }
            if (it is UnknownHostException) {

            }
            emit(Error(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }


    override suspend fun getVideoUrls(urlRelease: String): DreamCastDescriptionAndSeries {
        return getHtml(urlRelease)
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

    private suspend fun getHtml(url: String): DreamCastDescriptionAndSeries {
        var html = ""
        var strings = emptyList<String>()
        var descriptionText = ""
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        withContext(Dispatchers.IO) {
            val req = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Kotlin)")
                .header("Accept-Language", "ru")
                .build()

            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) error("HTTP ${resp.code}")
                html = resp.body.string() // автоматически разжимает gzip/deflate
            }

            val firstDes = html.indexOf("description\" content=\"")
            val secondDes = html.indexOf("\">", firstDes)
            descriptionText =
                html.substring(firstDes + "description\" content=\"".length, secondDes)
            val first = html.indexOf("atob(\"", secondDes)
            val second = html.indexOf("\").split('||||')", first)
            if (first < second) {
                val str = html.substring(first + 6, second)
                strings = decodeBase64(str).split("||||")
            }
        }
        return DreamCastDescriptionAndSeries(strings, descriptionText)
    }

    private fun decodeBase64(encoded: String): String {
        val decodedBytes = Base64.decode(encoded, Base64.DEFAULT)
        return String(decodedBytes, Charsets.UTF_8)
    }

}