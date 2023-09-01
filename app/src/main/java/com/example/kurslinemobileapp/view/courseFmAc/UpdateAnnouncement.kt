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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import com.example.kurslinemobileapp.api.announcement.updateanddelete.GetUserAnn
import com.example.kurslinemobileapp.api.announcement.updateanddelete.UpdateAnnouncementResponse
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.companyData.CompanyRegisterData
import com.example.kurslinemobileapp.api.companyData.SubCategory
import com.example.kurslinemobileapp.model.uploadPhoto.PhotoUpload
import com.example.kurslinemobileapp.model.uploadPhoto.SelectionPhotoShowOnViewPager
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
    private var selectedPhotos = ArrayList<SelectionPhotoShowOnViewPager>()
    private lateinit var regionAdapter: RegionAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var modeAdapter: ModeAdapter
    private var block: Boolean = true
    private var job: Job? = null

    private val viewModel: RegionViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var repository: MyRepositoryForCategory

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


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_announcement)


        selectedPhotos= ArrayList<SelectionPhotoShowOnViewPager>()
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

        val uProdCategory = sharedPreferences.getString("productDetailCategory", "") ?: ""
        val uProdSubCategory = sharedPreferences.getString("productDetailSubCategory", "") ?: ""
        val uProdPrice = sharedPreferences.getString("productDetailPrice", "") ?: ""
        val uProdName = sharedPreferences.getString("productDetailName", "") ?: ""
        val uProdDesc = sharedPreferences.getString("productDetailDesc", "") ?: ""
        val uProdRegion = sharedPreferences.getString("productDetailRegion", "") ?: ""
        val uProdMode = sharedPreferences.getString("productDetailMode", "") ?: ""
        val uProdTeacher = sharedPreferences.getString("productDetailTeacher", "") ?: ""
        val uProdAddress = sharedPreferences.getString("productDetailAddress", "") ?: ""
        val jsonImageUrls = intent.getStringExtra("imageUrlsJson")

        upcourseNameEditText.setText(uProdName)
        upcourseAboutEditText.setText(uProdDesc)
        upcourseTeacherEditText.setText(uProdTeacher)
        upcoursePriceEditText.setText(uProdPrice)
        upcourseAddressEditText.setText(uProdAddress)
        upcourseModeEditText.setText(uProdMode)
        upcourseAllCategoryEditText.setText(uProdCategory)
        courseSubCategoryEditText.setText(uProdSubCategory)
        upcourseRegionEditText.setText(uProdRegion)

        if (jsonImageUrls != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Photo>>() {}.type
            val imageUrls = gson.fromJson<List<Photo>>(jsonImageUrls, type)

            val viewPager: ViewPager2 = findViewById(R.id.viewPagerCourseUpdate)
            val photoAdapter = ProductDetailImageAdapter(imageUrls)
            viewPager.adapter = photoAdapter
        }
     //   businessAccountUpdateNameEditText.setText(userFullName)





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
                requestGalleryPermission()
                openGallery()
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


    }



    private fun sendUpdateAnn(token: String,userId:Int,announcementId: Int,createAnnouncementRequest: CreateAnnouncementRequest){
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

    }

    private fun handleresponseforUpdateAnnouncement(response:UpdateAnnouncementResponse){
        val intent=Intent(this@UpdateAnnouncement,MainActivity::class.java)
        startActivity(intent)
        Toast.makeText(this,"Kurs Məlumatlarınız uğurla yeniləndi",Toast.LENGTH_SHORT).show()
        println(response.id)
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
        UpsetImageUrl.setText(imageName?.trim().toString())
        UpsetImageUrl.visibility=View.GONE
        println("Image Name: "+UpsetImageUrl.text.toString()+".JPG")
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
        val viewPager = findViewById<ViewPager2>(R.id.viewPagerCourseUpdate)

        // Create the adapter with the selected photos list
        val adapter = PhotoPagerAdapter(selectedPhotos)
        viewPager.adapter = adapter
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
                    modeId = mode.modeId
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
                allcategoriesId = categorywithsubcategory.category.categoryId
                upcourseAllCategoryEditText.setText(categorywithsubcategory.category.categoryName)
                showSubCategories(categorywithsubcategory.subCategories)
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
            categoryId = subCategory.subCategoryId
            courseSubCategoryEditText.setText(subCategory.subCategoryName)
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
                    regionId = region.regionId
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