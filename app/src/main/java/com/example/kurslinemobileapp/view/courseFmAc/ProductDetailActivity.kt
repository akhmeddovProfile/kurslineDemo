package com.example.kurslinemobileapp.view.courseFmAc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CommentAdapter
import com.example.kurslinemobileapp.adapter.ProductDetailImageAdapter
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementDetailModel
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.Comment
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.updateanddelete.DeleteAnnouncementResponse
import com.example.kurslinemobileapp.api.announcement.updateanddelete.GetUserAnn
import com.example.kurslinemobileapp.api.comment.CommentAPI
import com.example.kurslinemobileapp.api.comment.CommentRequest
import com.example.kurslinemobileapp.api.comment.CommentResponse
import com.example.kurslinemobileapp.api.companyTeachers.companyProfile.Announcement
import com.example.kurslinemobileapp.api.favorite.FavoriteApi
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import com.example.kurslinemobileapp.view.fragments.HomeFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_user_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    var favoriteDetailItem:Boolean = false
    //val isFavoriteAnnouncement:Announcemenet?=intent.getBooleanExtra("announcement",false)
    private var isFavorite:Boolean=false
    private var isFavoriteFromFavoriteFragment:Boolean=false
    var deleteStatus = MutableLiveData<Boolean>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        scrollViewforProductDescription.visibility = View.GONE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingDetail)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
        val sharedPreferences = this.getSharedPreferences(sharedkeyname, Context.MODE_PRIVATE)
        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        favoriteDetailItem=sharedPreferences.getBoolean("isFavorite",false)
        val authHeader = "Bearer $token"
        println("gelenid" + annId)
        println("userid" + userId)
        println("token:"+authHeader)

        val userType = sharedPreferences.getString("userType",null)
        if (userType == "İstifadəçi" || userType == "Kurs" || userType == "Repititor") {
            linearlayoutforinputComment.visibility = View.VISIBLE
/*          if (userType == "Kurs" || userType == "Repititor"){
              deleteCourse.visibility=View.VISIBLE
              editCourse.visibility=View.VISIBLE
          }*/
        }

        else{
            linearlayoutforinputComment.visibility = View.GONE
        }

