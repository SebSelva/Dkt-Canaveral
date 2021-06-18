package com.decathlon.canaveral

import android.os.Bundle
import com.decathlon.canaveral.common.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO: Handle fragment ?
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }
}