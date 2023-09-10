package com.example.kurslinemobileapp.view.courseFmAc

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.LocusId
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.*
import com.example.kurslinemobileapp.api.announcement.AnnouncementAPI
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.CreateAnnouncementRequest
import com.example.kurslinemobileapp.api.announcement.createAnnouncement.Img
import com.example.kurslinemobileapp.api.announcement.getDetailAnnouncement.Photo
import com.example.kurslinemobileapp.api.announcement.updateanddelete.GetAnnouncementResponse
import com.example.kurslinemobileapp.api.announcement.updateanddelete.GetUserAnn
import com.example.kurslinemobileapp.api.announcement.updateanddelete.UpdateAnnouncementResponse
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.companyData.CompanyRegisterData
import com.example.kurslinemobileapp.api.companyData.SubCategory
import com.example.kurslinemobileapp.model.uploadPhoto.PhotoUpload
import com.example.kurslinemobileapp.model.uploadPhoto.SelectionPhotoShowOnViewPager
import com.example.kurslinemobileapp.model.uploadPhoto.ViewPagerFormData
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.service.Room.category.MyRepositoryForCategory
import com.example.kurslinemobileapp.service.Room.category.SubCategoryEntity
import com.example.kurslinemobileapp.service.Room.region.RegionViewModel
import com.example.kurslinemobileapp.view.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_company_update.*
import kotlinx.android.synthetic.main.activity_course_upload.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.activity_update_announcement.*
import kotlinx.android.synthetic.main.activity_update_announcement.lineForCourseUpload
import kotlinx.android.synthetic.main.fragment_business_account.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*

