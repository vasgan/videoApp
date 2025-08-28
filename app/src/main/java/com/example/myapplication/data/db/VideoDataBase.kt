package com.example.myapplication.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Video::class], version = 1, exportSchema = false)
abstract class VideoDataBase : RoomDatabase() {

    abstract fun videoDao(): VideoDao
}
