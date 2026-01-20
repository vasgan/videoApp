package com.example.myapplication.repository.history

import com.example.myapplication.data.db.history.History

interface HistoryRepository {
    suspend fun addToHistory(history: History)

    suspend fun getHistory(): List<History>?

    suspend fun getHistoryRelease(releaseId: Int): List<History>?

    suspend fun getLastALRelease(): History?

    suspend fun getLastDCRelease(): History?
}