package com.stitchumsdev.fyp.feature.home

import com.stitchumsdev.fyp.core.model.HomeItem
import com.stitchumsdev.fyp.core.model.ExhibitModel

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Error: HomeUiState
    data class Success(
        val topSection: List<HomeItem> = emptyList(),
        val midSection: List<HomeItem> = emptyList(),
        val bottomSection: List<HomeItem> = emptyList(),
        val selectedCard: HomeItem? = null,
        val popular: List<ExhibitModel> = emptyList(),
        val new: List<ExhibitModel> = emptyList(),
    ) : HomeUiState
}