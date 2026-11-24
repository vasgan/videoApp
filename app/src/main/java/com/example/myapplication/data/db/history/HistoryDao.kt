package com.example.myapplication.data.db.history

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun observeAll(): Flow<List<History>>

    @Upsert
    suspend fun upsert(history: History)

    @Query("DELETE FROM history WHERE releaseId = :id")
    suspend fun deleteById(id: Int): Int

    @Query("SELECT * FROM history WHERE releaseId = :id AND doubers = :source ORDER BY dateUpdate DESC")
    suspend fun getHistory(id: Int, source: String): List<History>?

    @Query("SELECT * FROM history")
    suspend fun getHistories(): List<History>?

    @Query("SELECT * FROM history WHERE doubers = :source ORDER BY dateUpdate DESC LIMIT 1")
    suspend fun getLastHistoryForSource(source: String): History?
}

