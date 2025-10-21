package com.example.myapplication.di.module

import com.example.myapplication.data.db.DataBaseModule
import com.example.myapplication.data.db.favorite.FavoriteDao
import com.example.myapplication.data.db.history.HistoryDao
import com.example.myapplication.data.db.video.VideoDao
import com.example.myapplication.data.db.watch_list.WatchListDao
import com.example.myapplication.data.network.Api
import com.example.myapplication.data.network.NetworkModule
import com.example.myapplication.repository.dreamcast.DreamCastRepository
import com.example.myapplication.repository.dreamcast.DreamCastRepositoryImpl
import com.example.myapplication.repository.favorite.FavoriteRepository
import com.example.myapplication.repository.favorite.FavoriteRepositoryImpl
import com.example.myapplication.repository.history.HistoryRepository
import com.example.myapplication.repository.history.HistoryRepositoryImpl
import com.example.myapplication.repository.video.ReleaseRepository
import com.example.myapplication.repository.video.VideoRepositoryImpl
import com.example.myapplication.repository.watch_list.WatchListRepository
import com.example.myapplication.repository.watch_list.WatchListRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class, NetworkModule::class, DataBaseModule::class])
class MainModule {

    @Singleton
    @Provides
    fun provideFavoriteRepository(dao: FavoriteDao): FavoriteRepository {
        return FavoriteRepositoryImpl(dao)
    }

    @Singleton
    @Provides
    fun provideVideoRepository(dao: VideoDao, api: Api): ReleaseRepository {
        return VideoRepositoryImpl(dao, api)
    }

    @Singleton
    @Provides
    fun provideWatchListRepository(dao: WatchListDao): WatchListRepository {
        return WatchListRepositoryImpl(dao)
    }

    @Singleton
    @Provides
    fun provideHistoryRepository(dao: HistoryDao): HistoryRepository {
        return HistoryRepositoryImpl(dao)
    }

    @Singleton
    @Provides
    fun provideDreamCastRepository(): DreamCastRepository {
        return DreamCastRepositoryImpl()
    }
}
