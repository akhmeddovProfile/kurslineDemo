package com.example.kurslinemobileapp.view.accountsFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.InputFilter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.kurslinemobileapp.R
import com.app.kurslinemobileapp.databinding.ActivityCompanyUpdateBinding
import com.example.kurslinemobileapp.adapter.CategoryAdapter
import com.example.kurslinemobileapp.adapter.ModeAdapter
import com.example.kurslinemobileapp.adapter.RegionAdapter
import com.example.kurslinemobileapp.adapter.StatusAdapter
import com.example.kurslinemobileapp.api.updateUserCompany.UpdateAPI
import com.example.kurslinemobileapp.api.updateUserCompany.UpdateResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.service.Room.category.MyRepositoryForCategory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CompanyUpdateActivity : AppCompatActivity() {
    private var block: Boolean = true
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var regionAdapter: RegionAdapter
    private lateinit var modeAdapter: ModeAdapter
    private lateinit var statusAdapter: StatusAdapter
    private lateinit var bindingCompanyBinding: ActivityCompanyUpdateBinding
    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image
    lateinit var categoryId: String
    lateinit var statusId: String
    lateinit var regionId: String

    private var job: Job? = null
    private lateinit var repository: MyRepositoryForCategory

    private var isCategoryChanged: Boolean = false
    private var isStatusChanged: Boolean = false
    private var isRegionChanged: Boolean = false

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
            openGallery()
        } else {

            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingCompanyBinding=ActivityCompanyUpdateBinding.inflate(layoutInflater)
        val view=bindingCompanyBinding.root
        setContentView(view)
        //setContentView(R.layout.activity_company_update)
        sharedPreferences = this.getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("userID", 0)
        val token = sharedPreferences.getString("USERTOKENNN", "")
        val authHeader = "Bearer $token"
        println("userID" + id)
        println("userToken" + authHeader)

        repository = MyRepositoryForCategory(
            AppDatabase.getDatabase(this).categoryDao(),
            AppDatabase.getDatabase(this).subCategoryDao()
        )

        bindingCompanyBinding.businessAccountUpdateCategoryEditText.setOnClickListener {
            showBottomSheetDialog()
        }
        bindingCompanyBinding.businessAccountUpdateRegionEditText.setOnClickListener {
            println("onclcik")
            showBottomSheetDialogRegions()
        }
        bindingCompanyBinding.companyUpdateStatusEditText.setOnClickListener {
            showBottomSheetDialogStatus()
        }

        categoryId = ""
        statusId = ""
        regionId = ""

        val categoryCMPid = sharedPreferences.getString("companyCategoryId", "") ?: ""
        val statusCMPid = sharedPreferences.getString("userStatusId", "") ?: ""
        val regionCMPid = sharedPreferences.getString("companyRegionId", "") ?: ""
        val userFullName = sharedPreferences.getString("companyOwnerName", "") ?: ""
        val userEmail = sharedPreferences.getString("companyEmail", "") ?: ""
        val userPhoneNumber = sharedPreferences.getString("companyNumber", "") ?: ""
        val companyName = sharedPreferences.getString("companyName", "") ?: ""
        val userAddress = sharedPreferences.getString("companyAddress", "") ?: ""
        val userAbout = sharedPreferences.getString("companyAbout", "") ?: ""
        val userPhoto = sharedPreferences.getString("companyPhoto", "") ?: ""
        val companyCategory = sharedPreferences.getString("companyCategory", "") ?: ""
        val companyStatus = sharedPreferences.getString("companyStatus", "") ?: ""
        val companyRegion = sharedPreferences.getString("companyRegion", "") ?: ""
        bindingCompanyBinding.businessAccountUpdateNameEditText.setText(userFullName)
        bindingCompanyBinding.businessAccountUpdateEmailEditText.setText(userEmail)
        bindingCompanyBinding.businessAccountUpdateCompanyEditText.setText(companyName)
        bindingCompanyBinding.companyUpdateAdressEditText.setText(userAddress)

        val number2 = if (userPhoneNumber.startsWith("+994")) {
            userPhoneNumber.substring(4)
        } else {
            userPhoneNumber
        }

        bindingCompanyBinding.businessAccountUpdatePhoneEditText.setText(number2)
        bindingCompanyBinding.businessAccountAboutEditText.setText(userAbout)
        bindingCompanyBinding.businessAccountUpdateCategoryEditText.setText(companyCategory)
        bindingCompanyBinding.companyUpdateStatusEditText.setText(companyStatus)
        bindingCompanyBinding.businessAccountUpdateRegionEditText.setText(companyRegion)
        if (userPhoto == null){
             bindingCompanyBinding.myCompanyUpdateProfilePhoto.setImageResource(R.drawable.setpp)
        }else{
            Picasso.get().load(userPhoto).into(bindingCompanyBinding.myCompanyUpdateProfilePhoto)
        }

        bindingCompanyBinding.savedUpdatesBtnCompany.setOnClickListener {
            block = true
            val companyNameContainer = bindingCompanyBinding.businessAccountUpdateNameEditText.text.toString().trim()
            val companyEmailContainer = bindingCompanyBinding.businessAccountUpdateEmailEditText.text.toString().trim()
            val companyFullNameContainer = bindingCompanyBinding.businessAccountUpdateCompanyEditText.text.toString().trim()
            val companyAddressContainer = bindingCompanyBinding.companyUpdateAdressEditText.text.toString().trim()
            val companyPhoneContainer = bindingCompanyBinding.businessAccountUpdatePhoneEditText.text.toString().trim()
            //  val companyModeContainer = companyModeEditText.text.toString().trim()
            val aboutCompanyContainer = bindingCompanyBinding.businessAccountAboutEditText.text.toString().trim()
            val category = bindingCompanyBinding.businessAccountUpdateCategoryEditText.text.toString().trim()
            val status = bindingCompanyBinding.companyUpdateStatusEditText.text.toString().trim()
            val region = bindingCompanyBinding.businessAccountUpdateRegionEditText.text.toString().trim()
            bindingCompanyBinding.businessAccountUpdatePhoneEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(13))
            if(companyNameContainer.isEmpty()){
                bindingCompanyBinding.businessAccountUpdateNameEditText.requestFocus()
                bindingCompanyBinding.businessAccountUpdateNameEditText.error = "Full name is not be empty"
                block  = false
            }
            if(companyEmailContainer.isEmpty()){
                bindingCompanyBinding.businessAccountUpdateEmailEditText.requestFocus()
                bindingCompanyBinding.businessAccountUpdateEmailEditText.error = "Email address is not be empty"
                block  = false
            }
            if(companyFullNameContainer.isEmpty()){
                bindingCompanyBinding.businessAccountUpdateCompanyEditText.requestFocus()
                bindingCompanyBinding.businessAccountUpdateCompanyEditText.error = "Company Name is not be empty"
                block  = false
            }
            if(companyAddressContainer.isEmpty()){
                bindingCompanyBinding.companyUpdateAdressEditText.requestFocus()
                bindingCompanyBinding.companyUpdateAdressEditText.error = "Company address is not be empty"
                block  = false
            }
            if(companyPhoneContainer.isEmpty()){
                bindingCompanyBinding.businessAccountUpdatePhoneEditText.requestFocus()
                bindingCompanyBinding.businessAccountUpdatePhoneEditText.error =  "Phone is not be empty"
                block  = false
            }
            if(aboutCompanyContainer.isEmpty()){
                bindingCompanyBinding.businessAccountAboutEditText.requestFocus()
                bindingCompanyBinding.businessAccountAboutEditText.error = "Company about is not be empty"
                block  = false
            }
            if(category.isEmpty()){
                bindingCompanyBinding.businessAccountUpdateCategoryEditText.requestFocus()
                bindingCompanyBinding.businessAccountUpdateCategoryEditText.error = "Company category is not be empty"
                block  = false
            }
            if(status.isEmpty()){
                bindingCompanyBinding.companyUpdateStatusEditText.requestFocus()
                bindingCompanyBinding.companyUpdateStatusEditText.error = "Company status is not be empty"
                block  = false
            }
            if(region.isEmpty()){
                bindingCompanyBinding.businessAccountUpdateRegionEditText.requestFocus()
                bindingCompanyBinding.businessAccountUpdateRegionEditText.error = "Company region is not be empty"
                block  = false
            }


                val imageUrl = if (bindingCompanyBinding.companyUpdatePhotoURLEditText.text.toString().isNotEmpty()) {
                    bindingCompanyBinding.companyUpdatePhotoURLEditText.text.toString().trim()
                } else {
                    null
                }

                val categoryContainer = if (isCategoryChanged) {
                    categoryId
                } else {
                    categoryCMPid
                }

                val statusContainer = if (isStatusChanged) {
                    statusId
                } else {
                    statusCMPid
                }

                val regionContainer = if (isRegionChanged) {
                    regionId
                } else {
                    regionCMPid
                }


                showProgressButton(true)
                sendCompanydata(
                    companyNameContainer,
                    companyEmailContainer,
                   "+994"+companyPhoneContainer,
                    companyFullNameContainer,
                    companyAddressContainer,
                    aboutCompanyContainer,
                    imageUrl,
                    categoryContainer,
                    statusContainer,
                    regionContainer,
                    authHeader,
                    id
                )

        }
        bindingCompanyBinding.myCompanyUpdateProfilePhoto.setOnClickListener {
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
                openGallery()
            }
        }


    }


    fun sendCompanydata(
        userFullName: String,
        email: String,
        mobileNumber: String,
        companyName: String,
        companyAddress: String,
        companyAbout: String,
        imagePath: String?,
        companyCategoryId: String,
        companyStatusId: String,
        companyRegionId: String,
        token: String,
        userId: Int
    ) {
        val photo: MultipartBody.Part? = if (imagePath != null) {
            val file = File(imagePath)
            val reqFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("Photos", file.name, reqFile)
        } else {
            null
        }
        val companyUsername: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), userFullName)
        val companyemail: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val companyNumber: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), mobileNumber)
        val address: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyAddress)
        val name: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), companyName)
        val about: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), companyAbout)
        val categoryid: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyCategoryId)
        val regionId: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyRegionId)
        val statusId: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyStatusId)


        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(UpdateAPI::class.java)

        compositeDisposable.add(
            retrofit.companyUpdateMethod(
                companyUsername,
                companyemail,
                companyNumber,
                name,
                address,
                about,
                photo,
                statusId,
                categoryid,
                regionId,
                token,
                userId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,
                    { throwable ->
                        if (throwable.message!!.contains("HTTP 409")) {
                            Toast.makeText(
                                this,
                                getString(R.string.http409Stringupdate),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val text = getString(R.string.infosWrong)
                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                            println("error: " + throwable.toString())
                        }
                        showProgressButton(false)
                    })
        )
    }

    private fun handleResponse(response: UpdateResponse) {
        println("Response: " + response.isSuccess)
        Toast.makeText(this, getString(R.string.saveSuccesfull), Toast.LENGTH_SHORT).show()
        onBackPressed()
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted, proceed with accessing gallery
                openGallery()
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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
            val selectedImageUri = data?.data
            val imagePath = selectedImageUri?.let { getRealPathFromURI(it) }
            if (imagePath != null) {
                val compressedBitmap = compressImageFile(imagePath)
                bindingCompanyBinding.companyUpdatePhotoURLEditText.setText(imagePath)
                bindingCompanyBinding.myCompanyUpdateProfilePhoto.setImageBitmap(compressedBitmap)
                if (compressedBitmap != null) {
                    val compressedImagePath = saveCompressedBitmapToFile(compressedBitmap)
                    bindingCompanyBinding.companyUpdatePhotoURLEditText.setText(compressedImagePath)
                    println("CompressedImagePath: " + compressedImagePath)
                }
                println(imagePath)
            }
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

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    private fun showBottomSheetDialog() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerViewCategories: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewCategories)
        recyclerViewCategories.setHasFixedSize(true)
        recyclerViewCategories.setLayoutManager(LinearLayoutManager(this))

        job = repository.getAllCategories().onEach { categories ->
            println("1")
            categoryAdapter = CategoryAdapter(categories)
            recyclerViewCategories.adapter = categoryAdapter
            categoryAdapter.setChanged(categories)
            categoryAdapter.setOnItemClickListener { category ->
                categoryId = category.category.categoryId.toString()
                bindingCompanyBinding.businessAccountUpdateCategoryEditText.setText(category.category.categoryName)
                isCategoryChanged = true
                dialog.dismiss()
            }
        }.catch {

        }.launchIn(lifecycleScope)

        dialog.show()
    }

    @SuppressLint("MissingInflatedId")
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
                    bindingCompanyBinding.businessAccountUpdateRegionEditText.setText(region.regionName)
                    regionId = region.regionId.toString()
                    isRegionChanged = false
                    dialog.dismiss()
                }
            }.catch { thorawable->
                println("Error 2: "+ thorawable.message)
            }.launchIn(lifecycleScope)
        dialog.show()

    }

    private fun cancelJob() {
        job?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelJob()
    }

    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetDialogStatus() {
        val appDatabase = AppDatabase.getDatabase(applicationContext)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_status, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerViewStatus: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewStatus)
        recyclerViewStatus.setHasFixedSize(true)
        recyclerViewStatus.setLayoutManager(LinearLayoutManager(this))

        job = appDatabase.statusDao().getAllMode().onEach { status ->
            println("4")
            statusAdapter = StatusAdapter(status)
            recyclerViewStatus.adapter = statusAdapter
            statusAdapter.setChanged(status)
            statusAdapter.setOnItemClickListener { status ->
                bindingCompanyBinding.companyUpdateStatusEditText.setText(status.statusName)
                statusId = status.statusId.toString()
                isStatusChanged = true
                dialog.dismiss()
            }
        }.catch { throwable ->
            println("MyTestStatus: $throwable")
        }.launchIn(lifecycleScope)
        dialog.show()
    }

    private fun showProgressButton(show: Boolean) {
        if (show) {
            bindingCompanyBinding.savedUpdatesBtnCompany.apply {
                isEnabled = false
                text = getString(R.string.savingChange)  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            bindingCompanyBinding.savedUpdatesBtnCompany.apply {
                isEnabled = true
                text = getString(R.string.saveChange)
                // Restore original background, text color, etc., if modified
            }
        }
    }
}