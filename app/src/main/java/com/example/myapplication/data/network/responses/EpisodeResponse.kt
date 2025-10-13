package com.example.myapplication.data.network.responses

import com.google.gson.annotations.SerializedName

data class EpisodeResponse (
    val id: String,
    val name: String? = null,
    val ordinal: Int,

    val opening: SegmentResponse? = null,   // {"start":110,"stop":200}
    val ending: SegmentResponse? = null,

    val preview: ImageOptimizedResponse,

    @SerializedName("hls_480")
    val hls480: String? = null,

    @SerializedName("hls_720")
    val hls720: String? = null,

    @SerializedName("hls_1080")
    val hls1080: String? = null,

    val duration: Int,
    val rutubeId: String? = null,
    val youtubeId: String? = null,

    val updatedAt: String,
    val sortOrder: Int,
    val releaseId: Int,
    val nameEnglish: String? = null
)