class UpdateAnnouncement : AppCompatActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
   // private var selectedPhotos = ArrayList<SelectionPhotoShowOnViewPager>()
    private val selectedImages = ArrayList<ViewPagerFormData>()
    private lateinit var regionAdapter: RegionAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var modeAdapter: ModeAdapter
    val imagesPaths = ArrayList<String>()
    val imagesPaths2 = ArrayList<String>()
    private var block: Boolean = true
    private var job: Job? = null

    private val viewModel: RegionViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var repository: MyRepositoryForCategory

    companion object {
        private const val REQUEST_CODE_PERMISSION = 101
        private const val REQUEST_CODE_PICK_IMAGES = 102
    }

    var courseName:String?=null
    var aboutrcourse:String?=null
    var teachersnames:String?=null
    var announcementPrice:Double?=0.0
    var announcementAdress:String?=null
    lateinit var announcementModeId:String
    lateinit var announcementCategoryId:String
    lateinit var announcementSubcategoryId:String
    lateinit var announcementRegionID:String

    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image

    var imageNames = mutableListOf<String>()
    var imageData = mutableListOf<String>()
    var images = mutableListOf<Img>()
    var teachersname= mutableListOf<String>()
    lateinit var allcategoriesId:String
    lateinit var categoryId:String
    lateinit var modeId: String
    lateinit var regionId:String

    var courseNameChanged = false
    var aboutCourseChanged = false
    var teacherNameChanged = false
    var coursePriceChanged=false
    var courseAddressChanged=false
    var modeChanged=false
    var categoryChanged=false
    var subCategoryChanged=false
    var chooseRegionChaged=false

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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGalleryMultipart()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_announcement)

        modeId=""
        allcategoriesId=""
        categoryId=""
        regionId=""
        //selectedPhotos= ArrayList<SelectionPhotoShowOnViewPager>()
        images= mutableListOf()

        repository = MyRepositoryForCategory(
            AppDatabase.getDatabase(this).categoryDao(),
            AppDatabase.getDatabase(this).subCategoryDao()
        )


        val sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val annId = sharedPreferences.getInt("announcementId",0)
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"

        upcourseNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                courseNameChanged = true
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        upcourseAboutEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                aboutCourseChanged = true
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        upcourseTeacherEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                teacherNameChanged = true
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        upcoursePriceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                coursePriceChanged = true
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        upcourseAddressEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                courseAddressChanged = true
            }
            override fun afterTextChanged(s: Editable?) {}
        })
     /*   upcourseModeEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                modeChanged = true
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        upcourseAllCategoryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                categoryChanged = true
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        courseSubCategoryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                subCategoryChanged = true
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        upcourseRegionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                chooseRegionChaged = true
            }
            override fun afterTextChanged(s: Editable?) {}
        })*/

        addupCoursePhotos.setOnClickListener {
            if(!checkPermission()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    checkAndRequestPermissions()
                }
                /*       val miuiVersion = BuildProperties.getMIUIVersion()
                       if (miuiVersion == "14.0.3") {
                           showMIUIExplanationDialog() // Show a custom explanation
                       }*/
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) // Launch permission request directly
            }
            else{
                openGalleryMultipart()
            }

            updateCourseBtn.setOnClickListener {
                showProgressButton(true)
                val modifiedCourseName = if (courseNameChanged) upcourseNameEditText.text.toString() else courseName
                val modifiedAboutCourse = if (aboutCourseChanged) upcourseAboutEditText.text.toString() else aboutrcourse
                val modifiedTeacherName = if (teacherNameChanged) upcourseTeacherEditText.text.toString() else teachersname
                val modifiedPrice=if (coursePriceChanged)upcoursePriceEditText.text.toString().toDouble() else announcementPrice
                val modifiedAddress=if (courseAddressChanged)upcourseAddressEditText.text.toString()else announcementAdress
                val modifiedmodeId=if(modeChanged)modeId else announcementModeId
                val modifiedCategoryId=if (categoryChanged)allcategoriesId else announcementCategoryId
                val modifiedSubCategory=if (subCategoryChanged)categoryId else announcementSubcategoryId
                val modifiefRegion=if (chooseRegionChaged)regionId else announcementRegionID
              /*  for (imagepath in selectedImages){
                    images= imagepath.imagePath

                }*/

                val modeid=if(modeChanged){
                    modeId
                }
                else{
                    announcementModeId
                }

                println("modifiedmodeId2: "+modifiedmodeId)
                println("modeid: "+modeid)

                sendUpdateAnnouncement(
                    modifiedCourseName!!,
                    modifiedAboutCourse!!,
                    modifiedTeacherName.toString(),
                    modifiedPrice!!,
                    modifiedAddress!!,
                    modifiedmodeId!!,
                    modifiedCategoryId!!,
                    modifiedSubCategory!!,
                    modifiefRegion!!,
                    imagesPaths2,
                    authHeader,
                    userId,
                    annId
                )
            }
        }


        backtoMainFromUpdateCourse.setOnClickListener {
            val intent=Intent(this,ProductDetailActivity::class.java)
            startActivity(intent)
            finish()
        }

        val category=sharedPreferences.getString("annCategory","")
        val subcategory=sharedPreferences.getString("annSubCategory","")
        println("SubCategory: "+subcategory)
        println("Category: "+category)

        getUpdateAnnouncement(userId,annId,authHeader)
        upcourseModeEditText.setOnClickListener {
            showBottomSheetDialogMode()

        }
        upcourseAllCategoryEditText.setOnClickListener {
            showBottomSheetDialogAllCatogories()
        }
        upcourseRegionEditText.setOnClickListener{
            showBottomSheetDialogRegions()
        }
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted, proceed with accessing gallery
                openGalleryMultipart()
            } else {
                // Request permission
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            showPermissionMessageForOlderDevices()
            // Handle devices with API level lower than 31
            // You might want to show a message or handle it differently
        }
    }
    private fun showPermissionMessageForOlderDevices() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("To access the gallery, you need to grant storage permission.")
            .setPositiveButton("Grant Permission") { dialog, _ ->
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getUpdateAnnouncement(userId: Int,announcementId: Int,token: String){
        compositeDisposable= CompositeDisposable()
        val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(
            retrofit.getAnnouncementForUpdate(userId,announcementId,token).
            subscribeOn(Schedulers.io()).
            observeOn(AndroidSchedulers.mainThread()).
            subscribe(
                this::handleresponseforgetannUpdate
                       ,{
                    println(it.message)
                }
            )
        )
    }

    @SuppressLint("CheckResult")
    private fun handleresponseforgetannUpdate(response:GetAnnouncementResponse){
        println("Response: "+response)
         courseName=response.announcementName
         aboutrcourse=response.announcementName
        teachersnames=response.teacher.toString()
         announcementPrice=response.announcementPrice.toDouble()
         announcementAdress=response.announcementAddress
         announcementModeId=response.isOnline.toString()
         announcementCategoryId=response.announcementCategoryId.toString()
         announcementSubcategoryId=response.announcementSubCategoryId.toString()
         announcementRegionID=response.announcementRegionId.toString()
        upcourseNameEditText.setText(courseName)
        upcourseAboutEditText.setText(aboutrcourse)
        upcourseTeacherEditText.setText(teachersnames)
        upcoursePriceEditText.setText(announcementPrice.toString())
        upcourseAddressEditText.setText(announcementAdress)
/*        upcourseAllCategoryEditText.setText(announcementCategory)
        courseSubCategoryEditText.setText(announcementSubcategory)*/
        var announcementRegionName=""
        var modeName=""
        var categoryname=""
        var subcategoryName=""
        println("announcementModeId: "+announcementModeId)
        getRegionList()!!.subscribe({
            announcementRegionName=it.regions.find{
                it.regionId==announcementRegionID.toInt()
            }?.regionName.toString()
            println(announcementRegionName)
            upcourseRegionEditText.setText(announcementRegionName.trim().toString())
        },{throwable->
            println("Category retrieval error: $throwable")

        }
        ).let { compositeDisposable.add(it) }

        getStatusList()!!.subscribe({
            modeName=it.isOnlines.find {
                it.isOnlineId==announcementModeId.toInt()
            }?.isOnlineName.toString()
            upcourseModeEditText.setText(modeName.trim())
        },{throwable->
            println("Region retrieval error: $throwable")

        }).let {
            compositeDisposable.add(it)
        }

        getCategoryList()!!.subscribe({response->
            val foundCategory = response.categories.find { it.categoryId == announcementCategoryId.toInt() }
            categoryname = foundCategory?.categoryName ?: ""
            upcourseAllCategoryEditText.setText(categoryname)
            // Now, find the subcategory within the found category
            val foundSubCategory = foundCategory?.subCategories?.find { it.subCategoryCategoryId == announcementSubcategoryId.toInt() }
            subcategoryName = foundSubCategory?.subCategoryName ?: ""
            courseSubCategoryEditText.setText(subcategoryName)

            // Add logging statements to check values
            println("categoryname: $categoryname")
            println("subcategoryName: $subcategoryName")
        }, { throwable ->
            println("Error getting categories: $throwable")
        })

    }

    @SuppressLint("SuspiciousIndentation")
    private fun sendUpdateAnnouncement(
        nameofCourse:String,
        courseDESC:String,
        teachersname:String,
        coursePrice:Double,
        courseAddress:String,
        courseMode:String,
        courseCategory:String,
        courseSubCategory:String,
        courseRegion:String,
        imagePath:List<String>?,
        token: String,
        userId:Int,
        announcementId: Int)
    {
        val photos = ArrayList<MultipartBody.Part>()

        if (imagePath != null) {
            for (imagePath in imagePath) {
                val file = File(imagePath)
                val reqFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val photoPart=MultipartBody.Part.createFormData("Photos", file.name, reqFile)
                    photos.add(photoPart)
            }
        }

        val nameofUpCourse: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), nameofCourse)
        val nameofUpDESC: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), courseDESC)
        val nameofUpTeachers: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), teachersname)
        val priceUp: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), coursePrice.toString())
        val nameofUpAddress: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), courseAddress)
        val modeofUpCourse: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), courseMode)
        val cateegoryofUpCourse: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), courseCategory)
        val subcateegoryofUpCourse: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), courseSubCategory)
        val regionofUpCourse: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), courseRegion)

        compositeDisposable= CompositeDisposable()
        val retrofit=RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(
            retrofit.updateAnnouncementFormData(
                nameofUpCourse,
                nameofUpDESC,
                priceUp,
                nameofUpAddress,
                cateegoryofUpCourse,
                subcateegoryofUpCourse,
                regionofUpCourse,
                photos,
                nameofUpTeachers,
                modeofUpCourse,
                token,
                userId,
                announcementId,
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Toast.makeText(this@UpdateAnnouncement,"Update Successfully.",Toast.LENGTH_SHORT).show()
                        val intent:Intent
                        intent=Intent(this@UpdateAnnouncement,ProductDetailActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    {
                        showProgressButton(false)
                        println("Success: "+ it)
                    }
                )
        )

    }


