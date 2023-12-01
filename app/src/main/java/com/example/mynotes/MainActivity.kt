package com.example.mynotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var drawer: DrawerLayout
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navhost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navhost.navController
//        setupActionBarWithNavController(navController)
        drawer = findViewById(R.id.drawer)
        actionBarDrawerToggle = ActionBarDrawerToggle(this,drawer,R.string.nav_open,R.string.nav_close)
        actionBarDrawerToggle.syncState()
    }



}