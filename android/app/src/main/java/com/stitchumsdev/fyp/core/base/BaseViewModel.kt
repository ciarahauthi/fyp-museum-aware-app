package com.stitchumsdev.fyp.core.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T : Action> : ViewModel() {
    abstract fun onAction(action: T)
}