package com.example.myapplication.data.db.watch_list

import androidx.room.Entity

@Entity(
    tableName = "watchList",
    primaryKeys = ["id", "doubers"]
)
data class WatchListItem (
    val id: String,
    val name: String,
    val imageUrl: String,
    val doubers: String,
    val url: String? = null,
)
