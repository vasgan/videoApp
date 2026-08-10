package com.example.myapplication.data.db.watch_list

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchListDao {

    @Query("SELECT * FROM watchList")
    fun observeAll(): Flow<List<WatchListItem>>

    @Upsert
    suspend fun upsert(favorite: WatchListItem)

    @Query("DELETE FROM watchList WHERE id = :id AND doubers = :doubers")
    suspend fun deleteById(id: Int, doubers: String): Int

    @Query("SELECT * FROM watchList WHERE id = :id AND doubers = :doubers")
    suspend fun getWatchListItem(id: Int, doubers: String): WatchListItem?

    @Query("SELECT * FROM watchList")
    suspend fun getWatchListItems(): List<WatchListItem>
}
