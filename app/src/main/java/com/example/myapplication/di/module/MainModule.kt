package com.example.myapplication.di.module

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.db.VideoDao
import com.example.myapplication.data.db.VideoDataBase
import com.example.myapplication.data.network.Api
import com.example.myapplication.data.network.NetworkModule
import com.example.myapplication.video.VideoRepository
import com.example.myapplication.video.VideoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class, NetworkModule::class])
class MainModule {

    @Singleton
    @Provides
    fun provideDataBase(context: Context): VideoDataBase {
        return Room.databaseBuilder(
            context.applicationContext, VideoDataBase::class.java, "videoDb"
        ).build()
    }

    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Singleton
    @Provides
    fun provideAlarmDao(dataBase: VideoDataBase): VideoDao {
        return dataBase.videoDao()
    }

    @Singleton
    @Provides
    fun provideAlarmRepository(dao: VideoDao, dispatcher: CoroutineDispatcher, api: Api): VideoRepository {
        return VideoRepositoryImpl(dao, dispatcher, api)
    }
}
