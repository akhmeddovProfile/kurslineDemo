package com.example.kurslinemobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kurslinemobileapp.UI.FavoritePageFragment
import com.example.kurslinemobileapp.UI.MainPageFragment
import com.example.kurslinemobileapp.UI.RegisterFragment
import com.example.kurslinemobileapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /*
        val mainpageFragment=MainPageFragment()
        val registrationpageFragment=RegisterFragment()
        val favoritesFragment=FavoritePageFragment()

        makeCurrentFragment(mainpageFragment)

        binding.bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.mainPageFragment->makeCurrentFragment(mainpageFragment)
                R.id.accountFragment->makeCurrentFragment(registrationpageFragment)
                R.id.favorites->makeCurrentFragment(favoritesFragment)
            }
            true
        }

         */

     val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
         bottomNavigationView = binding.bottomNav
        setupWithNavController(bottomNavigationView,navController)
    }

/*
    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.fragmentContainerView,fragment)
        commit()
    }
*/
}
