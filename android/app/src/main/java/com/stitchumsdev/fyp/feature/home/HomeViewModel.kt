package com.stitchumsdev.fyp.feature.home

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.repository.MuseumRepository
import com.stitchumsdev.fyp.core.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val userRepository: UserRepository,
    private val museumRepository: MuseumRepository
) : BaseViewModel<HomeScreenAction>() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    override fun onAction(action: HomeScreenAction) {}

    fun loadContent() {
        viewModelScope.launch {
            try {
                val homeData = userRepository.getHomeContent()
                val objects = museumRepository.load().objects

                val popularTop3 = objects
                    .sortedByDescending { it.likes + it.dislikes }
                    .take(3)
                val newestTop3 = objects
                    .sortedByDescending { it.createdAt }
                    .take(3)

                _uiState.value = HomeUiState.Success(
                    topSection = homeData.topSection,
                    midSection = homeData.midSection,
                    bottomSection = homeData.bottomSection,
                    popular = popularTop3,
                    new = newestTop3
                    )

            } catch (t: Throwable) {
                Timber.e("!! Error: $t")
                _uiState.value = HomeUiState.Error
            }
        }
    }
}
