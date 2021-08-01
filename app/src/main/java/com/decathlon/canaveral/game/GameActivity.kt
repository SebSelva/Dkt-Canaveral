package com.decathlon.canaveral.game

import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseActivity

class GameActivity : BaseActivity() {

    override fun onBackPressed() {
        // Do not back while playing
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_game
    }
}
