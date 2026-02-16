package com.stitchumsdev.fyp.feature.home

import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.database.AppDatabase
import com.stitchumsdev.fyp.core.data.repository.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository,
    private val appDatabase: AppDatabase
) : BaseViewModel<HomeScreenAction>() {
    override fun onAction(action: HomeScreenAction) {}

}
