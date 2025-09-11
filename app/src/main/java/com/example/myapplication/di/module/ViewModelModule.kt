package com.example.myapplication.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.di.ViewModelFactory
import com.example.myapplication.di.ViewModelKey
import com.example.myapplication.main_screen.MainViewModel
import com.example.myapplication.release_screen.ReleaseScreenViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReleaseScreenViewModel::class)
    abstract fun bindReleaseScreenViewModel(mainViewModel: ReleaseScreenViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}