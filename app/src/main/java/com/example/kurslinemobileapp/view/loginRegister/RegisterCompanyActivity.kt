package com.example.kurslinemobileapp.view.loginRegister

import android.Manifest.permission.*
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
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.app.kurslinemobileapp.databinding.ActivityRegisterCompanyBinding
import com.example.kurslinemobileapp.adapter.CategoryAdapter
import com.example.kurslinemobileapp.adapter.ModeAdapter
import com.example.kurslinemobileapp.adapter.RegionAdapter
import com.example.kurslinemobileapp.adapter.StatusAdapter
import com.example.kurslinemobileapp.api.register.RegisterAPI
import com.example.kurslinemobileapp.api.register.RegisterCompanyResponse
import com.example.kurslinemobileapp.service.Constant
import com.example.kurslinemobileapp.service.Constant.sharedkeyname
import com.example.kurslinemobileapp.service.RetrofitService
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.service.Room.category.MyRepositoryForCategory
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import java.util.regex.Pattern


class RegisterCompanyActivity : AppCompatActivity() {
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var regionAdapter: RegionAdapter
    private lateinit var modeAdapter: ModeAdapter
    private lateinit var statusAdapter: StatusAdapter
    var compositeDisposable = CompositeDisposable()
    lateinit var companyPhotoUrl : String
    private lateinit var repository: MyRepositoryForCategory
    private var job: Job? = null
    private lateinit var bindingRegComp:ActivityRegisterCompanyBinding
    val MAX_IMAGE_WIDTH = 800 // Maximum width for the compressed image
    val MAX_IMAGE_HEIGHT = 600 // Maximum height for the compressed image

    companion object {
        const val PICK_IMAGE_REQUEST = 1 // You can use any integer value
        private const val REQUEST_CODE_PERMISSIONS = 2 // Another unique integer value
    }

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
            val result1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {

           // Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    //local data save
    private var block: Boolean = true
    lateinit var editor: SharedPreferences.Editor
    private lateinit var sharedPreferences: SharedPreferences

    //Variable
    lateinit var name: String
    lateinit var companyEmail: String
    lateinit var companyPassword: String
    lateinit var companyFullName: String
    lateinit var companyRegion: String
    lateinit var companyAddress: String
    lateinit var companyPhone: String
    lateinit var companyMode: String
    lateinit var companyStatus: String
    lateinit var companyCategory: String
    lateinit var aboutCompany: String
    lateinit var categoryId: String
    lateinit var statusId: String
    lateinit var regionId:String

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingRegComp=ActivityRegisterCompanyBinding.inflate(layoutInflater)
        val view=bindingRegComp.root
        setContentView(view)
        //setContentView(R.layout.activity_register_company)


        sharedPreferences = getSharedPreferences(sharedkeyname, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        repository = MyRepositoryForCategory(
            AppDatabase.getDatabase(this).categoryDao(),
            AppDatabase.getDatabase(this).subCategoryDao()
        )



        bindingRegComp.companyFullNameEditText.addTextChangedListener(object : TextWatcher {
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
                    bindingRegComp.companyFullNameContainer.error = getString(R.string.nameCharacterCount)
                } else {
                    bindingRegComp.companyFullNameContainer.error = null
                }

                bindingRegComp.characterCountTextViewcmpname.text = "$characterCount / 50"
            }
        })

        bindingRegComp.CompanypasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString().trim()
                val isValid = isPasswordValid(password)

