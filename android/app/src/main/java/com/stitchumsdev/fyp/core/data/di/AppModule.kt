package com.stitchumsdev.fyp.core.data.di

import android.app.Application
import androidx.room.Room
import com.stitchumsdev.fyp.core.data.database.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get<Application>(),
            AppDatabase::class.java,
            "appDb"
        ).build()
    }

    single { get<AppDatabase>().exhibitItemDao() }
}