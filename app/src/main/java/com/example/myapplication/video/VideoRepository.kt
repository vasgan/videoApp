package com.example.myapplication.video

interface VideoRepository {
    suspend fun getVideo() : List<VideoData>
}