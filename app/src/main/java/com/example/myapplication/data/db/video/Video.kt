package com.example.myapplication.data.db.video

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "video"
)

data class Video (
    @PrimaryKey
    val id: String,
    val name: String,
    var url: Boolean
)