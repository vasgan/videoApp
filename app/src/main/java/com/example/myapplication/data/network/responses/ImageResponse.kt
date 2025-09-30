package com.example.myapplication.data.network.responses

data class ImageResponse(
    val preview: String,
    val src: String?,
    val thumbnail: String?,
    val optimized: ImageOptimizedResponse
)