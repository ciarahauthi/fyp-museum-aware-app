package com.stitchumsdev.fyp.feature.search

import com.stitchumsdev.fyp.core.base.Action

sealed class SearchAction: Action() {
    data class OnTextChanged(val text: String) : SearchAction()
    data object ClearSearch : SearchAction()
    data class OnRate(val id: Int, val rating: Boolean) : SearchAction()
}