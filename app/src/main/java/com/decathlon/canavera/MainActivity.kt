package com.decathlon.canavera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//        //Initialize Bottom Navigation View.
//        val navView = findViewById<BottomNavigationView>(R.id.bottomNav_view)
//
//        //Pass the ID's of Different destinations
//
//        //Pass the ID's of Different destinations
//        val appBarConfiguration = AppBarConfiguration.Builder(
//            R.id.navigation_dashboard,
//            R.id.navigation_stats,
//            R.id.navigation_settings
//        ).build()
//
        //Initialize NavController.
        val navController = Navigation.findNavController(this, R.id.navHostFragment)
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
       // NavigationUI.setupWithNavController(navView, navController)

    }
}