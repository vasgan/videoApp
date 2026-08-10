package com.example.myapplication.data

sealed class ReleaseSource(
    val value: String
) {
    abstract fun posterUrl(path: String): String

    data object Aniliberty : ReleaseSource(
        value = "aniliberty"
    ) {
        override fun posterUrl(path: String): String = "https://aniliberty.top/$path"
    }

    data object DreamCast : ReleaseSource(
        value = "dream_cast"
    ) {
        override fun posterUrl(path: String): String {
            return if (path.contains("://")) path else "https:$path"
        }
    }

    companion object {
        val items = listOf(Aniliberty, DreamCast)

        fun fromValue(value: String): ReleaseSource? {
            return items.firstOrNull { it.value == value }
        }

        fun posterUrl(sourceValue: String, path: String): String {
            return fromValue(sourceValue)?.posterUrl(path) ?: path
        }
    }
}