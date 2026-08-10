package com.example.myapplication.repository.watch_list

import com.example.myapplication.data.db.watch_list.WatchListDao
import com.example.myapplication.data.db.watch_list.WatchListItem

class WatchListRepositoryImpl(val watchDB: WatchListDao) : WatchListRepository {
    override suspend fun addToWatchList(
        id: Int,
        name: String,
        imageUrl: String,
        doubers: String,
        url: String?
    ) {
        watchDB.upsert(WatchListItem(id.toString(), name, imageUrl, doubers, url))
    }

    override suspend fun removeFromWatchList(id: Int, doubers: String) {
        watchDB.deleteById(id, doubers)
    }

    override suspend fun getWatchItems(): List<WatchListItem>? {
        return watchDB.getWatchListItems()
    }

    override suspend fun getWatchItem(id: Int, doubers: String): WatchListItem? {
        return watchDB.getWatchListItem(id, doubers)
    }
}
