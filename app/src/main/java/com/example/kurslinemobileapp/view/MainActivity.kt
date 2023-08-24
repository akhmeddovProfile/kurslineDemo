package com.example.kurslinemobileapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CompanyNamesAdapter
import com.example.kurslinemobileapp.adapter.StatusAdapter
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.companyTeachers.CompanyTeacherAPI
import com.example.kurslinemobileapp.databinding.ActivityMainBinding
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.service.Room.category.CategoryEntity
import com.example.kurslinemobileapp.service.Room.category.MyRepositoryForCategory
import com.example.kurslinemobileapp.service.Room.category.SubCategoryEntity
import com.example.kurslinemobileapp.service.Room.courses.CourseEntity
import com.example.kurslinemobileapp.service.Room.mode.ModeEntity
import com.example.kurslinemobileapp.service.Room.region.RegionEntity
import com.example.kurslinemobileapp.service.Room.status.StatusEntity
import com.example.kurslinemobileapp.service.Room.tutors.TutorsEntity
import com.example.kurslinemobileapp.view.courseFmAc.CourseUploadActivity
import com.example.kurslinemobileapp.view.loginRegister.LoginActivity
import com.example.kurslinemobileapp.view.loginRegister.UserToCompanyActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.fragment_filter.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var repository: MyRepositoryForCategory
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        saveRegionsInRoom()
        saveModeInRoom()
        saveCategoryInRoom()
        saveStatusInRoom()
        saveCourseInRoom()
        repository = MyRepositoryForCategory(
            AppDatabase.getDatabase(applicationContext).categoryDao(),
            AppDatabase.getDatabase(applicationContext).subCategoryDao()
        )
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
            goToUploadActivity.visibility = View.VISIBLE
            goToUploadActivity.setOnClickListener {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            }

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


    private fun saveCategoryInRoom(){
        compositeDisposable= CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        compositeDisposable.add(
            retrofit.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {reg->
                        val categoryEntities = reg.categories.map { category ->
                            CategoryEntity(category.categoryId, category.categoryName)
                        }

                        GlobalScope.launch {
                                repository.saveCategories(categoryEntities)
                                println("Added Room: "+categoryEntities)

                        }
                        val subCategoryEntities = reg.categories.flatMap { category ->
                            category.subCategories.map { subCategory ->
                                SubCategoryEntity(subCategory.subCategoryId, subCategory.subCategoryName, category.categoryId)
                            }
                        }
                        GlobalScope.launch {
                            repository.saveSubCategories(subCategoryEntities)
                            println("Added SubCat Room: "+categoryEntities)

                        }
                    },
                    {thorawable->
                        println("Error for Region: "+thorawable.message)
                    }
                )
        )
    }

    private fun saveModeInRoom(){
        val appDatabase = AppDatabase.getDatabase(applicationContext)
        compositeDisposable= CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        compositeDisposable.add(
            retrofit.getModes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {reg->
                        val mode = reg.isOnlines.map { region ->
                            ModeEntity(region.isOnlineId, region.isOnlineName)
                        }
                        GlobalScope.launch {
                            appDatabase.modeDao().insertAll(mode)
                            println("Added Room: "+mode)
                        }
                    },
                    {thorawable->
                        println("Error for Region: "+thorawable.message)
                    }
                )
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveRegionsInRoom(){
        val appDatabase = AppDatabase.getDatabase(applicationContext)
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
    @SuppressLint("MissingInflatedId")
    private fun saveStatusInRoom() {
        val appDatabase = AppDatabase.getDatabase(applicationContext)
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        compositeDisposable.add(
            retrofit.getStatus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ status ->
                    val statuses=status.statuses.map { status->
                        StatusEntity(status.statusId,status.statusName)
                    }
                    GlobalScope.launch {
                        appDatabase.statusDao().insertAllStatus(statuses)
                    }
                    }, { throwable -> println("MyTestStatus: $throwable")
                    }))
    }

    private fun saveCourseInRoom(){
        val appDatabase = AppDatabase.getDatabase(applicationContext)
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(CompanyTeacherAPI::class.java)
        compositeDisposable.add(
            retrofit.getCompanies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ companyNames ->
                    println("222")
                    val filteredCompanyNames = companyNames.filter {it->
                        CourseEntity(it.companyId,it.companyName)
                        it.companyStatusId == 1
                    }
                    val courseEntities = filteredCompanyNames.map {
                        CourseEntity(it.companyId, it.companyName)
                    }

                    val filteredTutorsNames = companyNames.filter {it->
                        CourseEntity(it.companyId,it.companyName)
                        it.companyStatusId == 2
                    }
                    val tutorsEntities = filteredTutorsNames.map {
                        TutorsEntity(it.companyId, it.companyName)
                    }

                    GlobalScope.launch {
                        appDatabase.courseDao().insertAllcourse(courseEntities)
                        println("courseEntities"+courseEntities)
                    }

                    GlobalScope.launch {
                        appDatabase.tutorsDao().insertAlltutors(tutorsEntities)
                        println("courseEntities"+courseEntities)
                    }


                }, { throwable -> println("MyTests: $throwable") })
        )
    }


    }


