package com.example.myapplication.data.db.favorite

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite")
    fun observeAll(): Flow<List<Favorite>>

    @Upsert
    suspend fun upsert(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("SELECT * FROM favorite WHERE id = :id")
    suspend fun getFavorite(id: Int): Favorite?

    @Query("SELECT * FROM favorite")
    suspend fun getFavorites(): List<Favorite>
}