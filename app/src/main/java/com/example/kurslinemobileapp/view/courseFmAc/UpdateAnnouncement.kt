package com.example.kurslinemobileapp.view.courseFmAc

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.LocusId
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.documentfile.provider.DocumentFile
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
import com.example.kurslinemobileapp.api.announcement.updateanddelete.UpdateAnnouncementResponse
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.companyData.SubCategory
import com.example.kurslinemobileapp.model.uploadPhoto.PhotoUpload
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.view.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_company_update.*
import kotlinx.android.synthetic.main.activity_course_upload.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.activity_update_announcement.*
import kotlinx.android.synthetic.main.activity_update_announcement.lineForCourseUpload
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class UpdateAnnouncement : AppCompatActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    private var selectedPhotos = ArrayList<String>()
    private lateinit var regionAdapter: RegionAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var modeAdapter: ModeAdapter
    private var block: Boolean = true

    companion object {
        private const val REQUEST_CODE_PERMISSION = 101
        private const val REQUEST_CODE_PICK_IMAGES = 102
    }

    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image

    var imageNames = mutableListOf<String>()
    var imageData = mutableListOf<String>()
    var images = mutableListOf<Img>()
    var teachersname= mutableListOf<String>()
    var allcategoriesId:String=""
    var categoryId: String =""
    var modeId: String = ""
    var regionId:String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_announcement)
        lineForCourseUpload.visibility = View.GONE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingDetailForUpAnn)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
        selectedPhotos= ArrayList<String>()
        images= mutableListOf()
        val sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        getUserAnnouncement(userId,annId,token!!)
        addupCoursePhotos.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission if not granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_PERMISSION
                )
            } else {
                launchGalleryIntent()
            }
        }

        backtoMainFromUpdateCourse.setOnClickListener {
            val intent=Intent(this,ProductDetailActivity::class.java)
            startActivity(intent)
            finish()
        }
        updateCourseBtn.setOnClickListener {
            val upcourseNameContainer1=upcourseNameEditText?.text?.trim().toString()
            val upcourseAboutContainer1=upcourseAboutEditText?.text?.trim().toString()
            val upcoursePriceContainer1=upcoursePriceEditText?.text?.trim().toString()
            val upcourseAddressContainer1=upcourseAddressEditText?.text?.trim().toString()
            val upAnnPhoto=UpsetImageUrl?.text?.toString()?.trim()
            val upcourseCategoryContainer1=categoryId
            val upcourseAllCategoryContainer1=allcategoriesId
            val upcourseRegionContainer1=regionId
            val upcourseModeContainer=modeId
            val upcourseteachername=upcourseTeacherEditText?.text?.trim().toString()
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
            if(upcourseteachername.isNullOrEmpty()){
                upcourseTeacherEditText.error="Teacher name is not be null"
                upcourseTeacherEditText.requestFocus()
                block=false
            }

            if (block==false){
                println("False")
            }
            println("SelectedPhotos: "+selectedPhotos)
            showProgressButton(true)
            sendUpdateAnnouncementData(upcourseNameContainer1,upcourseAboutContainer1,upcoursePriceContainer1,upcourseAddressContainer1,
                upcourseRegionContainer1,upcourseCategoryContainer1,
                selectedPhotos,upcourseteachername,upcourseModeContainer,upcourseAllCategoryContainer1,annId,token,userId
                )

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

    private fun sendUpdateAnnouncementData(
        announcementName:String,
        announcementDesc:String,
        announcementPrice:String,
        announcementAddress:String,
        announcementRegionId:String,
        AnnouncementSubCategoryId:String,
        imagePath:List<String>,
        teachersName:String,
        announcementIsOnlineId:String,
        announcementCategoryId:String,
        announcementId:Int,
        token:String,
        userId:Int
    ){
        val imageParts = mutableListOf<MultipartBody.Part>()

        // Create MultipartBody.Part for each image path
        for (imagePath in imagePath) {
            val file = File(imagePath)
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val imagePart = MultipartBody.Part.createFormData("Img[]", file.name, requestFile)
            imageParts.add(imagePart)
        }
        val annname:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(),announcementName)
        val anndesc:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(),announcementDesc)
        val annPrice:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(),announcementPrice)
        val annAddress:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(),announcementAddress)
        val annRegionId:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(),announcementRegionId)
        val annteachersName:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(),teachersName)
        val annSubId:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(),AnnouncementSubCategoryId)
        val annisOnlineId:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(),announcementIsOnlineId)
        val annCategoryId:RequestBody=RequestBody.create("text/plain".toMediaTypeOrNull(),announcementCategoryId)

        compositeDisposable= CompositeDisposable()

        val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(
            retrofit.updateAnnouncement(annname,anndesc,annPrice,annAddress,annRegionId,annSubId,imageParts,annteachersName,annisOnlineId,annCategoryId,token,userId,announcementId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleresponseforUpdateAnnouncement,
                    {
                        println("Error Message: "+it.message)
                        val text = "Məlumatlar doğru deyil"
                        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                    showProgressButton(false)
                    }
                    )
        )
    }

    private fun handleresponseforUpdateAnnouncement(response:UpdateAnnouncementResponse){
        val intent=Intent(this@UpdateAnnouncement,MainActivity::class.java)
        startActivity(intent)
        Toast.makeText(this,"Kurs Məlumatlarınız uğurla yeniləndi",Toast.LENGTH_SHORT).show()
        println(response.id)
    }

    @SuppressLint("IntentReset")
    fun launchGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, Constant.PICK_IMAGE_REQUEST)
    }
    private fun compressImageFile(imagePath: String): Bitmap? {
        val file = File(imagePath)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, options)
        val imageWidth = options.outWidth
        val imageHeight = options.outHeight
        val scaleFactor = Math.min(imageWidth / MAX_IMAGE_WIDTH, imageHeight / MAX_IMAGE_HEIGHT)
        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
