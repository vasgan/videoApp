package com.example.myapplication.data.db.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite"
)
data class Favorite (
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String)
