package com.example.myapplication.repository.watch_list

import com.example.myapplication.data.db.watch_list.WatchListDao
import com.example.myapplication.data.db.watch_list.WatchListItem

class WatchListRepositoryImpl(val watchDB: WatchListDao) : WatchListRepository {
    override suspend fun addToWatchList(id: Int, name: String) {
        watchDB.upsert(WatchListItem(id.toString(), name))
    }

    override suspend fun removeFromWatchList(id: Int) {
        watchDB.deleteById(id)
    }

    override suspend fun getWatchItems(): List<WatchListItem>? {
        return watchDB.getWatchListItems()
    }

    override suspend fun getWatchItem(id: Int): WatchListItem? {
        return watchDB.getWatchListItem(id)
    }
}