package com.example.kurslinemobileapp.view.courseFmAc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
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
import com.example.kurslinemobileapp.model.uploadPhoto.SelectionPhotoShowOnViewPager
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.view.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_all_companies.*
import kotlinx.android.synthetic.main.activity_course_upload.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_user_to_company.*
import kotlinx.android.synthetic.main.fragment_filter.*
import kotlinx.android.synthetic.main.fragment_filter.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.ByteArrayOutputStream
import java.io.InputStream


class CourseUploadActivity : AppCompatActivity() {
    private val selectedPhotos = mutableListOf<SelectionPhotoShowOnViewPager>()
    var compositeDisposable = CompositeDisposable()
    private lateinit var regionAdapter: RegionAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var modeAdapter: ModeAdapter
    private var job: Job? = null

    var imageNames = mutableListOf<String>()
    var imageData = mutableListOf<String>()
    var images = mutableListOf<Img>()
    var teachersname= mutableListOf<String>()
    var allcategoriesId:Int=0
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

        setupViewPager()
        uploadCourse.setOnClickListener {
        block=true
            val courseNameContainer = courseNameEditText.text.toString().trim()
            val courseAddressContainer = courseAddressEditText.text.toString().trim()
            val companyAboutContainer=courseAboutEditText.text.toString().trim()
            val companyTeacherContainer=courseTeacherEditText.text.toString().trim()
            val companyPriceContainer : Int =coursePriceEditText.text.toString().toInt()
            val companyModeContainer = modeId
            val companySubCategoryContainer = categoryId
            val companyRegionContainer = regionId
            val companyAllCategoryContainer=allcategoriesId
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
            val name = courseTeacherEditText.text.toString().trim()
            if (name.isNotEmpty()){
                teachersname.add(name)
            }

            for(i in 0 until imageNames.size){
                val img = Img(imageNames[i], imageData[i].toString())
                images.add(img)
            }
            showProgressButton(true)
            sendAnnouncementData(token!!,userId!!, CreateAnnouncementRequest(courseNameContainer,companyAboutContainer,companyPriceContainer,courseAddressContainer,companyModeContainer,companyAllCategoryContainer,companySubCategoryContainer,companyRegionContainer,images,teachersname))
        }



        addCoursePhotos.setOnClickListener {
            requestGalleryPermission()
            openGallery()
        }

        courseAllCategoryEditText.setOnClickListener {
            showBottomSheetDialogAllCatogories()
        }
        courseRegionEditText.setOnClickListener {
            showBottomSheetDialogRegions()
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
                        val text = "Məlumatlar doğru deyil"
                        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                        showProgressButton(false)
                    }
                )
        )
    }
    private fun handleResponse(response: CreateAnnouncementResponse) {
        val intent = Intent(this@CourseUploadActivity,MainActivity::class.java)
        startActivity(intent)
        finish()
        println("Response: "+ response.id)
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

        val compressedBitmap = compressImage(inputStream)
        val targetSize = 2_500_000 // Target size in bytes (2.5 MB)
        var compressionQuality = 100 // Start with maximum quality (minimum compression)
        val byteArrayOutputStream = ByteArrayOutputStream()
        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream)
        while (byteArrayOutputStream.size() > targetSize && compressionQuality > 0) {
            byteArrayOutputStream.reset() // Reset the output stream

            // Reduce the compression quality by 10%
            compressionQuality -= 10

            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream)
        }

        val imageBytes = byteArrayOutputStream.toByteArray()
        val base64String = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        inputStream?.close()

        // Use the base64String as needed
        setImageUrl.setText(imageName?.trim().toString())
        setImageUrl.visibility=View.GONE
        println("Image Name: "+setImageUrl.text.toString()+".JPG")
        imageNames.add(imageName!!)
        selectedPhotos.add(SelectionPhotoShowOnViewPager(imageName,imageUri,base64String))
        imageData.add(base64String)
        setupViewPager()
        print("Base64: "+base64String)

    }
    private fun compressImage(inputStream: InputStream?): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = 2 // Adjust the sample size as needed for desired compression

        val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()

        return bitmap!!
    }

    private fun setupViewPager() {
        val viewPager = findViewById<ViewPager2>(R.id.viewPagerCourseUploadNew)

        // Create the adapter with the selected photos list
        val adapter = PhotoPagerAdapter( selectedPhotos)
        viewPager.adapter = adapter
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
                        courseAllCategoryEditText.setText(category.categoryName)
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

   @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    private fun showBottomSheetDialogRegions() {
       val appDatabase = AppDatabase.getDatabase(applicationContext)

       val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_region, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerviewRegions: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewRegions)
        recyclerviewRegions.setHasFixedSize(true)
        recyclerviewRegions.setLayoutManager(LinearLayoutManager(this))
        compositeDisposable = CompositeDisposable()
       job=appDatabase.regionDao().getAllRegions()
           .onEach { reg->
               regionAdapter = RegionAdapter(reg)
               recyclerviewRegions.adapter = regionAdapter
               regionAdapter.setChanged(reg)
               regionAdapter.setOnItemClickListener { region ->
                   courseRegionEditText.setText(region.regionName)
                   regionId = region.regionId
                   dialog.dismiss()
               }
           }.catch { thorawable->
               println("Error 2: "+ thorawable.message)
           }.launchIn(lifecycleScope)
               dialog.show()
    }


    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetDialogMode() {
        val appdatabase = AppDatabase.getDatabase(applicationContext)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_mode, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerViewMode: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewMode)
        recyclerViewMode.setHasFixedSize(true)
        recyclerViewMode.setLayoutManager(LinearLayoutManager(this))
        compositeDisposable = CompositeDisposable()

        job=appdatabase.modeDao().getAllMode()
            .onEach {mode->
                println("3")
                modeAdapter = ModeAdapter(mode)
                recyclerViewMode.adapter = modeAdapter
                modeAdapter.setChanged(mode)
                modeAdapter.setOnItemClickListener { mode ->
                    courseModeEditText.setText(mode.modeName)
                    modeId = mode.modeId
                    dialog.dismiss()
                }
            }.catch { thorawable->
                println("Error 3: "+ thorawable.message)
            }
            .launchIn(lifecycleScope)
        dialog.show()
    }

    private fun showProgressButton(show: Boolean) {
        if (show) {
            uploadCourse.apply {
                isEnabled = false
                text = "Kurs elanı yaradılır..."  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            uploadCourse.apply {
                isEnabled = true
                text = "Kursu əlavə et"
                // Restore original background, text color, etc., if modified
            }
        }
    }

    private fun cancelJob() {
        job?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelJob()
    }
}