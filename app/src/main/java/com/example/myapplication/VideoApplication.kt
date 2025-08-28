package com.example.myapplication

import android.app.Application
import com.example.myapplication.di.component.DaggerMainComponent
import com.example.myapplication.di.component.MainComponent

class VideoApplication: Application() {
    internal lateinit var component: MainComponent
    override fun onCreate() {
        super.onCreate()
        component = DaggerMainComponent.factory().create(this)
    }
}