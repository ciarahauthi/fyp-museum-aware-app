package com.stitchumsdev.fyp.feature.home

import com.stitchumsdev.fyp.core.base.Action

sealed class HomeScreenAction : Action() {
    object GetAllExhibits: HomeScreenAction()
}