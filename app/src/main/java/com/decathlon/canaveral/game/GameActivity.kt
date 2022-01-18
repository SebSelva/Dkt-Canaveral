package com.decathlon.canaveral.game

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseActivity

class GameActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_game

    override fun onCreate(savedInstanceState: Bundle?) {
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        // Do not back while playing
    }

}
