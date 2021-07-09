package com.decathlon.canaveral.game

import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseActivity

class GameActivity : BaseActivity() {

    companion object {
        const val BUNDLE_KEY_GAME_SELECTED = "GAME_KEY_GAME_SELECTED"
        const val BUNDLE_KEY_GAME_VARIANT = "GAME_KEY_GAME_VARIANT"
        const val BUNDLE_KEY_GAME_DETAIL_IN = "GAME_KEY_GAME_DETAIL_IN"
        const val BUNDLE_KEY_GAME_DETAIL_OUT = "GAME_KEY_GAME_DETAIL_OUT"
        const val BUNDLE_KEY_GAME_DETAIL_IS_BULL_25 = "GAME_KEY_GAME_IS_BULL_25"

        const val BUNDLE_KEY_PLAYERS = "GAME_KEY_PLAYERS"
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_game
    }
}
