package com.example.myapplication.repository.history

import com.example.myapplication.data.db.history.History
import com.example.myapplication.data.ReleaseSource
import com.example.myapplication.data.db.history.HistoryDao

class HistoryRepositoryImpl (val historyDb: HistoryDao) : HistoryRepository {


    override suspend fun addToHistory(history: History) {
        historyDb.upsert(history)
    }

    override suspend fun getHistory(): List<History>? {
        return historyDb.getHistories()
    }

    override suspend fun getHistoryRelease(releaseId: Int, source: String): List<History>? {
        return historyDb.getHistory(releaseId, source)
    }

    override suspend fun getLastALRelease(): History? {
        return historyDb.getLastHistoryForSource(ReleaseSource.Aniliberty.value)
    }

    override suspend fun getLastDCRelease(): History? {
        return historyDb.getLastHistoryForSource(ReleaseSource.DreamCast.value)
    }
}
