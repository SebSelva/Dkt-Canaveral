package com.decathlon.canaveral.common.interactors

import com.decathlon.canaveral.common.interactors.player.AddPlayer
import com.decathlon.canaveral.common.interactors.player.DeletePlayer
import com.decathlon.canaveral.common.interactors.player.GetPlayers
import com.decathlon.canaveral.common.interactors.player.UpdatePlayer
import com.decathlon.canaveral.common.interactors.user.*

data class Interactors(
    val getPlayers: GetPlayers,
    val addPlayer: AddPlayer,
    val deletePlayer: DeletePlayer,
    val updatePlayer: UpdatePlayer,
    val initLogin: InitLogin,
    val userLogin: UserLogin,
    val userLogout: UserLogout,
    val userLoginState: UserLoginState,
    val getUserInfo: GetUserInfo,
    val completeUserInfo: CompleteUserInfo,
    val userActions: UserActions,
    val userConsent: UserConsent
)
