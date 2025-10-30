package com.example.myapplication.repository.history

import com.example.myapplication.data.db.favorite.Favorite
import com.example.myapplication.data.db.favorite.FavoriteDao
import com.example.myapplication.data.db.history.History
import com.example.myapplication.data.db.history.HistoryDao
import com.example.myapplication.repository.favorite.FavoriteRepository

class HistoryRepositoryImpl (val historyDb: HistoryDao) : HistoryRepository {


    override suspend fun addToHistory(history: History) {
        historyDb.upsert(history)
    }

    override suspend fun getHistory(): List<History>? {
        return historyDb.getHistories()
    }

    override suspend fun getHistoryRelease(releaseId: Int): History? {
        return historyDb.getHistory(releaseId)
    }
}