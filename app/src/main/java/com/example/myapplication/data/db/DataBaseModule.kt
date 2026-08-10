package com.example.myapplication.data.db

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.db.favorite.FavoriteDao
import com.example.myapplication.data.db.history.HistoryDao
import com.example.myapplication.data.db.video.VideoDao
import com.example.myapplication.data.db.watch_list.WatchListDao
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class DataBaseModule {
    @Singleton
    @Provides
    fun provideDataBase(context: Context): AppDataBase {
        return Room.databaseBuilder(
            context.applicationContext, AppDataBase::class.java, "appDataBase"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Singleton
    @Provides
    fun provideVideoDao(dataBase: AppDataBase): VideoDao {
        return dataBase.videoDao()
    }

    @Singleton
    @Provides
    fun provideFavoriteDao(dataBase: AppDataBase): FavoriteDao {
        return dataBase.favoriteDao()
    }

    @Singleton
    @Provides
    fun provideWatchListDao(dataBase: AppDataBase): WatchListDao {
        return dataBase.watchListDao()
    }

    @Singleton
    @Provides
    fun provideHistoryDao(dataBase: AppDataBase): HistoryDao {
        return dataBase.historyDao()
    }
}