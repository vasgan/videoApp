package com.example.myapplication.data.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
   @Query("SELECT * FROM video")
    fun observeAll(): Flow<List<Video>>
}