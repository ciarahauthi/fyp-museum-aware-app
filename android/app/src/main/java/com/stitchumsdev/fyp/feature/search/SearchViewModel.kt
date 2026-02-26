package com.stitchumsdev.fyp.feature.search

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.database.AppDatabase
import com.stitchumsdev.fyp.core.data.database.entities.ExhibitRatingEntity
import com.stitchumsdev.fyp.core.data.repository.MuseumRepository
import com.stitchumsdev.fyp.core.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel(
    private val museumRepository: MuseumRepository,
    private val userRepository: UserRepository,
    private val appDatabase: AppDatabase
) : BaseViewModel<SearchAction>() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadObjects() {
        viewModelScope.launch {
            try {
                museumRepository.clearCache()
                val objects = museumRepository.load().objects
                _uiState.value = SearchUiState.Default(objects = objects)
            } catch (t: Throwable) {
                Timber.e(t, "!! Failed to load objects")
                _uiState.value = SearchUiState.Error
            }
        }
    }

    override fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnTextChanged -> {
                _uiState.update { state ->
                    if (state is SearchUiState.Default) state.copy(searchText = action.text) else state
                }
            }
            SearchAction.ClearSearch -> {
                _uiState.update { state ->
                    if (state is SearchUiState.Default) state.copy(searchText = "") else state
                }
            }

            is SearchAction.OnRate -> onRate(
                id= action.id,
                rating = action.rating
            )
        }
    }

    private fun onRate(id: Int, rating: Boolean) {
        viewModelScope.launch {
            try {
                userRepository.sendExhibitRating(id, rating)
                appDatabase.exhibitRatingDao().markRated(
                    ExhibitRatingEntity(
                        exhibitId = id,
                        ratedDate = System.currentTimeMillis().toString()
                    )
                )
                _uiState.update { state ->
                    if (state is SearchUiState.Default) {
                        state.copy(
                            objects = state.objects.map { obj ->
                                if (obj.id == id) obj.copy(canRate = false) else obj
                            }
                        )
                    } else state
                }

                // Refresh cache for next load
                museumRepository.clearCache()
            } catch (e: Exception) {
                Timber.e("!! Error: $e")
            }
        }
    }
}