package com.example.kurslinemobileapp.view.courseFmAc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap

import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.LinkAddress
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.*
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.CreataAnnouncementApi
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.CreateAnnouncementRequest
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.CreateAnnouncementResponse
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.Img
import com.example.kurslinemobileapp.api.comment.CommentResponse
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.companyData.SubCategory
import com.example.kurslinemobileapp.model.uploadPhoto.PhotoUpload
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_all_companies.*
import kotlinx.android.synthetic.main.activity_course_upload.*
import kotlinx.android.synthetic.main.activity_product_detail.*


class CourseUploadActivity : AppCompatActivity() {
    private val selectedPhotos = mutableListOf<PhotoUpload>()
    var compositeDisposable = CompositeDisposable()
    private lateinit var regionAdapter: RegionAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var modeAdapter: ModeAdapter

    var imageNames = mutableListOf<String>()
    var imageData = mutableListOf<String>()
    var images = mutableListOf<Img>()
    var teachersname= mutableListOf<String>()
     var categoryId: Int =0
     var modeId: Int = 0
     var regionId:Int = 0

    private val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                // Permission denied, handle accordingly
            }
        }
    private val galleryLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            uris.forEach { imageUri ->
                val imageName=getImageName(imageUri)
                convertImageToBase64(imageUri,imageName)
            }
        }

    private var block: Boolean = true
    companion object {
        private const val REQUEST_CODE_GALLERY = 1
    }

    @SuppressLint("WrongViewCast", "WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_upload)
        imageNames= mutableListOf()
        imageData= mutableListOf()
        images= mutableListOf()
        teachersname= mutableListOf()

        val sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        println("userid" + userId)
        println("token:"+authHeader)

            backtoMainFromCourseUpload.setOnClickListener {
                val intent = Intent(this@CourseUploadActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        val adapter =
            PhotoPagerAdapter(emptyList()) // Customize the adapter implementation as needed
        viewPagerCourseUpload.adapter = adapter


        uploadCourse.setOnClickListener {
        block=true
            val courseNameContainer = courseNameEditText.text.toString().trim()
            val courseAddressContainer = courseAddressEditText.text.toString().trim()
            val companyAboutContainer=courseAboutEditText.text.toString().trim()
            val companyTeacherContainer=courseTeacherEditText.text.toString().trim()
            val companyPriceContainer : Int =coursePriceEditText.text.toString().toInt()
            val companyModeContainer = modeId
            val companyCategoryContainer = categoryId
            val companyRegionContainer = regionId

            if (courseNameContainer.isEmpty()){
                courseNameEditText.error="Course Name required"
                courseNameEditText.requestFocus()
                block=false
            }
            if (courseAddressContainer.isEmpty()){
                courseAddressEditText.error="Course Address required"
                courseAddressEditText.requestFocus()
                block=false
            }

            if (companyAboutContainer.isEmpty()){
                courseAboutEditText.error="About the Course required"
                courseAboutEditText.requestFocus()
                block=false
            }
            if (companyTeacherContainer.isEmpty()){
                courseTeacherEditText.error="Teacher required"
                courseTeacherEditText.requestFocus()
                block=false
            }
            if (companyPriceContainer.equals("")){
                coursePriceEditText.error="Price required"
                coursePriceEditText.requestFocus()
                block=false
            }
            /*
            if (companyModeContainer.isEmpty()){
                courseModeEditText.error="Mode required"
                courseModeEditText.requestFocus()
                block=false
            }
            if (companyCategoryContainer.isEmpty()){
                courseCategoryEditText.error="Category required"
                courseCategoryEditText.requestFocus()
                block=false
            }
            if (companyRegionContainer.isEmpty()){
                courseRegionEditText.error="Region required"
                courseRegionEditText.requestFocus()
                block=false
            }

             */

            val name = courseTeacherEditText.text.toString().trim()
            if (name.isNotEmpty()){
                teachersname.add(name)
            }

   /*         val nameInputLayout = findViewById<TextInputLayout>(R.id.courseTeacherEditText)
            nameInputLayout.editText?.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    val name = nameInputLayout.editText?.text.toString().trim()

                    if (name.isNotEmpty()) {
                        teachersname.add(name)
                        println("Teachers Name: "+teachersname)
                        // Optional: Perform any additional actions or updates based on the entered name
                        nameInputLayout.editText?.text?.clear()
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            }
*/


            for(i in 0 until imageNames.size){
                val img = Img(imageNames[i], imageData[i].toString())
                images.add(img)
            }
            sendAnnouncementData(token!!,userId!!, CreateAnnouncementRequest(courseNameContainer,companyAboutContainer,companyPriceContainer,courseAddressContainer,companyModeContainer,companyCategoryContainer,companyRegionContainer,images,teachersname))
        }



        addCoursePhotos.setOnClickListener {
            requestGalleryPermission()
            openGallery()
        }

        val viewPager: ViewPager2 = findViewById(R.id.viewPagerCourseUpload)
        leftArrow.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1, true)
            }
        }

        rightarrow.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < selectedPhotos.size - 1) {
                viewPager.setCurrentItem(currentItem + 1, true)
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateNavigationButtons(position)
            }
        })
        courseRegionEditText.setOnClickListener {
            showBottomSheetDialogRegions()
        }

        courseCategoryEditText.setOnClickListener {
            showBottomSheetDialog()
        }

        courseModeEditText.setOnClickListener {
            showBottomSheetDialogMode()
        }


    }

    private fun sendAnnouncementData(
        token:String,
        userId:Int,
        createAnnouncementRequest: CreateAnnouncementRequest

    ) {
        compositeDisposable= CompositeDisposable()
        val retrofitService=RetrofitService(Constant.BASE_URL).retrofit.create(CreataAnnouncementApi::class.java)
        compositeDisposable.add(
            retrofitService.createAnnouncementByuserID(token,userId,createAnnouncementRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleResponse,
                    {throwable->
                    println("MyTest: "+ throwable)
                    }
                )
        )
    }
    private fun handleResponse(response: CreateAnnouncementResponse) {
        print("Response: "+ response.id)
    }
    private fun updateNavigationButtons(position: Int) {
        leftArrow.isEnabled = position > 0
        rightarrow.isEnabled = position < selectedPhotos.size - 1
    }

    private fun requestGalleryPermission() {
        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
    @SuppressLint("Range")
    private fun getImageName(imageUri: Uri): String? {
        val cursor = contentResolver.query(imageUri, null, null, null, null)
        val name: String? = cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                displayName
            } else {
                null
            }
        }
        cursor?.close()
        return name
    }
    private fun convertImageToBase64(imageUri: Uri,imageName:String?) {
        val inputStream = contentResolver.openInputStream(imageUri)
        val imageBytes = inputStream?.readBytes()

        val base64String = if (imageBytes != null) {
            Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } else {
            ""
        }

        inputStream?.close()

        // Use the base64String as needed
        setImageUrl.setText(imageName?.trim().toString())
        println("Image Name: "+setImageUrl.text.toString())
        imageNames.add(imageName!!)
imageData.add("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")    }



    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    private fun showBottomSheetDialog() {
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
            courseCategoryEditText.setText(subCategory.subCategoryName)
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
                        courseRegionEditText.setText(region.regionName)
                        regionId = region.regionId
                        dialog.dismiss()
                    }
                }, { throwable -> println("MyTestsRegions: $throwable") })
        )
        dialog.show()
    }
    @SuppressLint("MissingInflatedId")
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
                        courseModeEditText.setText(mode.isOnlineName)
                        modeId = mode.isOnlineId
                        dialog.dismiss()
                    }
                }, { throwable -> println("MyTestMode: $throwable") })
        )
        dialog.show()
    }
}