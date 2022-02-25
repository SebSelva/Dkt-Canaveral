package com.decathlon.canaveral.user

import androidx.lifecycle.viewModelScope
import com.decathlon.canaveral.common.BaseViewModel
import com.decathlon.canaveral.common.interactors.Interactors
import com.decathlon.core.user.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserEditionViewModel(private val interactors: Interactors) : BaseViewModel<UserEditionViewModel.UserEditionState>() {

    fun updateUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        interactors.userActions.update(user)
    }

    sealed class UserEditionState {
        object nicknameCompleted: UserEditionState()
        object pictureUpdated: UserEditionState()

    }
}