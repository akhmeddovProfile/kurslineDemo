package com.example.kurslinemobileapp.view.courseFmAc

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CategoryAdapter
import com.example.kurslinemobileapp.adapter.ModeAdapter
import com.example.kurslinemobileapp.adapter.RegionAdapter
import com.example.kurslinemobileapp.adapter.SubCategoryAdapter
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.Img
import com.example.kurslinemobileapp.api.announcement.updateanddelete.GetUserAnn
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.companyData.SubCategory
import com.example.kurslinemobileapp.model.uploadPhoto.PhotoUpload
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_course_upload.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.activity_update_announcement.*
import kotlinx.android.synthetic.main.activity_update_announcement.lineForCourseUpload

class UpdateAnnouncement : AppCompatActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    private val selectedPhotos = mutableListOf<PhotoUpload>()
    private lateinit var regionAdapter: RegionAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var modeAdapter: ModeAdapter
    private var block: Boolean = true

    var imageNames = mutableListOf<String>()
    var imageData = mutableListOf<String>()
    var images = mutableListOf<Img>()
    var teachersname= mutableListOf<String>()
    var allcategoriesId:Int=0
    var categoryId: Int =0
    var modeId: Int = 0
    var regionId:Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_announcement)
        lineForCourseUpload.visibility = View.GONE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingDetailForUpAnn)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
        val sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        getUserAnnouncement(userId,annId,token!!)

        updateCourseBtn.setOnClickListener {
            val upcourseNameContainer1=upcourseNameEditText?.text?.trim().toString()
            val upcourseAboutContainer1=upcourseAboutEditText?.text?.trim().toString()
            val upcoursePriceContainer1=upcoursePriceEditText?.text?.trim().toString()
            val upcourseAddressContainer1=upcourseAddressEditText?.text?.trim().toString()
            val upcourseCategoryContainer1=courseSubCategoryEditText?.text?.trim().toString()
            val upcourseAllCategoryContainer1=upcourseAllCategoryEditText?.text?.trim().toString()
            val upcourseRegionContainer1=upcourseRegionEditText?.text?.trim().toString()

            if (upcourseNameContainer1.isNullOrEmpty()){
                upcourseNameEditText?.error="Name is not be null"
                upcourseNameEditText?.requestFocus()
                block=false
            }
            if(upcourseAboutContainer1.isNullOrEmpty()){
                upcourseAboutEditText.error="Course description is not be null"
                upcourseAboutEditText.requestFocus()
                block=false
            }
            if(upcoursePriceContainer1.isNullOrEmpty()){
                upcoursePriceEditText.error="Course Price is not be null"
                upcoursePriceEditText.requestFocus()
                block=false
            }
            if(upcourseAddressContainer1.isNullOrEmpty()){
                upcourseAddressEditText.error="Course Address is not be null"
                upcourseAddressEditText.requestFocus()
                block=false
            }
            if(upcourseAllCategoryContainer1.isNullOrEmpty()){
                upcourseAllCategoryEditText.error="Category is not be null"
                upcourseAllCategoryEditText.requestFocus()
                block=false
            }
            if(upcourseCategoryContainer1.isNullOrEmpty()){
                courseSubCategoryEditText.error="Sub-Category is not be null"
                courseSubCategoryEditText.requestFocus()
                block=false
            }
            if(upcourseRegionContainer1.isNullOrEmpty()){
                upcourseRegionEditText.error="Region is not be null"
                upcourseRegionEditText.requestFocus()
                block=false
            }

            if (block==false){
                println("False")
            }
            return@setOnClickListener
        }


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

    @SuppressLint("SuspiciousIndentation")
    private fun handleResponse(response:GetUserAnn) {
        lineForCourseUpload.visibility = View.VISIBLE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingDetailForUpAnn)
        lottie.pauseAnimation()
        lottie.visibility = View.GONE
        println("Response 1: "+ listOf(response))

     val nameofcourse=response.announcementName
     val coursedesc=response.announcementDesc
     val teachers=response.teacher
     val price=response.announcementPrice
     val address=response.announcementAddress
     val annmode=response.isOnline
     val category=response.categoryId
     val subcategory=response.announcementSubCategoryId
     val region=response.announcementRegionId

        upcourseNameEditText.setText(nameofcourse)
        upcourseAboutEditText.setText(coursedesc)
        val listofteachers=teachers.joinToString("[ , ]")
        val textView = findViewById<TextInputEditText>(R.id.upcourseTeacherEditText)
        textView.setText(listofteachers)
        upcoursePriceEditText.setText(price.toString())
        upcourseAddressEditText.setText(address)
       upcourseModeEditText.setText(annmode)
        upcourseModeEditText.setOnClickListener {
            showBottomSheetDialogMode()
        }
        //upcourseAllCategoryEditText.setText(category.toString())
        //courseSubCategoryEditText.setText(subcategory.toString())
        upcourseAllCategoryEditText.setOnClickListener {
            showBottomSheetDialogAllCatogories()
        }
        //upcourseRegionEditText.setText(region.toString())
        upcourseRegionEditText.setOnClickListener {
            showBottomSheetDialogRegions()
        }


    }

    private fun showBottomSheetDialogMode() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_mode, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerViewMode: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewMode)
        recyclerViewMode.setHasFixedSize(true)
        recyclerViewMode.setLayoutManager(LinearLayoutManager(this))
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        compositeDisposable.add(
            retrofit.getModes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ mode ->
                    println("3")
                    modeAdapter = ModeAdapter(mode.isOnlines)
                    recyclerViewMode.adapter = modeAdapter
                    modeAdapter.setChanged(mode.isOnlines)
                    modeAdapter.setOnItemClickListener { mode ->
                        upcourseModeEditText.setText(mode.isOnlineName)
                        modeId = mode.isOnlineId
                        dialog.dismiss()
                    }
                }, { throwable -> println("MyTestMode: $throwable") })
        )
        dialog.show()
    }

    private fun showBottomSheetDialogAllCatogories() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerViewCategories: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewCategories)
        recyclerViewCategories.setHasFixedSize(true)
        recyclerViewCategories.setLayoutManager(LinearLayoutManager(this))
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        compositeDisposable.add(
            retrofit.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ categories ->
                    println("1")
                    categoryAdapter = CategoryAdapter(categories.categories)
                    recyclerViewCategories.adapter = categoryAdapter
                    categoryAdapter.setChanged(categories.categories)
                    categoryAdapter.setOnItemClickListener { category ->
                        allcategoriesId = category.categoryId
                        upcourseAllCategoryEditText.setText(category.categoryName)
                        showSubCategories(category.subCategories)
                        dialog.dismiss()
                    }
                }, { throwable -> println("MyTests: $throwable") })
        )

        dialog.show()
    }


    private fun showSubCategories(subCategories: List<SubCategory>) {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerViewSubCategories: RecyclerView = bottomSheetView.findViewById(R.id.recyclerViewCategories)
        recyclerViewSubCategories.setHasFixedSize(true)
        recyclerViewSubCategories.layoutManager = LinearLayoutManager(this)

        val subCategoryAdapter = SubCategoryAdapter(subCategories)
        recyclerViewSubCategories.adapter = subCategoryAdapter
        subCategoryAdapter.setOnItemClickListener { subCategory ->
            // Handle the subcategory selection here
            categoryId = subCategory.subCategoryId
            courseSubCategoryEditText.setText(subCategory.subCategoryName)
            dialog.dismiss() // Dismiss the bottom sheet dialog when a subcategory is selected
        }
        dialog.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetDialogRegions() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_region, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerviewRegions: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewRegions)
        recyclerviewRegions.setHasFixedSize(true)
        recyclerviewRegions.setLayoutManager(LinearLayoutManager(this))
        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        compositeDisposable.add(
            retrofit.getRegions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ reg ->
                    println("2")
                    regionAdapter = RegionAdapter(reg.regions)
                    recyclerviewRegions.adapter = regionAdapter
                    regionAdapter.setChanged(reg.regions)
                    regionAdapter.setOnItemClickListener { region ->
                        upcourseRegionEditText.setText(region.regionName)
                        regionId = region.regionId
                        dialog.dismiss()
                    }
                }, { throwable -> println("MyTestsRegions: $throwable") })
        )
        dialog.show()
    }
}