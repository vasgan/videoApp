package com.example.myapplication.data.db.watch_list

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "watchList"
)
data class WatchListItem (
    @PrimaryKey
    val id: String,
    val name: String)

