package com.example.kurslinemobileapp.view.courseFmAc

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementDetailModel
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_product_detail.*

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val sharedPreferences =
            this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("id",0)
        println("gelenid" + id)
        getDataFromServer(id)

    }


    private fun getDataFromServer(id: Int) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(retrofit.getDataById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse,
                { throwable -> println("MyTests: $throwable") }
            ))
    }

    private fun handleResponse(response: AnnouncementDetailModel) {
       // Picasso.get().load(response.photos).into(productDetailImage)
        val image = response.photos
        val companyName = response.companyName
        val price = response.announcementPrice.toString()
        val courseName = response.announcementName
        val courseDesc = response.announcementDesc
        val categoryId = response.announcementSubCategoryId
        val regionId = response.announcementRegionId
        val modeId = response.isOnline
        val teacherName = response.teacher
        val phoneNumber = response.phone
        courseownerName.setText(companyName)
        detailCoursePrice.setText(price)
        coursecontentname.setText(courseName)
        aboutCourse.setText(courseDesc)
        catagoryTitle.setText(categoryId)
        regionTitle.setText(regionId)
        rejimTitle.setText(modeId)
        teacherTitle.setText(teacherName.toString())
        contactTitle.setText(phoneNumber)

    }
}