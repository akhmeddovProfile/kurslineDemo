package com.example.kurslinemobileapp.view.courseFmAc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CourseBusinessProfileAdapter
import com.example.kurslinemobileapp.adapter.MainListProductAdapter
import com.example.kurslinemobileapp.adapter.ResizeTransformation
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.companyTeachers.CompanyTeacherAPI
import com.example.kurslinemobileapp.api.companyTeachers.companyProfile.Announcement
import com.example.kurslinemobileapp.api.companyTeachers.companyProfile.CompanyDetail
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.tabsForCompanies.AllCompaniesActivity
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_course_business_profile.*
import kotlinx.android.synthetic.main.activity_product_detail.*

class CourseBusinessProfile : AppCompatActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var courseBusinessProfileAdapter: CourseBusinessProfileAdapter
    private lateinit var mainList : ArrayList<Announcement>
    private lateinit var mainList2 : ArrayList<Announcement>
    private lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_business_profile)
        sharedPreferences =this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        scrollBusinessProfile.visibility = View.GONE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingCourseBusinessProfile)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
        val companyId = intent.getIntExtra("companyId", -1)
        print("ID777:" + companyId)
        if (companyId != -1) {
            mainList = ArrayList<Announcement>()
            mainList2 = ArrayList<Announcement>()

            val coursesRV = findViewById<RecyclerView>(R.id.courseBusinessUploadsRV)
            coursesRV.layoutManager = GridLayoutManager(this,2)
        getDataFromServer(companyId)
        }

        backToCourseDesc.setOnClickListener {
            val intent = Intent(this@CourseBusinessProfile,AllCompaniesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getDataFromServer(id: Int) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(CompanyTeacherAPI::class.java)
        compositeDisposable.add(retrofit.getCompanyProfile(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse,
                { throwable -> println("MyTests: $throwable") }
            ))
    }

    private fun handleResponse(response: CompanyDetail) {
        scrollBusinessProfile.visibility = View.VISIBLE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingCourseBusinessProfile)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()
        val recycler = findViewById<RecyclerView>(R.id.courseBusinessUploadsRV)

        if (response.isNotEmpty()) {
            val companyDetailItem = response[0]
            mainList.addAll(companyDetailItem.announcements)
            mainList2.addAll(companyDetailItem.announcements)
            courseBusinessProfileAdapter = CourseBusinessProfileAdapter(mainList2)
            recycler.adapter = courseBusinessProfileAdapter
            courseBusinessProfileAdapter.notifyDataSetChanged()
            courseBusinessProfileAdapter.setOnItemClickListener {
                val intent = Intent(this@CourseBusinessProfile, ProductDetailActivity::class.java)
               startActivity(intent)
                sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                sharedPreferences.edit().putInt("announcementId", it.id).apply()
                println("gedenId-----"+it.id)
                editor.apply()
            }
            val companyName = companyDetailItem.companyName
            val companyPhone = companyDetailItem.companyPhone
            val companyAddress = companyDetailItem.companyAddress
            val companyImage = companyDetailItem.companyImage
            val category = companyDetailItem.companyCategory
            // Use the retrieved values as needed
            businessCompanyName.text = companyName
            courseBusinessNumber.text = companyPhone
            courseBusinessNumber.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$companyPhone")
                startActivity(intent)
            }
            courseBusinessLocation.text = companyAddress
            businessCategoryText.text = category

            if (companyImage == null) {
                courseBusinessPhoto.setImageResource(R.drawable.setpp)
            } else {
                Picasso.get().load(companyImage).transform(ResizeTransformation(300, 300)).into(courseBusinessPhoto)
            }
        } else {
            // Handle the case when the response is empty or null
        }
    }
}