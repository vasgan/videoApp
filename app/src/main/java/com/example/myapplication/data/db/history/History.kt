package com.example.myapplication.data.db.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "history"
)
data class History(
    @PrimaryKey
    val releaseId: Int,
    val releaseName: String,
    val episodeId: String,
    val timeEnd: Long,
    val dateUpdate: Long
)