/*        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            val imagePath = selectedImageUri?.let { getRealPathFromURI(it) }
            if (imagePath != null) {
                val compressedBitmap = compressImageFile(imagePath)
                UpsetImageUrl.setText(imagePath)
                if(compressedBitmap!=null){
                    val compressedImagePath = saveCompressedBitmapToFile(compressedBitmap)
                    UpsetImageUrl.setText(compressedImagePath)
                    println("CompressedImagePath"+compressedImagePath)
                }
                println(imagePath)
            }
        }*/
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUris = mutableListOf<Uri>()

            if (data?.clipData != null) {
                val clipData = data.clipData
                val count = clipData?.itemCount ?: 0
                for (i in 0 until count) {
                    val imageUri = clipData?.getItemAt(i)?.uri
                    imageUri?.let { selectedImageUris.add(it) }
                }
            } else if (data?.data != null) {
                // Only one image was selected
                val imageUri = data.data
                imageUri?.let { selectedImageUris.add(it) }
            }

            if (selectedImageUris.isNotEmpty()) {
                val selectedImagePaths = ArrayList<String>()
                for (uri in selectedImageUris) {
                    val imagePath = getRealPathFromURI(uri)

                    imagePath?.let {
                        val compressedBitmap = compressImageFile(it)
                        if (compressedBitmap != null) {
                            val compressedImagePath = saveCompressedBitmapToFile(compressedBitmap)
                            selectedImagePaths.add(compressedImagePath ?: it)
                        } else {
                            selectedImagePaths.add(it)
                        }
                    }
                }
                /*uploadImagesToServer(selectedImagePaths)*/
                selectedPhotos=selectedImagePaths
                println("Images: "+ selectedImagePaths)

            }
        }
    }

    private fun saveCompressedBitmapToFile(bitmap: Bitmap): String? {
        val outputDir = this?.cacheDir // Get the directory to store the compressed image
        val outputFile = File.createTempFile("compressed_", ".jpg", outputDir)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Compress and save the bitmap as JPEG with 80% quality
            return outputFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }
        return null
    }
    private fun getRealPathFromURI(uri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
        return path
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
        //teachersname=response.teacher
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
                        modeId = mode.isOnlineId.toString()
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
                        allcategoriesId = category.categoryId.toString()
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
            categoryId = subCategory.subCategoryId.toString()
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
                        regionId = region.regionId.toString()
                        dialog.dismiss()
                    }
                }, { throwable -> println("MyTestsRegions: $throwable") })
        )
        dialog.show()
    }
    private fun showProgressButton(show: Boolean) {
        if (show) {
            updateCourseBtn.apply {
                isEnabled = false
                text = "Kurs elanı yenilənir..."  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            updateCourseBtn.apply {
                isEnabled = true
                text = "Kursu əlavə et"
                // Restore original background, text color, etc., if modified
            }
        }
    }
}