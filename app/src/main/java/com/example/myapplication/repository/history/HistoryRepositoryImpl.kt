package com.example.myapplication.repository.history

import android.util.Log
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

    override suspend fun getHistoryRelease(releaseId: Int): List<History>? {
        return historyDb.getHistory(releaseId)
    }

    override suspend fun getLastALRelease(): History? {
        val sortedList = historyDb.getHistories()?.sortedByDescending { it.dateUpdate }
        sortedList?.size?.let {
            if (it > 0)
                return sortedList[0]
        }
        return null
    }

    override suspend fun getLastDCRelease(): History? {
        throw NullPointerException()
        return null
    }
}