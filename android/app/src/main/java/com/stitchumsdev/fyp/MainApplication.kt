package com.stitchumsdev.fyp

import android.app.Application
import com.stitchumsdev.fyp.core.data.di.networkModule
import com.stitchumsdev.fyp.core.data.di.repositoryModule
import com.stitchumsdev.fyp.core.data.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidLogger()
            androidContext(this@MainApplication)
            modules(networkModule, repositoryModule, viewModelModule)
        }
    }
}