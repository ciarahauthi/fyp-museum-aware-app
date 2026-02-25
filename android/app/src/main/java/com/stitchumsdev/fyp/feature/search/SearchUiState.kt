package com.stitchumsdev.fyp.feature.search

import com.stitchumsdev.fyp.core.model.ObjectModel

sealed interface SearchUiState {
    data object Loading : SearchUiState
    data object Error: SearchUiState

    data class Default(
        val searchText: String = "",
        val objects: List<ObjectModel>
    ) : SearchUiState

}