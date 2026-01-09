package com.stitchumsdev.fyp.feature.scan

import com.stitchumsdev.fyp.core.base.Action

sealed class ScanScreenAction : Action() {
    object GetNearbyObjects: ScanScreenAction()
}