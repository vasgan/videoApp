package com.example.myapplication.data.db.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite",
    primaryKeys = ["id", "doubers"]
)
data class Favorite (
    val id: Int,
    val name: String,
    val imageUrl: String,
    val doubers: String,
    val url: String? = null)