                if (!isValid) {
                    bindingRegComp.companyPasswordContainer.error = getString(R.string.passwordCount)
                } else {
                    bindingRegComp.companyPasswordContainer.error = null
                }
            }
        })

        bindingRegComp.companyFullNameEditText.addTextChangedListener(object : TextWatcher {
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
                    bindingRegComp.companyFullNameContainer.error = getString(R.string.busienssNameCharacterCount)
                } else {
                    bindingRegComp.companyFullNameContainer.error = null
                }

                bindingRegComp.characterCountTextViewcmpname.text = "$characterCount / 50"
            }
        })


        bindingRegComp.companyCategoryEditText.setOnClickListener {
            showBottomSheetDialog()
        }
        bindingRegComp.compantStatusEditText.setOnClickListener {
            Log.d("MyTag","Test")
            println("TestStatus")
            showBottomSheetDialogStatus()
        }
        categoryId = ""
        statusId = ""
        regionId = ""
        companyPhotoUrl = ""

        bindingRegComp.createBusinessAccountBtn.setOnClickListener {
            block = true
            //val companyNameContainer = companyNameEditText.text.toString().trim()
            val companyEmailContainer = bindingRegComp.companyEmailEditText.text.toString().trim()
            val companyPasswordContainer = bindingRegComp.CompanypasswordEditText.text.toString().trim()
            val companyFullNameContainer = bindingRegComp.companyFullNameEditText.text.toString().trim()
            val companyPhoneContainer = bindingRegComp.companyPhoneEditText.text.toString().trim()
            val statusContainer = bindingRegComp.compantStatusEditText.text.toString().trim()
            val categoryContainer = bindingRegComp.companyCategoryEditText.text.toString().trim()
            //  val companyModeContainer = companyModeEditText.text.toString().trim()
            val companyStatusContainer = statusId
            val companyCategoryContainer = categoryId
            //val companyRegionContainer = regionId
            val aboutCompanyContainer = bindingRegComp.aboutCompanyEditText.text.toString().trim()
            val companyPhotoContainer = bindingRegComp.companyPhoto.text.toString().trim()

            if(companyEmailContainer.isEmpty()){
                bindingRegComp.companyEmailEditText.requestFocus()
                bindingRegComp.companyEmailEditText.error = "Email address is not be empty"
                block = false
            }
            if(companyPasswordContainer.isEmpty()){
                bindingRegComp.CompanypasswordEditText.requestFocus()
                bindingRegComp.CompanypasswordEditText.error = "Password is not be empty"
                block = false
            }
            if(companyFullNameContainer.isEmpty()){
                bindingRegComp.companyFullNameEditText.requestFocus()
                bindingRegComp.companyFullNameEditText.error = "Company Name is not be empty"
                block  = false
            }
            if(companyPhoneContainer.isEmpty()){
                bindingRegComp.companyPhoneEditText.requestFocus()
                bindingRegComp.companyPhoneEditText.error = "Phone is not be empty"
                block  = false
            }
         if(statusContainer.isEmpty()){
             bindingRegComp.compantStatusEditText.requestFocus()
             bindingRegComp.compantStatusEditText.error = "Status is not be empty"
                block  = false
            }
            if(categoryContainer.isEmpty()){
                bindingRegComp.companyCategoryEditText.requestFocus()
                bindingRegComp.companyCategoryEditText.error = "Category is not be empty"
                block  = false
            }
            if(aboutCompanyContainer.isEmpty()){
                bindingRegComp.aboutCompanyEditText.requestFocus()
                bindingRegComp.aboutCompanyEditText.error ="Company about is not be empty"
                block  = false
            }
            if(companyPhotoContainer.isEmpty()){
                bindingRegComp.companyPhoto.requestFocus()
                bindingRegComp.companyPhoto.error ="Company photo is not be empty"
                block  = false
            }else{
                showProgressButton(true)
                sendCompanydata(companyEmailContainer,"+994"+companyPhoneContainer,companyPasswordContainer,companyFullNameContainer ,aboutCompanyContainer,companyCategoryContainer,companyPhotoUrl,companyStatusContainer)
            }
        }
        bindingRegComp.companyPhoto.setOnClickListener {
            if(!checkPermission()){
                checkAndRequestPermissions()
         /*       val miuiVersion = BuildProperties.getMIUIVersion()
                if (miuiVersion == "14.0.3") {
                    showMIUIExplanationDialog() // Show a custom explanation
                }*/
                requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE) // Launch permission request directly
            }
            else{
                openGallery()
            }
           // launchGalleryIntent()
        }
    }


    fun sendCompanydata(
        email: String,
        mobileNumber: String,
        password: String,
        companyName: String,
        companyAbout: String,
        companyCategoryId: String,
        imagePath: String,
        companyStatusId:String) {
        val file = File(imagePath)
        val reqFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val photo: MultipartBody.Part =
            MultipartBody.Part.createFormData("photos", file.name, reqFile)
        val companyemail: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
        val companyNumber: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), mobileNumber)
        val companyPassword: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), password)
        val name: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), companyName)
        val about: RequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), companyAbout)
        val categoryid: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyCategoryId)

        val statusId: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), companyStatusId)


        compositeDisposable = CompositeDisposable()
        val retrofit =
            RetrofitService(Constant.BASE_URL).retrofit.create(RegisterAPI::class.java)

        compositeDisposable.add(
            retrofit.createCompany(name,about,companyemail,companyNumber,statusId,categoryid,companyPassword,photo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,
                    { throwable ->
                        println(throwable.message)
                        if (throwable.message!!.contains("HTTP 409")){
                            Toast.makeText(this,getString(R.string.http409Stringupdate),Toast.LENGTH_SHORT).show()
                        }else{
                            val text = getString(R.string.infosWrong)
                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                        }
                        showProgressButton(false)
                    })
        )
    }

    private fun handleResponse(response: RegisterCompanyResponse) {
        println("Response: " + response.isSuccess)
        val intent = Intent(this@RegisterCompanyActivity, SuccessActivity::class.java)
        startActivity(intent)
        finish()
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted, proceed with accessing gallery
                openGallery()
            } else {
                // Request permission
                requestPermissionLauncher.launch(READ_MEDIA_IMAGES)
            }
        } else {
           // showPermissionMessageForOlderDevices()
            // Handle devices with API level lower than 31
            // You might want to show a message or handle it differently
        }
    }

    private fun showPermissionMessageForOlderDevices() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("To access the gallery, you need to grant storage permission.")
            .setPositiveButton("Grant Permission") { dialog, _ ->
                requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            val imagePath = selectedImageUri?.let { getRealPathFromURI(it) }
            if (imagePath != null) {
                val compressedBitmap = compressImageFile(imagePath)
                bindingRegComp.companyPhoto.setText(imagePath)
                if(compressedBitmap!=null){
                    val compressedImagePath = saveCompressedBitmapToFile(compressedBitmap)
                    companyPhotoUrl = compressedImagePath!!
                    bindingRegComp.companyPhoto.setText("Şəkil seçildi!")
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

    override fun onPause() {
        //getValues()
        super.onPause()
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

        job=repository.getAllCategories().onEach { categories ->
            println("1")
            categoryAdapter = CategoryAdapter(categories)
            recyclerViewCategories.adapter = categoryAdapter
            categoryAdapter.setChanged(categories)
            categoryAdapter.setOnItemClickListener { category ->
                categoryId = category.category.categoryId.toString()
                bindingRegComp.companyCategoryEditText.setText(category.category.categoryName)
                dialog.dismiss()
            }
        }.catch { throwable ->
            println("MyTests: $throwable")
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
                    dialog.dismiss()
                }

            }.catch {thorawable->
                println("Error message 2: "+ thorawable.message)
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
    private fun showBottomSheetDialogMode() {
        val appDatabase = AppDatabase.getDatabase(applicationContext)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_mode, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)
        val recyclerViewMode: RecyclerView =
            bottomSheetView.findViewById(R.id.recyclerViewMode)
        recyclerViewMode.setHasFixedSize(true)
        recyclerViewMode.setLayoutManager(LinearLayoutManager(this))
        compositeDisposable = CompositeDisposable()

        job=appDatabase.modeDao().getAllMode()
            .onEach { mode ->
                println("3")
                modeAdapter = ModeAdapter(mode)
                recyclerViewMode.adapter = modeAdapter
                modeAdapter.setChanged(mode)
                modeAdapter.setOnItemClickListener { mode ->
                   // companyModeEditText.setText(mode.modeName)
                    dialog.dismiss()
                }
            }.catch { throwable ->
                println("MyTestMode: $throwable")
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
                    bindingRegComp.compantStatusEditText.setText(status.statusName)
                    statusId = status.statusId.toString()
                    dialog.dismiss()
                }
            }.catch { throwable ->
                println("MyTestStatus: $throwable")
            }.launchIn(lifecycleScope)
        dialog.show()
    }

    private fun showProgressButton(show: Boolean) {
        if (show) {
            bindingRegComp.createBusinessAccountBtn.apply {
                isEnabled = false
                text = getString(R.string.registerContinue)  // Set empty text or loading indicator text
                // Add loading indicator drawable or ProgressBar if needed
            }
        } else {
            bindingRegComp.createBusinessAccountBtn.apply {
                isEnabled = true
                text = getString(R.string.registerString)
                // Restore original background, text color, etc., if modified
            }
        }
    }
    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        )
        return passwordPattern.matcher(password).matches()
    }
}
