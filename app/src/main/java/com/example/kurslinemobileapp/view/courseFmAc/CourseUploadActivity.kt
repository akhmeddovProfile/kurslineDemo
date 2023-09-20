package com.example.kurslinemobileapp.view.courseFmAc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.app.kurslinemobileapp.R
import com.app.kurslinemobileapp.databinding.ActivityCourseUploadBinding
import com.example.kurslinemobileapp.adapter.*
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.CreataAnnouncementApi
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.CreateAnnouncementRequest
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.CreateAnnouncementResponse
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.Img
import com.example.kurslinemobileapp.model.uploadPhoto.SelectionPhotoShowOnViewPager
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.service.Room.category.MyRepositoryForCategory
import com.example.kurslinemobileapp.service.Room.category.SubCategoryEntity
import com.example.kurslinemobileapp.view.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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
    private lateinit var repository: MyRepositoryForCategory
    private lateinit var bindingCourseUploadActivity: ActivityCourseUploadBinding
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
    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }
    private var block: Boolean = true


    @SuppressLint("WrongViewCast", "WrongThread", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingCourseUploadActivity= ActivityCourseUploadBinding.inflate(layoutInflater)
        val view=bindingCourseUploadActivity.root
        setContentView(view)
        //setContentView(R.layout.activity_course_upload)
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
        repository = MyRepositoryForCategory(
            AppDatabase.getDatabase(this).categoryDao(),
            AppDatabase.getDatabase(this).subCategoryDao()
        )
            bindingCourseUploadActivity.backtoMainFromCourseUpload.setOnClickListener {
                val intent = Intent(this@CourseUploadActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        setupViewPager()
        bindingCourseUploadActivity.uploadCourse.setOnClickListener {
        block=true
            val courseNameContainer = bindingCourseUploadActivity.courseNameEditText.text.toString().trim()
            val courseAddressContainer = bindingCourseUploadActivity.courseAddressEditText.text.toString().trim()
            val companyAboutContainer=bindingCourseUploadActivity.courseAboutEditText.text.toString().trim()
            val companyTeacherContainer=bindingCourseUploadActivity.courseTeacherEditText.text.toString().trim()
            val companyPriceContainer  =bindingCourseUploadActivity.coursePriceEditText.text.toString().trim()
            val companyModeContainer = modeId
            val companySubCategoryContainer = categoryId
            val companyRegionContainer = regionId
            val companyAllCategoryContainer=allcategoriesId
            val companyModeCon = bindingCourseUploadActivity.courseModeEditText.text.toString().trim()
            val courseCat = bindingCourseUploadActivity.courseAllCategoryEditText.text.toString().trim()
            val companySubCat = bindingCourseUploadActivity.courseCategoryEditText.text.toString().trim()
            val companyReg = bindingCourseUploadActivity.courseRegionEditText.text.toString().trim()
            if (courseNameContainer.isEmpty()){
                bindingCourseUploadActivity.courseNameEditText.error="Course Name required"
                bindingCourseUploadActivity.courseNameEditText.requestFocus()
                block=false
            }
            if (courseAddressContainer.isEmpty()){
                bindingCourseUploadActivity.courseAddressEditText.error="Course Address required"
                bindingCourseUploadActivity.courseAddressEditText.requestFocus()
                block=false
            }

            if (companyAboutContainer.isEmpty()){
                bindingCourseUploadActivity.courseAboutEditText.error="About the Course required"
                bindingCourseUploadActivity.courseAboutEditText.requestFocus()
                block=false
            }
            if (companyTeacherContainer.isEmpty()){
                bindingCourseUploadActivity.courseTeacherEditText.error="Teacher required"
                bindingCourseUploadActivity.courseTeacherEditText.requestFocus()
                block=false
            }
            if (companyPriceContainer.isEmpty()){
                bindingCourseUploadActivity.coursePriceEditText.error="Price required"
                bindingCourseUploadActivity.coursePriceEditText.requestFocus()
                block=false
            }
            if (companyModeCon.isEmpty()){
                bindingCourseUploadActivity.courseModeEditText.error="Mode required"
                bindingCourseUploadActivity.courseModeEditText.requestFocus()
                block=false
            }
            if (courseCat.isEmpty()){
                bindingCourseUploadActivity.courseAllCategoryEditText.error="Category required"
                bindingCourseUploadActivity.courseAllCategoryEditText.requestFocus()
                block=false
            }
            if (companySubCat.isEmpty()){
                bindingCourseUploadActivity.courseCategoryEditText.error="Sub Category required"
                bindingCourseUploadActivity.courseCategoryEditText.requestFocus()
                block=false
            }
            if (companyReg.isEmpty()){
                bindingCourseUploadActivity.courseRegionEditText.error="Region required"
                bindingCourseUploadActivity.courseRegionEditText.requestFocus()
                block=false
            }
            val name = bindingCourseUploadActivity.courseTeacherEditText.text.toString().trim()
            if (name.isNotEmpty()){
                teachersname.add(name)
            }

            for(i in 0 until imageNames.size){
                val img = Img(imageNames[i], imageData[i].toString())
                images.add(img)
            }
            showProgressButton(true)
            sendAnnouncementData(authHeader!!,
                userId,
                 CreateAnnouncementRequest(courseNameContainer,companyAboutContainer,companyPriceContainer,courseAddressContainer,companyModeContainer,companyAllCategoryContainer,companySubCategoryContainer,companyRegionContainer,images,teachersname))
        }

        bindingCourseUploadActivity.addCoursePhotos.setOnClickListener {
            println("1111111")
            if (!checkPermission()){
                requestGalleryPermission()
            }else{
                openGallery()
            }


        }

        bindingCourseUploadActivity.courseAllCategoryEditText.setOnClickListener {
            showBottomSheetDialogAllCatogories()
        }
        bindingCourseUploadActivity.courseRegionEditText.setOnClickListener {
            showBottomSheetDialogRegions()
        }
        bindingCourseUploadActivity.courseModeEditText.setOnClickListener {
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
                        println("Error: "+ throwable.message)
                        val text=getString(R.string.theinformationdonotmatch)
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

    private fun getFileSize(uri: Uri): Long {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    return it.getLong(sizeIndex)
                }
            }
        }
        return 0
    }

    private fun convertImageToBase64(imageUri: Uri,imageName:String?) {
        val imageSize = getFileSize(imageUri) // Get the image size in bytes
        val maxSizeBytes = 3 * 1024 * 1024 // 3MB in bytes

        if (imageSize >= maxSizeBytes) {
            // Show a toast message indicating that the image size is too large
            Toast.makeText(this, "Image size is too large (Max: 3MB)", Toast.LENGTH_SHORT).show()
            return // Don't proceed with processing the image
        }

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
        bindingCourseUploadActivity.setImageUrl.setText(imageName?.trim().toString())
        bindingCourseUploadActivity.setImageUrl.visibility=View.GONE
        println("Image Name: "+bindingCourseUploadActivity.setImageUrl.text.toString()+".JPG")
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
        job=repository.getAllCategories().onEach { categories ->
            println("1")
            categoryAdapter = CategoryAdapter(categories)
            recyclerViewCategories.adapter = categoryAdapter
            categoryAdapter.setChanged(categories)
            categoryAdapter.setOnItemClickListener { categorywithsubcategory ->
                allcategoriesId = categorywithsubcategory.category.categoryId
                bindingCourseUploadActivity.courseAllCategoryEditText.setText(categorywithsubcategory.category.categoryName)
                showSubCategories(categorywithsubcategory.subCategories)
                dialog.dismiss()
            }
        }.catch {exception->
            println("Exception: "+ exception.message)
        }.launchIn(lifecycleScope)

        dialog.show()
    }


    private fun showSubCategories(subCategories: List<SubCategoryEntity>) {
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
            bindingCourseUploadActivity.courseCategoryEditText.setText(subCategory.subCategoryName)
            dialog.dismiss() // Dismiss the bottom sheet dialog when a subcategory is selected
        }
        dialog.setOnDismissListener {
            if (bindingCourseUploadActivity.courseCategoryEditText.text!!.isEmpty()) {
                bindingCourseUploadActivity.courseAllCategoryEditText.text!!.clear()
            }
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
                   bindingCourseUploadActivity.courseRegionEditText.setText(region.regionName)
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
                    bindingCourseUploadActivity.courseModeEditText.setText(mode.modeName)
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
            bindingCourseUploadActivity.uploadCourse.apply {
                isEnabled = false
                text = "Kurs elanı yaradılır..."  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            bindingCourseUploadActivity.uploadCourse.apply {
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