package com.example.kurslinemobileapp.view.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isRegistered = sharedPreferences.getBoolean("token", false)
        if (!isRegistered) {
// User is not registered, navigate to the registration fragment
            goToUploadActivity.visibility = View.GONE
// User is already registered, stay on the current fragment/activity
        } else {
            // Required data is present, display it
            goToUploadActivity.visibility = View.VISIBLE
        }
        goToUploadActivity.setOnClickListener {
            val intent = Intent(this@MainActivity, CourseUploadActivity::class.java)
            startActivity(intent)
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = binding.bottomNav
        setupWithNavController(bottomNavigationView, navController)
    }
}
