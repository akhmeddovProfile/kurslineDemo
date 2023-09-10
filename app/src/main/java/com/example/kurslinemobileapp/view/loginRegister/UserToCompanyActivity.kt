package com.example.kurslinemobileapp.view.loginRegister

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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kurslinemobileapp.R
import com.example.kurslinemobileapp.adapter.CategoryAdapter
import com.example.kurslinemobileapp.adapter.ModeAdapter
import com.example.kurslinemobileapp.adapter.RegionAdapter
import com.example.kurslinemobileapp.adapter.StatusAdapter
import com.example.kurslinemobileapp.api.companyData.CompanyDatasAPI
import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.api.register.RegisterCompanyResponse
import com.example.kurslinemobileapp.api.register.UserToCompanyResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.service.Room.category.MyRepositoryForCategory
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_company.*
import kotlinx.android.synthetic.main.activity_user_register.*
import kotlinx.android.synthetic.main.activity_user_to_company.*
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

class UserToCompanyActivity : AppCompatActivity() {
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var regionAdapter: RegionAdapter
    private lateinit var statusAdapter: StatusAdapter
    var compositeDisposable = CompositeDisposable()
    lateinit var companyPhotoUrl : String
    //Gallery
    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image
    private var reenterPermissionRequestCode = 0
    companion object {
        const val PICK_IMAGE_REQUESTT = 1 // You can use any integer value
        private const val REQUEST_CODE_PERMISSIONS = 2 // Another unique integer value
    }
    private var block: Boolean = true
    lateinit var editor: SharedPreferences.Editor
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var categoryId: String
    lateinit var statusId: String
    lateinit var regionId:String

