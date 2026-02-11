package com.stitchumsdev.fyp.core.data.di

import com.stitchumsdev.fyp.feature.home.HomeViewModel
import com.stitchumsdev.fyp.feature.map.MapViewModel
import com.stitchumsdev.fyp.feature.route.RouteViewModel
import com.stitchumsdev.fyp.feature.scan.ScanViewModel
import com.stitchumsdev.fyp.feature.test.TestScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { TestScreenViewModel(get()) }
    viewModel { ScanViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { MapViewModel(get()) }
    viewModel { RouteViewModel(get()) }
}