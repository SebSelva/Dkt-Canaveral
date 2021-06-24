package com.decathlon.canaveral

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.decathlon.canaveral.common.BaseActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize Bottom Navigation View.
        val navView = findViewById<BottomNavigationView>(R.id.bottomNav_view)

        //Initialize NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        NavigationUI.setupWithNavController(navView, navHostFragment.navController)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }
}
