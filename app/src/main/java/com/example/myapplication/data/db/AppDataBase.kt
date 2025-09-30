package com.example.myapplication.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.data.db.favorite.Favorite
import com.example.myapplication.data.db.favorite.FavoriteDao
import com.example.myapplication.data.db.video.Video
import com.example.myapplication.data.db.video.VideoDao
import com.example.myapplication.data.db.watch_list.WatchListDao
import com.example.myapplication.data.db.watch_list.WatchListItem

@Database(entities = [Video::class, Favorite::class, WatchListItem::class], version = 1, exportSchema = false)
abstract class AppDataBase: RoomDatabase() {
    abstract fun videoDao(): VideoDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun watchListDao(): WatchListDao
}