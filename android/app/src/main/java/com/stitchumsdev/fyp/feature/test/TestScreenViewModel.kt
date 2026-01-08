package com.stitchumsdev.fyp.feature.test

import androidx.lifecycle.viewModelScope
import com.stitchumsdev.fyp.core.base.BaseViewModel
import com.stitchumsdev.fyp.core.data.repository.UserRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class TestScreenViewModel(
    private val userRepository: UserRepository
) : BaseViewModel<TestScreenAction>() {
    override fun onAction(action: TestScreenAction) {
        when (action) {
            TestScreenAction.FetchUsers -> fetchUsers()
        }
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            runCatching {
                userRepository.getUsers()
            }.onSuccess { users ->
                Timber.d("!! Users: $users ")

            }.onFailure { e ->
                Timber.e(e, "!! Failed to fetch users")
            }
        }
    }
}