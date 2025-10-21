package com.example.myapplication.repository.dreamcast

import android.util.Base64
import com.example.myapplication.data.parse.dreamcast.BaseDreamCastSearch
import com.example.myapplication.data.parse.dreamcast.DreamCastReleaseElement
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class DreamCastRepositoryImpl: DreamCastRepository {
    override suspend fun searchOnDreamCast(search: String): List<DreamCastReleaseElement> {
        return fetchReleasesBySearch(search)
    }

    override suspend fun getVideoUrls(urlRelease: String): List<String> {
        return getHtml(urlRelease)
    }

    private suspend fun fetchReleasesBySearch(searchString: String): List<DreamCastReleaseElement> =
        withContext(Dispatchers.IO) {
            val url = "https://dreamerscast.com/?search=$searchString&pageSize=20&pageNumber=1"
            val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("User-Agent", "Mozilla/5.0 (Kotlin client)")
                setRequestProperty("Accept-Language", "ru")
                connectTimeout = 10_000
                readTimeout = 10_000
            }
            val html = conn.inputStream.bufferedReader().use { it.readText() }
            Gson().fromJson(html, BaseDreamCastSearch::class.java).releases
        }

    private suspend fun getHtml(url: String): List<String> {
        var html = ""
        var strings = emptyList<String>()
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
            val first = html.lastIndexOf("atob(\"")
            val second = html.lastIndexOf("\").split('||||')")
            if (first < second) {
                val str = html.substring(first + 6, second)
                strings = decodeBase64(str).split("||||")
            }
        }
        return strings
    }

    private fun decodeBase64(encoded: String): String {
        val decodedBytes = Base64.decode(encoded, Base64.DEFAULT)
        return String(decodedBytes, Charsets.UTF_8)
    }

}