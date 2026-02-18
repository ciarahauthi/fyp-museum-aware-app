package com.stitchumsdev.fyp.core.data.di

import com.stitchumsdev.fyp.core.data.repository.BeaconRepository
import com.stitchumsdev.fyp.core.data.repository.BeaconRepositoryImpl
import org.koin.dsl.module

val beaconModule = module {
    single<BeaconRepository> { BeaconRepositoryImpl(get()) }
}