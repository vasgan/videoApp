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

    override suspend fun getHistoryRelease(releaseId: Int): List<History>? {
        return historyDb.getHistory(releaseId)
    }

    override suspend fun getLastALRelease(): History? {
        val sortedList = historyDb.getHistories()
            ?.filter { ReleaseSource.fromValue(it.doubers) == ReleaseSource.Aniliberty }
        sortedList?.size?.let {
            if (it > 0)
                return sortedList[0]
        }
        return null
    }

    override suspend fun getLastDCRelease(): History? {
        val sortedList = historyDb.getHistories()
            ?.filter { ReleaseSource.fromValue(it.doubers) == ReleaseSource.DreamCast }
        sortedList?.size?.let {
            if (it > 0)
                return sortedList[0]
        }
        return null
    }
}
