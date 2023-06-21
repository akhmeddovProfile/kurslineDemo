package com.example.kurslinemobileapp.view.courseFmAc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.MainListProductAdapter
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

class CourseBusinessProfile : AppCompatActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_business_profile)

        val companyId = intent.getIntExtra("companyId", -1)
        print("ID777:" + companyId)
        if (companyId != -1) {
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

        if (response.isNotEmpty()) {
            val companyDetailItem = response[0]

            val companyName = companyDetailItem.companyName
            val companyPhone = companyDetailItem.companyPhone
            val companyAddress = companyDetailItem.companyAddress
            val companyImage = companyDetailItem.companyImage
            val category = companyDetailItem.companyCategory
            // Use the retrieved values as needed
            businessCompanyName.text = companyName
            courseBusinessNumber.text = companyPhone
            courseBusinessLocation.text = companyAddress
            businessCategoryText.text = category

            if (companyImage.equals("")) {
                courseBusinessPhoto.setImageResource(R.drawable.setpp)
            } else {
                Picasso.get().load(companyImage).into(courseBusinessPhoto)
            }
        } else {
            // Handle the case when the response is empty or null
        }
    }
}