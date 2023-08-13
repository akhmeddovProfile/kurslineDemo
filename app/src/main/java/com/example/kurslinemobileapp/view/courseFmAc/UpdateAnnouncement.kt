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
        lineForCourseUpload.visibility = View.GONE
        val lottie = findViewById<LottieAnimationView>(R.id.loadingDetailForUpAnn)
        lottie.visibility = View.VISIBLE
        lottie.playAnimation()
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
        getUserAnnouncement(userId,annId,authHeader)
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


        updateCourseBtn.setOnClickListener {
            val upcourseNameContainer1=upcourseNameEditText?.text?.trim().toString()
            val upcourseAboutContainer1=upcourseAboutEditText?.text?.trim().toString()
            val upcoursePriceContainer1:Int=upcoursePriceEditText?.text?.toString()!!.toInt()
            val upcourseAddressContainer1=upcourseAddressEditText?.text?.trim().toString()
            val upAnnPhoto=selectedPhotos
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
            if(upcoursePriceContainer1.equals("")){
                upcoursePriceEditText.error="Course Price is not be null"
                upcoursePriceEditText.requestFocus()
                block=false
            }
            if(upcourseAddressContainer1.isNullOrEmpty()){
                upcourseAddressEditText.error="Course Address is not be null"
                upcourseAddressEditText.requestFocus()
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

            val name = upcourseTeacherEditText.text.toString().trim()
            if (name.isNotEmpty()){
                teachersname.add(name)
            }
            for(i in 0 until imageNames.size){
                val img = Img(imageNames[i], imageData[i].toString())
                images.add(img)
            }
            println("SelectedPhotos: "+selectedPhotos.size)
            showProgressButton(true)

            sendUpdateAnn(authHeader,userId,annId, CreateAnnouncementRequest(upcourseNameContainer1,upcourseAboutContainer1,upcoursePriceContainer1,upcourseAddressContainer1,
            upcourseModeContainer,upcourseAllCategoryContainer1,upcourseCategoryContainer1,upcourseRegionContainer1,images,teachersname
                ))
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
        val viewPager = findViewById<ViewPager2>(R.id.viewPagerCourseUpload)

        // Create the adapter with the selected photos list
        val adapter = PhotoPagerAdapter( selectedPhotos)
        viewPager.adapter = adapter
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
     val annmode=response.isOnline.toInt()
     val category=response.categoryId
     val subcategory=response.announcementSubCategoryId
     val regionname=response.announcementRegionId.toInt()

        upcourseNameEditText.setText(nameofcourse)
        upcourseAboutEditText.setText(coursedesc)
        val listofteachers=teachers.joinToString("[ , ]")
        val textView = findViewById<TextInputEditText>(R.id.upcourseTeacherEditText)
        textView.setText(listofteachers)
        upcoursePriceEditText.setText(price.toString())
        upcourseAddressEditText.setText(address)
        upcourseModeEditText.setOnClickListener {
            showBottomSheetDialogMode()
        }
        println("category "+category)
        var categoryName = ""
        var subcategorName=""
        getCategoryList()!!.subscribe({ categories ->
            println("333")
            categoryName = categories.categories.find { it.categoryId == category }?.categoryName.toString()
            upcourseAllCategoryEditText.setText(categoryName)

            subcategorName = categories.categories.find { category ->
                category.subCategories.find { subCategory ->
                    subCategory?.subCategoryCategoryId == subcategory
                } != null
            }?.subCategories?.find { subCategory ->
                subCategory?.subCategoryCategoryId == subcategory
            }?.subCategoryName.toString()
            courseSubCategoryEditText.setText(subcategorName)

        }, { throwable ->
            // Handle error during category retrieval
            println("Category retrieval error: $throwable")
        }).let { compositeDisposable.add(it) }


        var statusName = ""
        getStatusList()!!.subscribe({ status ->
            statusName = status.statuses.find { it.statusId == annmode }?.statusName.toString()
            upcourseModeEditText.setText(statusName)
        }, { throwable ->
            // Handle error during category retrieval
            println("Category retrieval error: $throwable")
        }).let { compositeDisposable.add(it) }

        var regionName = ""
        getRegionList()!!.subscribe({ region ->
            regionName = region.regions.find { it.regionId == regionname }?.regionName.toString()
            upcourseRegionEditText.setText(regionName)
        }, { throwable ->
            // Handle error during category retrieval
            println("Category retrieval error: $throwable")
        }).let { compositeDisposable.add(it) }


        upcourseAllCategoryEditText.setText(category.toString())
        upcourseAllCategoryEditText.setOnClickListener {
            showBottomSheetDialogAllCatogories()
        }
        upcourseRegionEditText.setOnClickListener {
            showBottomSheetDialogRegions()
        }


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