    private var job: Job? = null
    private lateinit var repository: MyRepositoryForCategory

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_to_company)
        repository = MyRepositoryForCategory(
            AppDatabase.getDatabase(this).categoryDao(),
            AppDatabase.getDatabase(this).subCategoryDao()
        )
        sharedPreferences = getSharedPreferences(Constant.sharedkeyname, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        val userId = sharedPreferences.getInt("userID",0)
        val token = sharedPreferences.getString("USERTOKENNN","")
        val authHeader = "Bearer $token"
        println("userid" + userId)
        println("token:"+authHeader)

        userToCompanyNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val name = s.toString().trim()
                val characterCount = name.length

                if (characterCount < 3 || characterCount > 50) {
                    characterCountTextViewuserToCompanyName.visibility=View.VISIBLE
                    userToCompanyNameContainer.error = getString(R.string.busienssNameCharacterCount)
                } else {
                    characterCountTextViewuserToCompanyName.visibility=View.GONE
                    userToCompanyNameContainer.error = null
                }

                characterCountTextViewuserToCompanyName.text = "$characterCount / 50"
            }
        })

        userToCompanyAddressEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val name = s.toString().trim()
                val characterCount = name.length

                if (characterCount < 3 || characterCount > 200) {
                    userToCompanyAddressContainer.error = getString(R.string.addressCharacterCount)
                } else {
                    userToCompanyAddressContainer.error = null
                }

                characterCountTextViewuserToCompanyAddress.text = "$characterCount / 200"
            }
        })

        userToCompanyAboutEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val name = s.toString().trim()
                val characterCount = name.length

                if (characterCount < 3 || characterCount > 1500) {
                    userToCompanyAboutContainer.error = getString(R.string.aboutCharacterCount)
                } else {
                    userToCompanyAboutContainer.error = null
                }

                characterCountTextViewuserToCompanyAbout.text = "$characterCount / 1500"
            }
        })

        userToCompanyCategoryEditText.setOnClickListener {
            showBottomSheetDialog()
        }

        userToCompanyStatusEditText.setOnClickListener {
            showBottomSheetDialogStatus()
        }

        userToCompanyRegionEditText.setOnClickListener {
            showBottomSheetDialogRegions()
        }
        categoryId = ""
        statusId = ""
        companyPhotoUrl = ""
        regionId = ""

        userToCompanyRegisterBtn.setOnClickListener{
            block = true
            val companyNameContainer = userToCompanyNameEditText.text.toString().trim()
            val companyAddressContainer = userToCompanyAddressEditText.text.toString().trim()
            val photoContainer = userToCompanyPhotoEditText.text.toString().trim()
            val companyStatusContainer = statusId
            val companyCategoryContainer = categoryId
            val regionContainer = regionId
            val statusContainer = userToCompanyStatusEditText.text.toString().trim()
            val categoryContainer = userToCompanyCategoryEditText.text.toString().trim()
            val aboutCompanyContainer = userToCompanyAboutEditText.text.toString().trim()
            val region = userToCompanyRegionEditText.text.toString().trim()

            if(companyNameContainer.isEmpty()){
                userToCompanyNameEditText.requestFocus()
                userToCompanyNameEditText.error = "Company Name is not be empty"
                block  = false
            }
            if(companyAddressContainer.isEmpty()){
                userToCompanyAddressEditText.requestFocus()
                userToCompanyAddressEditText.error = "Address is not be empty"
                block  = false
            }
            if(statusContainer.isEmpty()){
                userToCompanyStatusEditText.requestFocus()
                userToCompanyStatusEditText.error = "Status is not be empty"
                block  = false
            }
            if(categoryContainer.isEmpty()){
                userToCompanyCategoryEditText.requestFocus()
                userToCompanyCategoryEditText.error = "Category is not be empty"
                block  = false
            }
            if(aboutCompanyContainer.isEmpty()){
                userToCompanyAboutEditText.requestFocus()
                userToCompanyAboutEditText.error ="Company about is not be empty"
                block  = false
            }
            if(photoContainer.isEmpty()){
                userToCompanyPhotoEditText.requestFocus()
                userToCompanyPhotoEditText.error ="Company photo is not be empty"
                block  = false
            }
            if(region.isEmpty()){
                userToCompanyRegionEditText.requestFocus()
                userToCompanyRegionEditText.error ="Region is not be empty"
                block  = false
            }else{
                showProgressButton(true)
                sendCompanydata(companyNameContainer, companyCategoryContainer, companyAddressContainer, aboutCompanyContainer, companyPhotoUrl, companyStatusContainer,regionContainer, authHeader, userId)
            }


        }
        userToCompanyPhotoEditText.setOnClickListener {
            if(!checkPermission()){
                checkAndRequestPermissions()
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
        companyName: String,
        companyCategoryId: String,
        companyAddress: String,
        companyAbout: String,
        imagePath: String,
        companyStatusId:String,
        companyRegionId:String,
        token:String,userId:Int
    ) {
        val file = File(imagePath)
        val reqFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val photo: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", file.name, reqFile)
        val companyame: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyName)
        val address: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyAddress)
        val about: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), companyAbout)
        val categoryid: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyCategoryId)
        val statusId: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyStatusId)
        val regionId : RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyRegionId)


        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(RegisterAPI::class.java)

        compositeDisposable.add(
            retrofit.userToCompanyCreate(companyame,categoryid,address,about,photo,statusId,regionId,token,userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,
                    { throwable ->
                        println(throwable.message)
                        if (throwable.message!!.contains("HTTP 409")){
                            Toast.makeText(this,getString(R.string.http409String),Toast.LENGTH_SHORT).show()
                        }else{
                            val text = getString(R.string.infosWrong)
                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                        }
                        showProgressButton(false)
                    })
        )
    }

    private fun handleResponse(response: UserToCompanyResponse) {
        println("Response: " + response.isSuccess)
        val intent = Intent(this@UserToCompanyActivity, SuccessActivity::class.java)
        startActivity(intent)
        finish()
    }


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
                userToCompanyPhotoEditText.setText(imagePath)
                if(compressedBitmap!=null){
                    val compressedImagePath = saveCompressedBitmapToFile(compressedBitmap)
                    userToCompanyPhotoEditText.setText("Şəkil seçildi!")
                    companyPhotoUrl = compressedImagePath!!
                    println("CompressedImagePath"+compressedImagePath)
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Compress and save the bitmap as JPEG with 80% quality
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

        job=repository.getAllCategories().onEach { categories ->
            println("1")
            categoryAdapter = CategoryAdapter(categories)
            recyclerViewCategories.adapter = categoryAdapter
            categoryAdapter.setChanged(categories)
            categoryAdapter.setOnItemClickListener { category ->
                categoryId = category.category.categoryId.toString()
                userToCompanyCategoryEditText.setText(category.category.categoryName)
                dialog.dismiss()
            }
        }.catch {

        }.launchIn(lifecycleScope)

        dialog.show()
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
        job=appDatabase.statusDao().getAllMode()
            .onEach { status ->
                println("4")
                statusAdapter = StatusAdapter(status)
                recyclerViewStatus.adapter = statusAdapter
                statusAdapter.setChanged(status)
                statusAdapter.setOnItemClickListener { status ->
                    statusId = status.statusId.toString()
                    userToCompanyStatusEditText.setText(status.statusName)
                    dialog.dismiss()
                }
            }.catch { throwable ->
                println("MyTestStatus: $throwable")
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

        job= appDatabase.regionDao().getAllRegions()
            .onEach {reg->
                println("2")
                regionAdapter = RegionAdapter(reg)
                recyclerviewRegions.adapter = regionAdapter
                regionAdapter.setChanged(reg)
                regionAdapter.setOnItemClickListener { region ->
                    //   companyRegionEditText.setText(region.regionName)
                    regionId = region.regionId.toString()
                    userToCompanyRegionEditText.setText(region.regionName)
                    dialog.dismiss()
                }

            }.catch {thorawable->
                println("Error message 2: "+ thorawable.message)
            }.launchIn(lifecycleScope)

        dialog.show()
    }

    private fun showProgressButton(show: Boolean) {
        if (show) {
            userToCompanyRegisterBtn.apply {
                isEnabled = false
                text = getString(R.string.registerContinue)  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            userToCompanyRegisterBtn.apply {
                isEnabled = true
                text = getString(R.string.createBusiness)
                // Restore original background, text color, etc., if modified
            }
        }
    }
}

