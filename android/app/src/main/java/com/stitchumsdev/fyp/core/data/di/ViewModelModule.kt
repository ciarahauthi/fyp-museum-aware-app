package com.stitchumsdev.fyp.core.data.di

import com.stitchumsdev.fyp.feature.home.HomeViewModel
import com.stitchumsdev.fyp.feature.map.MapViewModel
import com.stitchumsdev.fyp.feature.route.RouteViewModel
import com.stitchumsdev.fyp.feature.scan.ScanViewModel
import com.stitchumsdev.fyp.feature.search.SearchViewModel
import com.stitchumsdev.fyp.feature.splash.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ScanViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { MapViewModel(get(), get()) }
    viewModel { RouteViewModel(get(), get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { SplashViewModel(get(), get(), get()) }
}