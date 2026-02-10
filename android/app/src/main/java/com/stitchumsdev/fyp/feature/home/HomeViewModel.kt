package com.stitchumsdev.fyp.feature.home

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.database.AppDatabase
import com.stitchumsdev.fyp.core.data.repository.UserRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val userRepository: UserRepository,
    private val appDatabase: AppDatabase
) : BaseViewModel<HomeScreenAction>() {
    override fun onAction(action: HomeScreenAction) {
        when (action) {
            HomeScreenAction.GetAllExhibits -> getAllExhibits()
            HomeScreenAction.GetAllLocations -> getAllLocations()
        }
    }

    // ToDo: Move API calls to splash screen
    private fun getAllExhibits() {
        Timber.d("!! Get all exhibits")
        viewModelScope.launch {
            try {
                val response = userRepository.getExhibits()
                Timber.d("!! Response: $response")
                appDatabase.exhibitItemDao().upsertAll(response.map { item -> item.toExhibitEntity() })
                Timber.d("!! Database: ${appDatabase.exhibitItemDao().getAll()}")
            } catch (e: Exception) {
                Timber.d("!! Error: $e")
            }
        }
    }

    private fun getAllLocations() {
        viewModelScope.launch {
            try {
                val response = userRepository.getLocations()
                appDatabase.locationItemDao().upsertAll(response.map { item -> item.toLocationItemEntity() })
            } catch (e: Exception) {
                Timber.d("!! Error: $e")
            }
        }
    }
}