getUserAnnouncement(userId,annId,authHeader)
        deleteCourse.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setMessage("Are you sure you want to delete this item?")
            alertDialogBuilder.setPositiveButton("Yes") { dialog, which->
                // Delete the item
                deleteItem(authHeader,userId,annId)


            }
            alertDialogBuilder.setNegativeButton("No") { dialog, which ->
                // Do nothing, the delete process is not started
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        editCourse.setOnClickListener {
            val intent=Intent(this@ProductDetailActivity,CourseUploadActivity::class.java)
            startActivity(intent)
        }

        commentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val name = s.toString().trim()
                val characterCount = name.length

                if (characterCount < 3 || characterCount > 100) {
                    commentforUser.error = "Comment must be between 3 and 100 characters."
                } else {
                    commentforUser.error = null
                }

                characterCountTextViewComment.text = "$characterCount / 100"
            }
        })
        if(userId==0){
            getDataFromServer(annId,0)
        }else{
            getProductWhichIncludeFavorite(annId,userId)
        }

        val checkLogin=sharedPreferences.getBoolean("checkIsRegistered",true)
       // println("Check: "+Constant.isFavorite)
         isFavorite=intent.getBooleanExtra("isFavorite",false)
        isFavoriteFromFavoriteFragment=intent.getBooleanExtra("isFavoriteFromFavoriteFragment",false)
        //val position = intent.getBooleanExtra("position", false)
       // var itemFavorite=sharedPreferences.getBoolean("isFavoriteItemForDetail_${position}",false)
        if (checkLogin==true){
            if (isFavorite==true||isFavoriteFromFavoriteFragment==true){
                favorite_button_for_detail.setImageResource(R.drawable.favorite_for_product)
                println("Check2: "+isFavorite)
            }
            else{
                favorite_button_for_detail.setImageResource(R.drawable.favorite_border_for_product)
                println("Check3: "+isFavorite)
            }
            }

        favorite_button_for_detail.setOnClickListener {
            if(checkLogin==true){
                isFavorite=!isFavorite
                isFavoriteFromFavoriteFragment=!isFavoriteFromFavoriteFragment
                favorite_button_for_detail.setImageResource(if (isFavorite&&isFavoriteFromFavoriteFragment) R.drawable.favorite_for_product else R.drawable.favorite_border_for_product)
                postOrdeletefav(token!!,userId,annId)
            }
            else{
                Toast.makeText(this@ProductDetailActivity,"Please u have to be Log In or Register",Toast.LENGTH_SHORT).show()
            }
        }

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

    private fun deleteItem(token: String,userId: Int,annId: Int) {
        compositeDisposable= CompositeDisposable()
        val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(
            retrofit.deleteAnnouncementForOwner(token,userId,annId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                   this::handleResponseForDeleteCourse
                ,{error->
                    print("Error: "+error)
                })
        )

/*        val coroutineScope= CoroutineScope(Dispatchers.Main)
        val job=coroutineScope.launch {
            try {
                 withContext(Dispatchers.IO){
                    retrofit.deleteAnnouncementForOwner(token,userId,annId)
                }
                deleteStatus.postValue(true)
                println("Deleted")
            }catch (error:Throwable){
                println("Error: "+error)
            }
        }
        job.cancel()*/
    }
    private fun handleResponseForDeleteCourse(response: DeleteAnnouncementResponse){
        //scrollViewforProductDescription.visibility = View.VISIBLE
        deleteStatus.postValue(true)
        val lottie = findViewById<LottieAnimationView>(R.id.loadingDetail)
        Toast.makeText(this@ProductDetailActivity,"Item Deleted",Toast.LENGTH_SHORT).show()
        println("Deleted: "+response.isSuccess)
        lottie.visibility = View.GONE
        lottie.pauseAnimation()
        val intent = Intent(this@ProductDetailActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun postOrdeletefav(token: String,userId:Int,annId:Int) {
        val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)
        compositeDisposable.add(
            retrofit.postFavorite(token!!,userId,annId).
            subscribeOn(Schedulers.io()).
            observeOn(AndroidSchedulers.mainThread()).
            subscribe({
                println(it.isSuccess)
            },{throwable->
                println("My msg: ${throwable}")
            })
        )
    }

    private fun getDataFromServer(annId: Int,userId: Int) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(retrofit.getDataById(annId,userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse,
                { throwable -> println("MyDescTest: $throwable") }
            ))
    }
    private fun getProductWhichIncludeFavorite(announId:Int,userId: Int) {
        compositeDisposable=CompositeDisposable()
        val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(
            retrofit.getDataById(announId,userId).
            subscribeOn(Schedulers.io()).
            observeOn(AndroidSchedulers.mainThread()).
            subscribe(this::handleResponseforItemsWhichFav,{
            })
        )
    }
    private fun handleResponseforItemsWhichFav(response: AnnouncementDetailModel) {
        scrollViewforProductDescription.visibility = View.VISIBLE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingDetail)
        lottie.pauseAnimation()
        lottie.visibility = View.GONE

        // Picasso.get().load(response.photos).into(productDetailImage)


        val companyName = response.companyName
        val price = response.announcementPrice.toString()
        val courseName = response.announcementName
        val courseDesc = response.announcementDesc
        val categoryId = response.announcementSubCategoryId
        val regionId = response.announcementRegionId
        val modeId = response.isOnline
        val teacherName = response.teacher
        val phoneNumber = response.phone
        val count = response.countView
        if (response.isVIP == true){
            vip_product_for_detail.visibility = View.VISIBLE
        }else{
            vip_product_for_detail.visibility = View.GONE
        }

        courseownerName.setText(companyName)
        detailCoursePrice.setText(price + " AZN")
        coursecontentname.setText(courseName)
        aboutCourse.setText(courseDesc)
        catagoryTitle.setText(categoryId)
        regionTitle.setText(regionId)
        rejimTitle.setText(modeId)
        teacherTitle.setText(teacherName.toString())
        contactTitle.setText(phoneNumber)
        viewCount.setText(count.toString())
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewUserComment)
        val commentList: List<Comment> = response.comments

        val commentAdapter = CommentAdapter(commentList)
        recyclerView.adapter = commentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val imageUrls = response.photos
        val viewPager: ViewPager2 = findViewById(R.id.viewPagerProductDetail)
        val photoAdapter = ProductDetailImageAdapter(imageUrls)
        viewPager.adapter = photoAdapter
    }

    private fun handleResponse(response: AnnouncementDetailModel) {
        scrollViewforProductDescription.visibility = View.VISIBLE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingDetail)
        lottie.pauseAnimation()
        lottie.visibility = View.GONE

       // Picasso.get().load(response.photos).into(productDetailImage)

        val companyName = response.companyName
        val price = response.announcementPrice.toString()
        val courseName = response.announcementName
        val courseDesc = response.announcementDesc
        val categoryId = response.announcementSubCategoryId
        val regionId = response.announcementRegionId
        val modeId = response.isOnline
        val teacherName = response.teacher
        val phoneNumber = response.phone
        val count = response.countView
        if (response.isVIP == true){
            vip_product_for_detail.visibility = View.VISIBLE
        }else{
            vip_product_for_detail.visibility = View.GONE
        }

        courseownerName.setText(companyName)
        detailCoursePrice.setText(price + " AZN")
        coursecontentname.setText(courseName)
        aboutCourse.setText(courseDesc)
        catagoryTitle.setText(categoryId)
        regionTitle.setText(regionId)
        rejimTitle.setText(modeId)
        teacherTitle.setText(teacherName.toString())
        contactTitle.setText(phoneNumber)
        viewCount.setText(count.toString())
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewUserComment)
        val commentList: List<Comment> = response.comments

        val commentAdapter = CommentAdapter(commentList)
        recyclerView.adapter = commentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val imageUrls = response.photos
        val viewPager: ViewPager2 = findViewById(R.id.viewPagerProductDetail)
        val photoAdapter = ProductDetailImageAdapter(imageUrls)
        viewPager.adapter = photoAdapter
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
        recreate()
        commentEditText.text!!.clear()
        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
    }
    private fun getUserAnnouncement(id: Int,annId: Int,token: String) {
        compositeDisposable = CompositeDisposable()
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(retrofit.getAnnouncementForUser(id,annId,token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse,
                { throwable -> println("MyTests: $throwable") }
            ))
    }

    private fun handleResponse(response: GetUserAnn) {
        deleteCourse.visibility = View.VISIBLE
        editCourse.visibility = View.VISIBLE
    }
}