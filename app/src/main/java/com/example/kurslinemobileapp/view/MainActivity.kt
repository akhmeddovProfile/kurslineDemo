package com.example.kurslinemobileapp.view

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
import com.example.kurslinemobileapp.view.courseFmAc.CourseUploadActivity
import com.example.kurslinemobileapp.view.loginRegister.RegisterCompanyActivity
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
        val userType = sharedPreferences.getString("userType",null)
        if (userType == "İstifadəçi") {
// User is not registered, navigate to the registration fragment
            goToUploadActivity.visibility = View.VISIBLE
            goToUploadActivity.setOnClickListener{
                val intent = Intent(this@MainActivity,RegisterCompanyActivity::class.java)
                startActivity(intent)
            }
// User is already registered, stay on the current fragment/activity
        } else if(userType == "Kurs" || userType == "Repititor") {
            // Required data is present, display it
            goToUploadActivity.visibility = View.VISIBLE
            goToUploadActivity.setOnClickListener {
                val intent = Intent(this@MainActivity, CourseUploadActivity::class.java)
                startActivity(intent)
            }
        }
        else{
            goToUploadActivity.visibility = View.GONE
        }


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = binding.bottomNav
        setupWithNavController(bottomNavigationView, navController)
    }
}