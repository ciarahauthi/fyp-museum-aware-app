package com.stitchumsdev.fyp.core.data.di

import com.stitchumsdev.fyp.core.data.repository.MuseumRepository
import com.stitchumsdev.fyp.core.data.repository.MuseumRepositoryImpl
import com.stitchumsdev.fyp.core.data.repository.UserRepository
import com.stitchumsdev.fyp.core.data.repository.UserRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(api = get()) }
    single<MuseumRepository> { MuseumRepositoryImpl(appDatabase = get()) }
}