package com.example.myapplication.repository.watch_list

import com.example.myapplication.data.db.watch_list.WatchListItem

interface WatchListRepository {
    suspend fun addToWatchList(id: Int, name: String, imageUrl: String)

    suspend fun removeFromWatchList(id: Int)

    suspend fun getWatchItems(): List<WatchListItem>?

    suspend fun getWatchItem(id: Int): WatchListItem?
}