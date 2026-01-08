package com.stitchumsdev.fyp.core.data.di

import com.stitchumsdev.fyp.feature.test.TestScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { TestScreenViewModel(get()) }
}