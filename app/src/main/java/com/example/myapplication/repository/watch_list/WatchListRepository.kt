package com.example.myapplication.repository.watch_list

import com.example.myapplication.data.db.watch_list.WatchListItem

interface WatchListRepository {
    suspend fun addToWatchList(id: Int, name: String, imageUrl: String, doubers: String, url: String? = null)

    suspend fun removeFromWatchList(id: Int, doubers: String)

    suspend fun getWatchItems(): List<WatchListItem>?

    suspend fun getWatchItem(id: Int, doubers: String): WatchListItem?
}