/*    private fun sendUpdateAnn(token: String,userId:Int,announcementId: Int,createAnnouncementRequest: CreateAnnouncementRequest){
        compositeDisposable=CompositeDisposable()
        val retrofitService=RetrofitService(Constant.BASE_URL).retrofit.create(AnnouncementAPI::class.java)
        compositeDisposable.add(
            retrofitService.updateAnnJSON(token,userId,announcementId,createAnnouncementRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleresponseforUpdateAnnouncement
                ,{

                })
        )

    }*/

    private fun handleresponseforUpdateAnnouncement(response:UpdateAnnouncementResponse){
        val intent=Intent(this@UpdateAnnouncement,MainActivity::class.java)
        startActivity(intent)
        Toast.makeText(this,"Kurs Məlumatlarınız uğurla yeniləndi",Toast.LENGTH_SHORT).show()
        println(response.id)
    }




private fun setUpViewPagerFileFormat(){
    val viewPager = findViewById<ViewPager2>(R.id.viewPagerCourseUpdate)

    // Create the adapter with the selected photos list

    for (imageData in selectedImages) {
        val imagePath = imageData.imagePath
        if (imagePath != null) {
            imagesPaths.add(imagePath)
        }

    }
    println("SelectedImagesPath: $imagesPaths")

    val adapter = PhotoPagerAdapterForFormData(selectedImages)
    viewPager.adapter = adapter

}
///////////////////////////////////////////////////////////////////////////////
    @SuppressLint("IntentReset")
    private fun openGalleryMultipart() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //intent.type = "image/*"
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
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUris = data?.clipData
            val singleSelectedImageUri = data?.data

            if (selectedImageUris != null) {
                // Handle multiple selected images
                for (i in 0 until selectedImageUris.itemCount) {
                    val selectedImageUri = selectedImageUris.getItemAt(i).uri
                    val imagePath = selectedImageUri?.let { getRealPathFromURI(it) }
                    if (imagePath != null) {
                        val compressedBitmap = compressImageFile(imagePath)
                        val compressedImagePath = saveCompressedBitmapToFile(compressedBitmap!!)
                        val selectedImage = ViewPagerFormData(compressedImagePath!!, compressedBitmap)
                        selectedImages.add(selectedImage)
                        imagesPaths2.add(imagePath)
                    }
                }

            } else if (singleSelectedImageUri != null) {
                // Handle single selected image
                val imagePath = singleSelectedImageUri.let { getRealPathFromURI(it) }
                if (imagePath != null) {
                    val compressedBitmap = compressImageFile(imagePath)
                    val compressedImagePath = saveCompressedBitmapToFile(compressedBitmap!!)
                    val selectedImage = ViewPagerFormData(compressedImagePath!!, compressedBitmap)
                    selectedImages.add(selectedImage)
                }
            }

            println("imagesPaths2: "+ imagesPaths2)
            // After adding the selected images to the list, update the ViewPager
            setUpViewPagerFileFormat()
        }
    }

    private fun saveCompressedBitmapToFile(bitmap: Bitmap): String? {
        val outputDir = this?.cacheDir // Get the directory to store the compressed image
        val outputFile = File.createTempFile("compressed_", ".jpg", outputDir)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(outputFile)
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                80,
                outputStream
            ) // Compress and save the bitmap as JPEG with 80% quality
            return outputFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }
        return null
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


    private fun getRealPathFromURI(uri: Uri): String? {
        var path: String? = null
        val imageSize = getFileSize(uri) // Get the image size in bytes
        val maxSizeBytes = 3 * 1024 * 1024
        if (imageSize > maxSizeBytes) {
            // Show a toast message indicating that the image size is too large
            Toast.makeText(this, "Image size is too large (Max: 3MB)", Toast.LENGTH_SHORT).show()
            return null// Don't proceed with processing the image
        }
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

        job =appdatabase.modeDao().getAllMode()
            .onEach { mode ->
                modeAdapter = ModeAdapter(mode)
                println("3")
                recyclerViewMode.adapter = modeAdapter
                modeAdapter.setChanged(mode)
                modeAdapter.setOnItemClickListener { mode ->
                    upcourseModeEditText.setText(mode.modeName)
                    modeId = mode.modeId.toString()
                    modeChanged=true
                    dialog.dismiss()
                }
            }.catch {

            }.launchIn(lifecycleScope)
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
        job=repository.getAllCategories().onEach { categories ->
            println("1")
            categoryAdapter = CategoryAdapter(categories)
            recyclerViewCategories.adapter = categoryAdapter
            categoryAdapter.setChanged(categories)
            categoryAdapter.setOnItemClickListener { categorywithsubcategory ->
                allcategoriesId = categorywithsubcategory.category.categoryId.toString()
                upcourseAllCategoryEditText.setText(categorywithsubcategory.category.categoryName)
                showSubCategories(categorywithsubcategory.subCategories)
                categoryChanged=true
                dialog.dismiss()
            }
        }.catch {

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
            categoryId = subCategory.subCategoryId.toString()
            courseSubCategoryEditText.setText(subCategory.subCategoryName)
            subCategoryChanged=true
            dialog.dismiss() // Dismiss the bottom sheet dialog when a subcategory is selected
        }
        dialog.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetDialogRegions() {
        val appdatabase = AppDatabase.getDatabase(applicationContext)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_region, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerviewRegions: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewRegions)
        recyclerviewRegions.setHasFixedSize(true)
        recyclerviewRegions.setLayoutManager(LinearLayoutManager(this))

        job=appdatabase.regionDao().getAllRegions()
            .onEach {reg->
                println("2")
                regionAdapter = RegionAdapter(reg)
                recyclerviewRegions.adapter = regionAdapter
                regionAdapter.setChanged(reg)
                regionAdapter.setOnItemClickListener { region ->
                    upcourseRegionEditText.setText(region.regionName)
                    regionId = region.regionId.toString()
                    chooseRegionChaged
                    dialog.dismiss()
                }
            }.catch {throwable->
                println("Error 2: "+throwable.message)
            }.launchIn(lifecycleScope)
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

    private fun getCategoryList(): Observable<CompanyRegisterData>? {
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        return retrofit.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getStatusList(): Observable<CompanyRegisterData>?{
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        return retrofit.getStatus()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
    private fun getRegionList():Observable<CompanyRegisterData>?{
        val retrofit = RetrofitService(Constant.BASE_URL).retrofit.create(CompanyDatasAPI::class.java)
        return retrofit.getRegions()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}