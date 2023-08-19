package com.example.kurslinemobileapp.view.courseFmAc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CommentAdapter
import com.example.kurslinemobileapp.adapter.MainListProductAdapter
import com.example.kurslinemobileapp.adapter.ProductDetailImageAdapter
import com.example.kurslinemobileapp.adapter.SimilarCoursesAdapter
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementDetailModel
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.AnnouncementSimilarCourse
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.Comment
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.Announcemenet
import com.example.kurslinemobileapp.api.announcement.getmainAnnouncement.GetAllAnnouncement
import com.example.kurslinemobileapp.api.announcement.updateanddelete.DeleteAnnouncementResponse
import com.example.kurslinemobileapp.api.announcement.updateanddelete.GetUserAnn
import com.example.kurslinemobileapp.api.comment.CommentAPI
import com.example.kurslinemobileapp.api.comment.CommentRequest
import com.example.kurslinemobileapp.api.comment.CommentResponse
import com.example.kurslinemobileapp.api.companyData.Category
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.companyData.CompanyRegisterData
import com.example.kurslinemobileapp.api.companyTeachers.companyProfile.Announcement
import com.example.kurslinemobileapp.api.favorite.FavoriteApi
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import com.example.kurslinemobileapp.view.fragments.HomeFragment
import com.example.kurslinemobileapp.view.payment.MoveForwardAnn
import com.example.kurslinemobileapp.view.payment.VipPaymentPage
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_user_register.*
import kotlinx.android.synthetic.main.fragment_business_account.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.product_item_row.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class ProductDetailActivity : AppCompatActivity(),SimilarCoursesAdapter.FavoriteItemClickListener {
    private lateinit var compositeDisposable: CompositeDisposable
    var favoriteDetailItem:Boolean = false
    //val isFavoriteAnnouncement:Announcemenet?=intent.getBooleanExtra("announcement",false)
    private var isFavorite:Boolean=false
    private var isFavoriteFromFavoriteFragment:Boolean=false
    var deleteStatus = MutableLiveData<Boolean>()
    private lateinit var similarProductAdapter: SimilarCoursesAdapter
    var checkLogin:Boolean=false
    private lateinit var similarcourseList : ArrayList<AnnouncementSimilarCourse>

    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        scrollViewforProductDescription.visibility = View.GONE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingDetail)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
         sharedPreferences = this.getSharedPreferences(sharedkeyname, Context.MODE_PRIVATE)
        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        favoriteDetailItem=sharedPreferences.getBoolean("isFavorite",false)
        val authHeader = "Bearer $token"
        println("gelenid" + annId)
        println("userid" + userId)
        println("token:"+authHeader)

        similarcourseList= ArrayList<AnnouncementSimilarCourse>()
        val userType = sharedPreferences.getString("userType",null)
        if (userType == "İstifadəçi" || userType == "Kurs" || userType == "Repititor") {
            linearlayoutforinputComment.visibility = View.VISIBLE
        }

        else{
            linearlayoutforinputComment.visibility = View.GONE
        }

        getUserAnnouncement(userId,annId,authHeader)

        relativeLayoutClickUpForward.setOnClickListener {
            val intent=Intent(this,MoveForwardAnn::class.java)
            startActivity(intent)
        }
        relativeLayoutClickVIP.setOnClickListener {
        val intent=Intent(this,VipPaymentPage::class.java)
        startActivity(intent)
        }

        deleteCourse.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(
                ContextThemeWrapper(this,R.style.CustomAlertDialogTheme))
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
            getProductWhichIncludeFavorite(annId,0)
        }else{
            getProductWhichIncludeFavorite(annId,userId)
        }

         checkLogin=sharedPreferences.getBoolean("checkIsRegistered",true)
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
                postOrdeletefav(authHeader,userId,annId)
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

    }
    private fun handleResponseForDeleteCourse(response: DeleteAnnouncementResponse){
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


        val receivedIntent = intent
        val valueFromIntent = receivedIntent.getStringExtra("subCategory")

        println("valueFromIntent"+valueFromIntent)


        val companyName = response.companyName
        val price = response.announcementPrice.toString()
        val courseName = response.announcementName
        val courseDesc = response.announcementDesc
        val categoryId = response.subCategory
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
        //catagoryTitle.setText(categoryId)
        regionTitle.setText(regionId)
        rejimTitle.setText(modeId)
        teacherTitle.setText(teacherName.toString())
        contactTitle.setText(phoneNumber)
        viewCount.setText(count.toString())
        catagoryTitle.setText(valueFromIntent)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewUserComment)
        val commentList: List<Comment> = response.comments
        val commentAdapter = CommentAdapter(commentList)
        recyclerView.adapter = commentAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val imageUrls = response.photos
        val viewPager: ViewPager2 = findViewById(R.id.viewPagerProductDetail)
        val photoAdapter = ProductDetailImageAdapter(imageUrls)
        viewPager.adapter = photoAdapter

        similarcourseList.addAll(response.announcements)
        println("size:"+similarcourseList)
        val recyclerViewSimilarCourse:RecyclerView=findViewById(R.id.recyclerviewforSameCourse)
        similarProductAdapter=SimilarCoursesAdapter(similarcourseList,this@ProductDetailActivity,this@ProductDetailActivity)
        recyclerViewSimilarCourse.adapter=similarProductAdapter
        recyclerViewSimilarCourse.layoutManager=GridLayoutManager(this,2)
        similarProductAdapter.notifyDataSetChanged()
        similarProductAdapter.setOnItemClickListener {
            isFavorite=it.isFavorite
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("isFavorite",isFavorite)
            startActivity(intent)
            sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname,Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            sharedPreferences.edit().putInt("announcementId", it.id).apply()
           // sharedPreferences.edit().putBoolean("checkIsRegistered",isRegistered).apply()
            //println("Item Clicked with UserID: "+isRegistered)
            println("gedenId-----"+it.id)
            editor.apply()
        }


    }
    override fun onFavoriteItemClick(id: Int, position: Int) {
        if (checkLogin==true){
            postOrdeletefavSimilarCourse(id,position)
            }
            else{
            Toast.makeText(this@ProductDetailActivity,"Please u have to be Log In or Register",Toast.LENGTH_SHORT).show()
        }
            }

    private fun postOrdeletefavSimilarCourse(id:Int,position: Int) {
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        favoriteDetailItem=sharedPreferences.getBoolean("isFavorite",false)
        val authHeader = "Bearer $token"
        val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(FavoriteApi::class.java)
        compositeDisposable.add(
            retrofit.postFavorite(authHeader!!,userId,id).
            subscribeOn(Schedulers.io()).
            observeOn(AndroidSchedulers.mainThread()).
            subscribe({
                similarProductAdapter.updateItemFavoriteStatus(position, it.isSuccess)
                similarProductAdapter.notifyDataSetChanged()
                println(it.isSuccess)
            },{throwable->
                println("My msg: ${throwable}")
            })
        )
    }


    private fun handleResponse(response: AnnouncementDetailModel) {
        scrollViewforProductDescription.visibility = View.VISIBLE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingDetail)
        lottie.pauseAnimation()
        lottie.visibility = View.GONE

       // Picasso.get().load(response.photos).into(productDetailImage)

        val receivedIntent = intent
        val valueFromIntent = receivedIntent.getStringExtra("SubCategory")

        val companyName = response.companyName
        val price = response.announcementPrice.toString()
        val courseName = response.announcementName
        val courseDesc = response.announcementDesc
        val categoryId = response.subCategory
        val regionId = response.announcementRegionId

        //println("Similar course: "+response.announcement.size)
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
        catagoryTitle.setText(valueFromIntent)
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
        println("Image: "+response.photos)
        sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)

        val annPhoto=response.photos
        val gson = Gson()
        val jsonPhotoList = gson.toJson(annPhoto)

        val announcmentname=response.announcementName
        val aboutannouncement=response.announcementDesc
        val announcementteacher=response.teacher
        val price=response.announcementPrice
        val address=response.announcementRegionId
        val adressid=response.announcementRegionId.toString()
        val annCategory=response.categoryId

        println("AnnCategory: "+annCategory)
        val annSubCategoryId=response.announcementSubCategoryId
        val category=response.categoryId.toString()
        val subcategory=response.announcementSubCategoryId.toString()
        sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("annName",announcmentname)
        editor.putString("annDesc",aboutannouncement)
        editor.putString("annTeacher",announcementteacher.toString())
        editor.putInt("annPrice",price)
        editor.putString("annId",adressid)
        editor.putString("annCategoryId",category)
        editor.putString("annSubCat",subcategory)
        editor.putString("annPhoto",jsonPhotoList)


        var categoryName = ""
        var subCategoryName=""

        getCategoryList()!!.subscribe({ categories ->
            println("333")
            categoryName = categories.categories.find { it.categoryId == annCategory }?.categoryName.toString()
            println(categoryName)
            editor.putString("companyCategory",categoryName)
            editor.apply()
        }, { throwable ->
            // Handle error during category retrieval
            println("Category retrieval error: $throwable")
        }).let { compositeDisposable.add(it) }

        editCourse.setOnClickListener {
            val intent=Intent(this@ProductDetailActivity,UpdateAnnouncement::class.java)

            startActivity(intent)
        }
    }
    private fun getCategoryList(): Observable<CompanyRegisterData>? {
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        return retrofit.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }



}