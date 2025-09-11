package com.example.myapplication.di.component

import android.content.Context
import com.example.myapplication.MainActivity
import com.example.myapplication.data.network.NetworkModule
import com.example.myapplication.di.module.MainModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        MainModule::class,
        NetworkModule::class
    ],
)
interface MainComponent {

    fun inject(into: MainActivity)

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance context: Context,
        ): MainComponent
    }
}