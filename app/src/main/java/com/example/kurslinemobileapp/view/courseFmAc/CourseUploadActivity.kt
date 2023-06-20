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
    var imageData = mutableListOf<Uri>()
    var images = mutableListOf<Img>()
    var teachersname= mutableListOf<String>()
    lateinit var categoryId: String
    lateinit var modeId: String
    lateinit var regionId:String

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

        categoryId = ""
        modeId = ""
        regionId = ""

        uploadCourse.setOnClickListener {
        block=true
            val courseNameContainer = courseNameEditText.text.toString().trim()
            val courseAddressContainer = courseAddressEditText.text.toString().trim()
            val companyAboutContainer=courseAboutEditText.text.toString().trim()
            val companyTeacherContainer=courseTeacherEditText.text.toString().trim()
            val companyPriceContainer=coursePriceEditText.text.toString().trim()
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
            if (companyPriceContainer.isEmpty()){
                coursePriceEditText.error="Price required"
                coursePriceEditText.requestFocus()
                block=false
            }
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
            sendAnnouncementData(token!!,userId!!, CreateAnnouncementRequest(courseNameContainer,companyAboutContainer,companyPriceContainer.toInt(),courseAddressContainer,categoryId,regionId,teachersname,modeId,images))

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
        imageData.add(imageUri)
    }



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
            categoryId = subCategory.subCategoryId.toString()
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
                        regionId = region.regionId.toString()
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
                        modeId = mode.isOnlineId.toString()
                        dialog.dismiss()
                    }
                }, { throwable -> println("MyTestMode: $throwable") })
        )
        dialog.show()
    }
}