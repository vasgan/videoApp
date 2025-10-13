package com.example.myapplication.data.parse.dreamcast

data class BaseDreamCastSearch(
    val releases: List<DreamCastReleaseElement>,
    val count: String
)