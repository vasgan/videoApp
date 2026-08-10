package com.example.myapplication.data.db.history

import androidx.room.Entity


@Entity(
    tableName = "history",
    primaryKeys = ["releaseId", "doubers", "episodeId"]
)
data class History(
    val releaseId: Int,
    val doubers: String,
    val releaseName: String,
    val episodeId: String,
    val episodeNumber: Int,
    val timeEnd: Long,
    val dateUpdate: Long,
    val imageUrl: String,
    val url: String? = null,
)
{
    companion object {
        const val ANILIBERTY = "aniliberty"
        const val DREAM_CAST = "dream_cast"
    }
}
