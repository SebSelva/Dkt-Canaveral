package com.decathlon.canaveral.common.interactors

import com.decathlon.canaveral.common.interactors.player.PlayerActions
import com.decathlon.canaveral.common.interactors.stats.StdActions
import com.decathlon.canaveral.common.interactors.user.*

data class Interactors(
    val playerActions: PlayerActions,
    val initLogin: InitLogin,
    val userLogin: UserLogin,
    val userLogout: UserLogout,
    val userLoginState: UserLoginState,
    val getUserInfo: GetUserInfo,
    val completeUserInfo: CompleteUserInfo,
    val userActions: UserActions,
    val userConsent: UserConsent,
    val stdActions: StdActions
)
