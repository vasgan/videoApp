package com.example.myapplication.data.parse.dreamcast

import java.util.Base64

/** Parses the small subset of DreamCast HTML that the app needs. */
class DreamCastHtmlParser {

    fun parseRelease(html: String, id: String): DreamCastReleaseElement {
        val image = html.valueBetween("property=\"og:image\" content=\"", "\"")
            .orEmpty()
        val releaseSection = html.substringAfter("postInfo", html)
        val title = releaseSection.valueBetween("<h3>", "</h3>")
            .orEmpty()
        val titles = title.split(" / ", limit = 2)

        return DreamCastReleaseElement(
            id = id,
            russian = titles.firstOrNull().orEmpty(),
            original = titles.getOrNull(1).orEmpty(),
            image = image,
            wall = "",
            description = "",
            status = "",
            type = "",
            dateissue = "",
            season = "",
            genres = "",
            studio = "",
            duration = "",
            series = "",
            currentSeries = "",
            dubbers = "",
            timers = "",
            user = "",
            url = "",
            views = "",
            rating = "",
            date = "",
            update = ""
        )
    }

    fun parseDescriptionAndSeries(html: String): DreamCastDescriptionAndSeries {
        val description = html.valueBetween("description\" content=\"", "\"").orEmpty()
        val encodedSeries = html.valueBetween("atob(\"", "\").split('||||')")
        val series = encodedSeries
            ?.let(::decodeBase64)
            ?.split("||||")
            ?.filter(String::isNotBlank)
            .orEmpty()

        return DreamCastDescriptionAndSeries(series, description)
    }

    private fun decodeBase64(encoded: String): String? = runCatching {
        String(Base64.getDecoder().decode(encoded), Charsets.UTF_8)
    }.getOrNull()

    private fun String.valueBetween(start: String, end: String): String? {
        val startIndex = indexOf(start)
        if (startIndex == -1) return null

        val contentStart = startIndex + start.length
        val endIndex = indexOf(end, contentStart)
        return if (endIndex == -1) null else substring(contentStart, endIndex)
    }
}
