package com.stitchumsdev.fyp.feature.test

import com.stitchumsdev.fyp.core.base.Action

sealed class TestScreenAction : Action() {
    object FetchUsers: TestScreenAction()
}