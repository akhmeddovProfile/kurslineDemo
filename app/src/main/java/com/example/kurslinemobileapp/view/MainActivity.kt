package com.example.kurslinemobileapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.room.Room
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.databinding.ActivityMainBinding
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.service.Room.RegionEntity
import com.example.kurslinemobileapp.view.courseFmAc.CourseUploadActivity
import com.example.kurslinemobileapp.view.loginRegister.RegisterCompanyActivity
import com.example.kurslinemobileapp.view.loginRegister.UserToCompanyActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var compositeDisposable: CompositeDisposable

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        saveRegionsInRoom()
        val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userType = sharedPreferences.getString("userType",null)
        if (userType == "İstifadəçi") {
// User is not registered, navigate to the registration fragment
            goToUploadActivity.visibility = View.VISIBLE
            goToUploadActivity.setOnClickListener{
                val intent = Intent(this@MainActivity, UserToCompanyActivity::class.java)
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


        val inflateLayout=findViewById<View>(R.id.networkError)
        val networkConnection=NetworkConnection(applicationContext)
        networkConnection.observe(this){
            if (it){
                Toast.makeText(this,"Connected", Toast.LENGTH_SHORT).show()
                inflateLayout.visibility= View.GONE
                fragmentContainerView.visibility=View.VISIBLE
                goToUploadActivity.visibility = View.VISIBLE
                bottom_nav.visibility=View.VISIBLE
            }
            else{
                Toast.makeText(this,"Not Connected", Toast.LENGTH_SHORT).show()
                fragmentContainerView.visibility=View.GONE
                bottom_nav.visibility=View.GONE
                goToUploadActivity.visibility = View.GONE
                inflateLayout.visibility= View.VISIBLE
            }
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView = binding.bottomNav
        setupWithNavController(bottomNavigationView, navController)
    }

    private fun saveRegionsInRoom(){
        val appDatabase = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app-database").build()
        compositeDisposable= CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        compositeDisposable.add(
            retrofit.getRegions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {reg->
                        val regions = reg.regions.map { region ->
                            RegionEntity(region.regionId, region.regionName)
                        }
                        GlobalScope.launch {
                            appDatabase.regionDao().insertAll(regions)
                            println("Added Room: "+regions)
                        }
                    },
                    {thorawable->
                    println("Error for Region: "+thorawable.message)
                    }
                )
        )
    }
}
