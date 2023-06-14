package com.example.kurslinemobileapp.view.courseFmAc

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementDetailModel
import com.example.kurslinemobileapp.api.comment.CommentAPI
import com.example.kurslinemobileapp.api.comment.CommentRequest
import com.example.kurslinemobileapp.api.comment.CommentResponse
import com.example.kurslinemobileapp.api.login.LogInAPi
import com.example.kurslinemobileapp.api.login.LoginRequest
import com.example.kurslinemobileapp.api.login.LoginResponseX
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import com.example.kurslinemobileapp.view.loginRegister.RegisterCompanyActivity
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_product_detail.*

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        productDetail_Rl.visibility = View.GONE

        val sharedPreferences = this.getSharedPreferences(sharedkeyname, Context.MODE_PRIVATE)
        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        println("gelenid" + annId)
        println("userid" + userId)
        println("token:"+authHeader)

        val userType = sharedPreferences.getString("userType",null)
        if (userType == "İstifadəçi" || userType == "Kurs" || userType == "Repititor") {
            linearlayoutforinputComment.visibility = View.VISIBLE
        }
        else{
            linearlayoutforinputComment.visibility = View.GONE
        }

        getDataFromServer(annId)
        sendCommentBtn.setOnClickListener {
            val comment = commentEditText.text.toString()
            // Validate user input
            if ( comment.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                sendComment(comment,authHeader,userId,annId)
            }
        }
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
        productDetail_Rl.visibility = View.VISIBLE
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
        if (response.isVIP == true){
            vip_product_for_detail.visibility = View.VISIBLE
        }else{
            vip_product_for_detail.visibility = View.GONE
        }
        courseownerName.setText(companyName)
        courseownerName.setOnClickListener {
            val intent = Intent(this@ProductDetailActivity,CourseBusinessProfile::class.java)
            startActivity(intent)
        }
        detailCoursePrice.setText(price + " AZN")
        coursecontentname.setText(courseName)
        aboutCourse.setText(courseDesc)
        catagoryTitle.setText(categoryId)
        regionTitle.setText(regionId)
        rejimTitle.setText(modeId)
        teacherTitle.setText(teacherName.toString())
        contactTitle.setText(phoneNumber)

    }

    private fun sendComment(comment:String,token:String,userId:Int,annId:Int) {
        compositeDisposable   = CompositeDisposable()
        val retrofitService =
            RetrofitService(Constant.BASE_URL).retrofit.create(CommentAPI::class.java)
        val request = CommentRequest(comment)
        compositeDisposable!!.add(
            retrofitService.postComment(token,userId,annId,request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,
                    { throwable ->println("MyTests: $throwable")
                    })
        )
    }

    private fun handleResponse(response: CommentResponse) {
        val intent = Intent(this@ProductDetailActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
    }
}